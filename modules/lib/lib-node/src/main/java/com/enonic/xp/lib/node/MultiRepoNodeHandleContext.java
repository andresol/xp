package com.enonic.xp.lib.node;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

public class MultiRepoNodeHandleContext
{
    private final Set<SearchTarget> searchTargets = Sets.newHashSet();

    public MultiRepoNodeHandleContext()
    {
    }

    @SuppressWarnings("unused")
    public void addSource( final String repoId, final String branchId, final String[] principalKeys )
    {
        this.searchTargets.add( SearchTarget.create().
            repositoryId( RepositoryId.from( repoId ) ).
            branch( Branch.from( branchId ) ).
            principalKeys( PrincipalKeys.from( principalKeys ) ).
            build() );
    }

    Set<SearchTarget> getSearchTargets()
    {
        return this.searchTargets;
    }
}
