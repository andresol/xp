package com.enonic.wem.web.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;

import static com.enonic.wem.api.command.Commands.contentType;

@Component
@Path("content/image")
@Produces("image/*")
public class ContentImageResource
{
    private final ContentImageHelper helper;

    private Client client;

    public ContentImageResource()
    {
        this.helper = new ContentImageHelper();
    }

    @GET
    @Path("{contentId}")
    public Response getContentImage( @PathParam("contentId") final String contentId,
                                     @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        if ( contentId == null )
        {
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        final ContentId contentIdValue = ContentId.from( contentId );
        final Content content = findContent( contentIdValue );
        if ( content == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        final QualifiedContentTypeName contentType = content.getType();

        final String mimeType;
        final BufferedImage contentImage;
        if ( contentType.isImageMedia() )
        {
            final RootDataSet contentData = content.getRootDataSet();
            final Binary binary = findBinary( contentData.getData( "binary" ).getBinaryId() );
            contentImage = helper.getImageFromBinary( binary, size );
            mimeType = contentData.getData( "mimeType" ).getString();
        }
        else
        {
            final Icon contentTypeIcon = findRootContentTypeIcon( contentType );
            contentImage = helper.getIconImage( contentTypeIcon, size );
            mimeType = contentTypeIcon == null ? "image/png" : contentTypeIcon.getMimeType();
        }

        return Response.ok( contentImage, mimeType ).build();
    }

    private Icon findRootContentTypeIcon( final QualifiedContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType == null ? null : contentType.getIcon();
    }

    private ContentType getContentType( final QualifiedContentTypeName contentTypeName )
    {
        if ( contentTypeName == null )
        {
            return null;
        }
        final QualifiedContentTypeNames qualifiedNames = QualifiedContentTypeNames.from( contentTypeName );
        return client.execute( contentType().get().qualifiedNames( qualifiedNames ) ).first();
    }

    private Content findContent( final ContentId contentId )
    {
        return client.execute( Commands.content().get().selectors( ContentIds.from( contentId ) ) ).first();
    }

    private Binary findBinary( final BinaryId binaryId )
    {
        return client.execute( Commands.binary().get().binaryId( binaryId ) );
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
