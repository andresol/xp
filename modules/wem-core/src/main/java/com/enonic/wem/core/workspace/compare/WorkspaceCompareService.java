package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.core.workspace.compare.query.CompareEntityQuery;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;

interface WorkspaceCompareService
{
    public WorkspaceComparison compareWorkspaces( final CompareWorkspacesQuery query );

    public EntityComparison compare( final CompareEntityQuery query );

}
