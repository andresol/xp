package com.enonic.wem.core.content.type.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.jcr.JcrHelper;


final class CreateMixinDaoHandler
    extends AbstractMixinDaoHandler
{
    CreateMixinDaoHandler( final Session session )
    {
        super( session );
    }

    void create( final Mixin mixin )
        throws RepositoryException
    {
        final QualifiedMixinName qualifiedName = mixin.getQualifiedName();
        if ( mixinExists( qualifiedName ) )
        {
            throw new SystemException( "Mixin already exists: {0}", qualifiedName.toString() );
        }

        final Node mixinNode = createMixinNode( qualifiedName );
        this.mixinJcrMapper.toJcr( mixin, mixinNode );
    }

    private Node createMixinNode( final QualifiedMixinName qualifiedName )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node mixinsNode = rootNode.getNode( MixinDao.MIXINS_PATH );
        final Node moduleNode = JcrHelper.getOrAddNode( mixinsNode, qualifiedName.getModuleName().toString() );
        return JcrHelper.getOrAddNode( moduleNode, qualifiedName.getLocalName() );
    }

}
