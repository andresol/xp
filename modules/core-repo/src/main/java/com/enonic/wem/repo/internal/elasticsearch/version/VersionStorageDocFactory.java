package com.enonic.wem.repo.internal.elasticsearch.version;

import java.time.Instant;

import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageData;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.xp.repository.RepositoryId;

public class VersionStorageDocFactory
{
    public static StoreRequest create( final NodeVersionDocument nodeVersion, final RepositoryId repositoryId )
    {
        final StorageData data = StorageData.create().
            add( VersionIndexPath.VERSION_ID.getPath(), nodeVersion.getNodeVersionId().toString() ).
            add( VersionIndexPath.NODE_ID.getPath(), nodeVersion.getNodeId().toString() ).
            add( VersionIndexPath.TIMESTAMP.getPath(), nodeVersion.getTimestamp() != null ? nodeVersion.getTimestamp() : Instant.now() ).
            add( VersionIndexPath.NODE_PATH.getPath(), nodeVersion.getNodePath().toString() ).
            build();

        return StoreRequest.create().
            nodePath( nodeVersion.getNodePath() ).
            id( createId( nodeVersion ) ).
            forceRefresh( true ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( repositoryId ) ).
                storageType( StaticStorageType.VERSION ).
                build() ).
            data( data ).
            build();
    }

    private static String createId( final NodeVersionDocument nodeVersion )
    {
        return new NodeVersionDocumentId( nodeVersion.getNodeId(), nodeVersion.getNodeVersionId() ).toString();
    }
}