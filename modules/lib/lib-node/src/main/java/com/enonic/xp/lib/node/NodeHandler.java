package com.enonic.xp.lib.node;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.Context;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.script.ScriptValue;

public class NodeHandler
{
    protected final NodeService nodeService;

    private final Context context;

    public NodeHandler( final Context context, final NodeService nodeService )
    {
        this.context = context;
        this.nodeService = nodeService;
    }

    public Object create( final ScriptValue params )
    {
        return execute( CreateNodeHandler.create().
            nodeService( this.nodeService ).
            params( params ).
            build() );
    }

    public Object modify( final ScriptValue editor, String key )
    {
        return execute( ModifyNodeHandler.create().
            nodeService( this.nodeService ).
            key( NodeKey.from( key ) ).
            editor( editor ).
            build() );
    }

    public Object get( final String[] keys )
    {
        return execute( GetNodeHandler.create().
            nodeService( this.nodeService ).
            keys( NodeKeys.from( keys ) ).
            build() );
    }

    public Object delete( final String[] keys )
    {
        return execute( DeleteNodeHandler.create().
            nodeService( this.nodeService ).
            keys( NodeKeys.from( keys ) ).
            build() );
    }

    public Object push( final PushNodeHandlerParams params )
    {
        final PushNodeHandler handler = PushNodeHandler.create().
            nodeService( this.nodeService ).
            exclude( params.getExclude() ).
            includeChildren( params.isIncludeChildren() ).
            key( params.getKey() ).
            keys( params.getKeys() ).
            resolve( params.isResolve() ).
            targetBranch( params.getTargetBranch() ).
            build();

        return execute( handler );
    }

    public Object diff( final DiffBranchesHandlerParams params )
    {
        return execute( DiffBranchesHandler.create().
            includeChildren( params.isIncludeChildren() ).
            key( params.getKey() ).
            targetBranch( params.getTargetBranch() ).
            nodeService( this.nodeService ).
            build() );
    }

    public Object move( final String source, final String target )
    {
        return execute( MoveNodeHandler.create().
            source( NodeKey.from( source ) ).
            target( target ).
            nodeService( this.nodeService ).
            build() );
    }

    public Object query( final QueryNodeHandlerParams params )
    {
        return execute( FindNodesByQueryHandler.create().
            query( params.getQuery() ).
            aggregations( params.getAggregations() ).
            count( params.getCount() ).
            start( params.getStart() ).
            sort( params.getSort() ).
            nodeService( this.nodeService ).
            build() );
    }

    public Object findChildren( final FindChildrenHandlerParams params )
    {
        return execute( FindChildrenNodeHandler.create().
            parentKey( NodeKey.from( params.getParentKey() ) ).
            count( params.getCount() ).
            start( params.getStart() ).
            childOrder( ChildOrder.from( params.getChildOrder() ) ).
            countOnly( params.isCountOnly() ).
            recursive( params.isRecursive() ).
            nodeService( this.nodeService ).
            build() );
    }

    public ByteSource getBinary( final String key, final String binaryReference )
    {
        return this.context.callWith( () -> GetBinaryHandler.create().
            key( NodeKey.from( key ) ).
            binaryReference( binaryReference ).
            nodeService( this.nodeService ).
            build().
            execute() );
    }

    public void refresh( final String mode )
    {
        this.context.runWith( () -> nodeService.refresh( RefreshMode.valueOf( mode ) ) );
    }

    private Object execute( final AbstractNodeHandler handler )
    {
        return this.context.callWith( handler::execute );
    }
}
