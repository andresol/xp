package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;

import com.google.common.collect.ImmutableSet;

public final class BundleApplicationUrlResolver
    extends ApplicationUrlResolverBase
{
    private final Bundle bundle;

    private ImmutableSet<String> files;

    public BundleApplicationUrlResolver( final Bundle bundle )
    {
        this.bundle = bundle;
    }

    @Override
    public Set<String> findFiles()
    {
        if ( this.files == null )
        {
            this.files = doFindFiles();
        }

        return this.files;
    }

    private ImmutableSet<String> doFindFiles()
    {
        final Enumeration<URL> urls = this.bundle.findEntries( "/", "*", true );
        return ImmutableSet.copyOf( Collections.list( urls ).stream().
            map( url -> url.getFile().substring( 1 ) ).
            filter( name -> !name.endsWith( "/" ) ).
            collect( Collectors.toSet() ) );
    }

    @Override
    public URL findUrl( final String path )
    {
        final String normalized = normalizePath( path );
        return this.bundle.getResource( normalized );
    }
}
