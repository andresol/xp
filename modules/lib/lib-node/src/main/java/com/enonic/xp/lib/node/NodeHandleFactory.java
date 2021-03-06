package com.enonic.xp.lib.node;

import java.util.concurrent.Callable;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

public class NodeHandleFactory
    implements ScriptBean
{
    private NodeService nodeService;

    private SecurityService securityService;

    @Override
    public void initialize( final BeanContext context )
    {
        this.nodeService = context.getService( NodeService.class ).get();
        this.securityService = context.getService( SecurityService.class ).get();
    }

    public NodeHandler create( final NodeHandleContext context )
    {
        final ContextBuilder contextBuilder = ContextBuilder.from( ContextAccessor.current() );

        if ( context.getRepoId() != null )
        {
            contextBuilder.repositoryId( context.getRepoId() );
        }

        if ( context.getBranch() != null )
        {
            contextBuilder.branch( context.getBranch() );
        }

        contextBuilder.authInfo( getAuthInfo( context.getUsername(), context.getUserStore(), context.getPrincipals() ) );

        return new NodeHandler( contextBuilder.build(), this.nodeService );
    }


    private AuthenticationInfo getAuthInfo( final String username, final String userStore, final PrincipalKey[] principals )
    {
        AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( username != null )
        {
            authInfo = runAsAuthenticated( () -> getAuthenticationInfo( username, userStore ) );
        }
        if ( principals != null )
        {
            authInfo = AuthenticationInfo.
                copyOf( authInfo ).
                principals( principals ).
                build();
        }

        return authInfo;
    }

    private AuthenticationInfo getAuthenticationInfo( final String username, final String userStore )
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
        token.setUsername( username );
        token.setUserStore( userStore == null ? null : UserStoreKey.from( userStore ) );
        return this.securityService.authenticate( token );
    }

    private <T> T runAsAuthenticated( final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( authInfo ).
            repositoryId( SecurityConstants.SECURITY_REPO.getId() ).
            branch( SecurityConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }
}
