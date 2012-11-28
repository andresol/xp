package com.enonic.wem.core.content.type;


import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.type.form.FormItemsXmlSerializer;

import com.enonic.cms.framework.util.JDOMUtil;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

public class ContentTypeXmlSerializer
    implements ContentTypeSerializer
{
    private FormItemsXmlSerializer formItemsSerializer = new FormItemsXmlSerializer();

    private boolean prettyPrint = false;

    public ContentTypeXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( ContentType type )
    {
        if ( prettyPrint )
        {
            return JDOMUtil.prettyPrintDocument( toJDomDocument( type ) );
        }
        else
        {
            return JDOMUtil.printDocument( toJDomDocument( type ) );
        }
    }

    public Document toJDomDocument( ContentType type )
    {
        final Element typeEl = new Element( "type" );
        generate( type, typeEl );
        return new Document( typeEl );
    }


    private void generate( final ContentType type, final Element typeEl )
    {
        typeEl.addContent( new Element( "name" ).setText( type.getName() ) );
        typeEl.addContent( new Element( "module" ).setText( type.getModule().getName() ) );
        typeEl.addContent( new Element( "qualifiedName" ).setText( type.getQualifiedName().toString() ) );
        typeEl.addContent( new Element( "displayName" ).setText( type.getDisplayName() ) );
        typeEl.addContent( new Element( "superType" ).setText( type.getSuperType() != null ? type.getSuperType().toString() : null ) );
        typeEl.addContent( new Element( "isAbstract" ).setText( Boolean.toString( type.isAbstract() ) ) );
        typeEl.addContent( new Element( "isFinal" ).setText( Boolean.toString( type.isFinal() ) ) );
        typeEl.addContent( formItemsSerializer.serialize( type.form().formItemIterable() ) );
    }

    public ContentType toContentType( String xml )
        throws XmlParsingException
    {
        try
        {
            Document document = JDOMUtil.parseDocument( xml );

            return parse( document.getRootElement() );
        }
        catch ( JDOMException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
        catch ( IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

    private ContentType parse( final Element contentTypeEl )
        throws IOException
    {
        final String superTypeValue = StringUtils.trimToNull( contentTypeEl.getChildText( "superType" ) );
        final QualifiedContentTypeName superType = superTypeValue != null ? new QualifiedContentTypeName( superTypeValue ) : null;
        final boolean isAbstract = Boolean.parseBoolean( contentTypeEl.getChildText( "isAbstract" ) );
        final boolean isFinal = Boolean.parseBoolean( contentTypeEl.getChildText( "isFinal" ) );

        final ContentType.Builder contentTypeBuilder = newContentType().
            name( contentTypeEl.getChildText( "name" ) ).
            module( new Module( contentTypeEl.getChildText( "module" ) ) ).
            displayName( contentTypeEl.getChildText( "displayName" ) ).
            superType( superType ).
            setAbstract( isAbstract ).
            setFinal( isFinal );

        try
        {
            final FormItems formItems = formItemsSerializer.parse( contentTypeEl );
            for ( FormItem formItem : formItems )
            {
                contentTypeBuilder.addFormItem( formItem );
            }
        }
        catch ( Exception e )
        {
            throw new XmlParsingException( "Failed to parse content type: " + JDOMUtil.printElement( contentTypeEl ), e );
        }

        return contentTypeBuilder.build();
    }
}
