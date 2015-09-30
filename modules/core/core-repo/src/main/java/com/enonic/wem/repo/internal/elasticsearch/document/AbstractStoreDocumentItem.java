package com.enonic.wem.repo.internal.elasticsearch.document;

import com.enonic.wem.repo.internal.index.IndexValueType;
import com.enonic.xp.index.IndexPath;

public abstract class AbstractStoreDocumentItem<T>
{
    private final IndexPath indexPath;

    AbstractStoreDocumentItem( final IndexPath path )
    {
        this.indexPath = path;
    }

    public String getPath()
    {
        return indexPath.toString();
    }

    public abstract IndexValueType getIndexBaseType();

    public abstract T getValue();

}
