package com.enonic.wem.api.content.type.form;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public class QualifiedMixinName
    extends ModuleBasedQualifiedName
{
    public QualifiedMixinName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public QualifiedMixinName( final ModuleName moduleName, final String mixinName )
    {
        super( moduleName, mixinName );
    }

    public QualifiedMixinName( final String moduleName, final String mixinName )
    {
        super( new ModuleName( moduleName ), mixinName );
    }

    public String getMixinName()
    {
        return getLocalName();
    }

    public static QualifiedMixinName from( final String value )
    {
        return new QualifiedMixinName( value );
    }
}
