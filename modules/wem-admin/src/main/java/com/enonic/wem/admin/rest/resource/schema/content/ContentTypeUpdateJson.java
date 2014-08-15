package com.enonic.wem.admin.rest.resource.schema.content;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.icon.ThumbnailJson;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.xml.XmlSerializers;

public class ContentTypeUpdateJson
{

    private final ContentTypeName contentTypeToUpdate;

    private final ContentTypeName name;

    private final ContentType contentTypeUpdate;

    private final ThumbnailJson thumbnailJson;

    @JsonCreator
    public ContentTypeUpdateJson( @JsonProperty("contentTypeToUpdate") final String contentTypeToUpdateAsString,
                                  @JsonProperty("name") final String nameAsString, @JsonProperty("config") final String config,
                                  @JsonProperty("icon") final ThumbnailJson thumbnailJson )
    {

        this.contentTypeToUpdate = ContentTypeName.from( contentTypeToUpdateAsString );
        this.name = ContentTypeName.from( nameAsString );
        this.thumbnailJson = thumbnailJson;
        this.contentTypeUpdate = parse( name, config );
    }

    private ContentType parse( final ContentTypeName name, final String config )
    {
        final ContentType.Builder builder = ContentType.newContentType().name( name );
        XmlSerializers.contentType().parse( config ).to( builder );
        return builder.build();
    }

    @JsonIgnore
    public ContentTypeName getName()
    {
        return name;
    }

    @JsonIgnore
    public ContentTypeName getContentTypeToUpdate()
    {
        return contentTypeToUpdate;
    }

    @JsonIgnore
    public ContentType getContentTypeUpdate()
    {
        return contentTypeUpdate;
    }

    @JsonIgnore
    public ThumbnailJson getIconJson()
    {
        return this.thumbnailJson;
    }
}
