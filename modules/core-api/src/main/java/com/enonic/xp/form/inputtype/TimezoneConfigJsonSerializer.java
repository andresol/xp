package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public abstract class TimezoneConfigJsonSerializer<T extends TimezoneConfig>
    extends AbstractInputTypeConfigJsonSerializer<T>
{

    @Override
    public JsonNode serializeConfig( final T config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        jsonConfig.put( "withTimezone", config.isWithTimezone() );
        return jsonConfig;
    }

    @Override
    public T parseConfig( final JsonNode inputTypeConfigNode )
    {
        final T.Builder builder = T.newTimezoneConfig();
        final JsonNode withTimeZoneNode = inputTypeConfigNode.get( "withTimezone" );
        if ( withTimeZoneNode != null && !withTimeZoneNode.isNull() )
        {
            builder.withTimezone( Boolean.valueOf( withTimeZoneNode.booleanValue() ) );
        }
        return (T) builder.build();
    }

    public void parseTimezone( final JsonNode inputTypeConfigNode, T.Builder builder )
    {
        final JsonNode withTimeZoneNode = inputTypeConfigNode.get( "withTimezone" );
        if ( withTimeZoneNode != null && !withTimeZoneNode.isNull() )
        {
            builder.withTimezone( Boolean.valueOf( withTimeZoneNode.booleanValue() ) );
        }
    }
}