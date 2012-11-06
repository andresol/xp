package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditors;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.MockContentTypeFetcher;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;

@Component
public final class CreateOrUpdateContentRpcHandler
    extends AbstractDataRpcHandler
{
    private ContentTypeFetcher contentTypeFetcher;

    public CreateOrUpdateContentRpcHandler()
    {
        super( "content_createOrUpdate" );

        MockContentTypeFetcher mockContentTypeFetcher = new MockContentTypeFetcher();
        ContentType myContentType = new ContentType();
        myContentType.setModule( Module.newModule().name( "myModule" ).build() );
        myContentType.setName( "myContentType" );
        myContentType.addComponent( newInput().name( "myTextLine1" ).type( InputTypes.TEXT_LINE ).build() );
        myContentType.addComponent( newInput().name( "myTextLine2" ).type( InputTypes.TEXT_LINE ).build() );
        ComponentSet componentSet = newComponentSet().name( "myComponentSet" ).build();
        componentSet.add( newInput().name( "myTextLine1" ).type( InputTypes.TEXT_LINE ).build() );
        myContentType.addComponent( componentSet );
        mockContentTypeFetcher.add( myContentType );
        this.contentTypeFetcher = mockContentTypeFetcher;
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedContentTypeName qualifiedContentTypeName =
            new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
        final ContentPath contentPath = ContentPath.from( context.param( "contentPath" ).required().asString() );

        final ContentType contentType = contentTypeFetcher.getContentType( qualifiedContentTypeName );
        final ContentData contentData = new ContentDataParser( contentType ).parse( context.param( "contentData" ).required().asObject() );

        if ( !contentExists( contentPath ) )
        {
            final CreateContent createContent = Commands.content().create();
            createContent.contentPath( contentPath );
            createContent.contentType( qualifiedContentTypeName );
            createContent.contentData( contentData );
            createContent.owner( AccountKey.anonymous() );
            try
            {
                client.execute( createContent );
                context.setResult( CreateOrUpdateContentJsonResult.created() );
            }
            catch ( ContentNotFoundException e )
            {
                context.setResult(
                    new JsonErrorResult( "Unable to create content. Path [{0}] does not exist", contentPath.getParentPath().toString() ) );
            }
        }
        else
        {
            final UpdateContents updateContents = Commands.content().update();
            updateContents.paths( ContentPaths.from( contentPath ) );
            updateContents.editor( ContentEditors.setContentData( contentData ) );
            updateContents.modifier( AccountKey.anonymous() );

            client.execute( updateContents );
            context.setResult( CreateOrUpdateContentJsonResult.updated() );
        }
    }

    private boolean contentExists( final ContentPath contentPath )
    {
        final Contents contents = client.execute( Commands.content().get().paths( ContentPaths.from( contentPath ) ) );
        return contents.isNotEmpty();
    }
}
