package com.enonic.wem.core.content.type.form;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.content.type.form.HierarchicalFormItem;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.Layout;
import com.enonic.wem.api.content.type.form.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.form.SubTypeReference;
import com.enonic.wem.api.content.type.form.inputtype.BaseInputType;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.form.inputtype.InputTypeConfigJsonSerializer;
import com.enonic.wem.core.content.type.form.inputtype.InputTypeJsonSerializer;

import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;

class ComponentJsonSerializer
    extends AbstractJsonSerializer<FormItem>
{
    private static final String TYPE = "type";

    private static final String NAME = "name";

    private static final String REFERENCE = "reference";

    public static final String LABEL = "label";

    public static final String IMMUTABLE = "immutable";

    public static final String CUSTOM_TEXT = "customText";

    public static final String HELP_TEXT = "helpText";

    public static final String OCCURRENCES = "occurrences";

    public static final String INDEXED = "indexed";

    public static final String VALIDATION_REGEXP = "validationRegexp";

    public static final String INPUT_TYPE_CONFIG = "inputTypeConfig";

    public static final String INPUT_TYPE = "inputType";

    public static final String LAYOUT_TYPE = "layoutType";

    public static final String ITEMS = "items";

    public static final String SUB_TYPE_CLASS = "subTypeClass";

    private final InputTypeJsonSerializer inputTypeSerializer = new InputTypeJsonSerializer();

    private final InputTypeConfigJsonSerializer inputTypeConfigSerializer = new InputTypeConfigJsonSerializer();

    private final OccurrencesJsonSerializer occurrencesJsonSerializer = new OccurrencesJsonSerializer();

    private final ComponentsJsonSerializer componentsJsonSerializer;

    public ComponentJsonSerializer( final ComponentsJsonSerializer componentsJsonSerializer )
    {
        this.componentsJsonSerializer = componentsJsonSerializer;
    }

    @Override
    protected JsonNode serialize( final FormItem formItem, final ObjectMapper objectMapper )
    {
        if ( formItem instanceof FormItemSet )
        {
            return serializeComponentSet( (FormItemSet) formItem, objectMapper );
        }
        else if ( formItem instanceof Layout )
        {
            return serializeLayout( (Layout) formItem, objectMapper );
        }
        else if ( formItem instanceof Input )
        {
            return serializeInput( (Input) formItem, objectMapper );
        }
        else if ( formItem instanceof SubTypeReference )
        {
            return serializeReference( (SubTypeReference) formItem, objectMapper );
        }
        return NullNode.getInstance();
    }

    private JsonNode serializeInput( final Input input, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, Input.class.getSimpleName() );
        jsonObject.put( NAME, input.getName() );
        jsonObject.put( LABEL, input.getLabel() );
        jsonObject.put( IMMUTABLE, input.isImmutable() );
        jsonObject.put( OCCURRENCES, occurrencesJsonSerializer.serialize( input.getOccurrences(), objectMapper ) );
        jsonObject.put( INDEXED, input.isIndexed() );
        jsonObject.put( CUSTOM_TEXT, input.getCustomText() );
        jsonObject.put( VALIDATION_REGEXP, input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null );
        jsonObject.put( HELP_TEXT, input.getHelpText() );
        jsonObject.put( INPUT_TYPE, inputTypeSerializer.serialize( (BaseInputType) input.getInputType(), objectMapper ) );
        if ( input.getInputType().requiresConfig() && input.getInputTypeConfig() != null )
        {
            final JsonNode inputTypeNode =
                input.getInputType().getInputTypeConfigJsonGenerator().serialize( input.getInputTypeConfig(), objectMapper );
            jsonObject.put( INPUT_TYPE_CONFIG, inputTypeNode );
        }
        return jsonObject;
    }

    private JsonNode serializeComponentSet( final FormItemSet componentSet, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, FormItemSet.class.getSimpleName() );
        jsonObject.put( NAME, componentSet.getName() );
        jsonObject.put( LABEL, componentSet.getLabel() );
        jsonObject.put( IMMUTABLE, componentSet.isImmutable() );
        jsonObject.put( OCCURRENCES, occurrencesJsonSerializer.serialize( componentSet.getOccurrences(), objectMapper ) );
        jsonObject.put( CUSTOM_TEXT, componentSet.getCustomText() );
        jsonObject.put( HELP_TEXT, componentSet.getHelpText() );
        jsonObject.put( ITEMS, componentsJsonSerializer.serialize( componentSet.getFormItems(), objectMapper ) );
        return jsonObject;
    }

    private JsonNode serializeLayout( final Layout layout, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, Layout.class.getSimpleName() );
        jsonObject.put( LAYOUT_TYPE, FieldSet.class.getSimpleName() );

        if ( layout instanceof FieldSet )
        {
            generateFieldSet( (FieldSet) layout, jsonObject, objectMapper );
        }
        return jsonObject;
    }

    private void generateFieldSet( final FieldSet fieldSet, final ObjectNode jsonObject, final ObjectMapper objectMapper )
    {
        jsonObject.put( LABEL, fieldSet.getLabel() );
        jsonObject.put( NAME, fieldSet.getName() );
        jsonObject.put( ITEMS, componentsJsonSerializer.serialize( fieldSet.getFormItems(), objectMapper ) );
    }

    private JsonNode serializeReference( final SubTypeReference subTypeReference, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( TYPE, SubTypeReference.class.getSimpleName() );
        jsonObject.put( NAME, subTypeReference.getName() );
        jsonObject.put( REFERENCE, subTypeReference.getSubTypeQualifiedName().toString() );
        jsonObject.put( SUB_TYPE_CLASS, subTypeReference.getSubTypeClass().getSimpleName() );
        return jsonObject;
    }

    public FormItem parse( final JsonNode componentNode )
    {
        final String componentType = JsonParserUtil.getStringValue( TYPE, componentNode );

        final FormItem formItem;

        if ( componentType.equals( Input.class.getSimpleName() ) )
        {
            formItem = parseInput( componentNode );
        }
        else if ( componentType.equals( FormItemSet.class.getSimpleName() ) )
        {
            formItem = parseComponentSet( componentNode );
        }
        else if ( componentType.equals( Layout.class.getSimpleName() ) )
        {
            formItem = parseLayout( componentNode );
        }
        else if ( componentType.equals( SubTypeReference.class.getSimpleName() ) )
        {
            formItem = parseSubTypeReference( componentNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown FormItemType: " + componentType );
        }

        return formItem;
    }

    private HierarchicalFormItem parseInput( final JsonNode componentNode )
    {
        final Input.Builder builder = newInput();
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode ) );
        builder.label( JsonParserUtil.getStringValue( LABEL, componentNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( IMMUTABLE, componentNode ) );
        builder.helpText( JsonParserUtil.getStringValue( HELP_TEXT, componentNode ) );
        builder.customText( JsonParserUtil.getStringValue( CUSTOM_TEXT, componentNode ) );
        parseValidationRegexp( builder, componentNode );

        parseOccurrences( builder, componentNode.get( OCCURRENCES ) );
        parseInputType( builder, componentNode.get( INPUT_TYPE ) );
        parseInputTypeConfig( builder, componentNode.get( INPUT_TYPE_CONFIG ) );

        return builder.build();
    }

    private HierarchicalFormItem parseComponentSet( final JsonNode componentNode )
    {
        final FormItemSet.Builder builder = newFormItemSet();
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode ) );
        builder.label( JsonParserUtil.getStringValue( LABEL, componentNode, null ) );
        builder.immutable( JsonParserUtil.getBooleanValue( IMMUTABLE, componentNode ) );
        builder.helpText( JsonParserUtil.getStringValue( HELP_TEXT, componentNode ) );
        builder.customText( JsonParserUtil.getStringValue( CUSTOM_TEXT, componentNode ) );

        parseOccurrences( builder, componentNode.get( OCCURRENCES ) );

        final FormItems formItems = componentsJsonSerializer.parse( componentNode.get( ITEMS ) );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private FormItem parseLayout( final JsonNode componentNode )
    {
        final String layoutType = JsonParserUtil.getStringValue( LAYOUT_TYPE, componentNode );
        if ( layoutType.equals( FieldSet.class.getSimpleName() ) )
        {
            return parseFieldSet( componentNode );
        }
        else
        {
            throw new JsonParsingException( "Unknown layoutType: " + layoutType );
        }
    }

    private FormItem parseFieldSet( final JsonNode componentNode )
    {
        final FieldSet.Builder builder = newFieldSet();
        builder.label( JsonParserUtil.getStringValue( LABEL, componentNode, null ) );
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode, null ) );

        final FormItems formItems = componentsJsonSerializer.parse( componentNode.get( ITEMS ) );
        for ( FormItem formItem : formItems.iterable() )
        {
            builder.add( formItem );
        }

        return builder.build();
    }

    private HierarchicalFormItem parseSubTypeReference( final JsonNode componentNode )
    {
        final SubTypeReference.Builder builder = SubTypeReference.newSubTypeReference();
        builder.name( JsonParserUtil.getStringValue( NAME, componentNode ) );
        builder.subType( new SubTypeQualifiedName( JsonParserUtil.getStringValue( REFERENCE, componentNode ) ) );
        builder.type( JsonParserUtil.getStringValue( SUB_TYPE_CLASS, componentNode ) );
        return builder.build();
    }

    private void parseValidationRegexp( final Input.Builder builder, final JsonNode inputNode )
    {
        final String validationRegexp = JsonParserUtil.getStringValue( VALIDATION_REGEXP, inputNode, null );
        if ( validationRegexp != null )
        {
            builder.validationRegexp( validationRegexp );
        }
    }

    private void parseOccurrences( final Input.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( occurrencesJsonSerializer.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private void parseOccurrences( final FormItemSet.Builder builder, final JsonNode occurrencesNode )
    {
        if ( occurrencesNode != null )
        {
            builder.occurrences( occurrencesJsonSerializer.parse( occurrencesNode ) );
        }
        else
        {
            builder.multiple( false );
        }
    }

    private void parseInputTypeConfig( final Input.Builder builder, final JsonNode inputTypeConfigNode )
    {
        if ( inputTypeConfigNode != null )
        {
            builder.inputTypeConfig( inputTypeConfigSerializer.parse( inputTypeConfigNode ) );
        }
    }

    private void parseInputType( final Input.Builder builder, final JsonNode inputTypeNode )
    {
        if ( inputTypeNode != null )
        {
            builder.type( inputTypeSerializer.parse( inputTypeNode ) );
        }
    }
}
