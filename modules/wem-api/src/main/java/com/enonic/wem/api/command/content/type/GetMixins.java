package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;

public final class GetMixins
    extends Command<Mixins>
{
    private QualifiedMixinNames qualifiedMixinNames;

    private boolean getAllContentTypes = false;

    public QualifiedMixinNames getQualifiedMixinNames()
    {
        return this.qualifiedMixinNames;
    }

    public GetMixins names( final QualifiedMixinNames qualifiedMixinNames )
    {
        this.qualifiedMixinNames = qualifiedMixinNames;
        return this;
    }

    public boolean isGetAll()
    {
        return getAllContentTypes;
    }

    public GetMixins all()
    {
        getAllContentTypes = true;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetMixins ) )
        {
            return false;
        }

        final GetMixins that = (GetMixins) o;
        return Objects.equal( this.qualifiedMixinNames, that.qualifiedMixinNames ) &&
            ( this.getAllContentTypes == that.getAllContentTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedMixinNames, this.getAllContentTypes );
    }

    @Override
    public void validate()
    {
        if ( getAllContentTypes )
        {
            Preconditions.checkArgument( this.qualifiedMixinNames == null,
                                         "Cannot specify both get all and get content type qualifiedMixinNames" );
        }
        else
        {
            Preconditions.checkNotNull( this.qualifiedMixinNames, "Content type cannot be null" );
        }
    }

}
