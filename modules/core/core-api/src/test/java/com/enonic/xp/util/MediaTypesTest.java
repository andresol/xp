package com.enonic.xp.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;

import com.enonic.xp.media.MediaTypeProvider;

import static org.junit.Assert.*;

public class MediaTypesTest
{
    @Test
    public void testExtension()
    {
        final MediaTypes mediaTypes = MediaTypes.instance();

        Assert.assertNotNull( mediaTypes.fromExt( "html" ) );
        assertEquals( "text/html", mediaTypes.fromExt( "html" ).toString() );

        Assert.assertNotNull( mediaTypes.fromExt( "any" ) );
        assertEquals( "application/octet-stream", mediaTypes.fromExt( "any" ).toString() );
    }

    @Test
    public void testFileName()
    {
        final MediaTypes mediaTypes = MediaTypes.instance();

        Assert.assertNotNull( mediaTypes.fromFile( "index.html" ) );
        assertEquals( "text/html", mediaTypes.fromFile( "index.html" ).toString() );

        Assert.assertNotNull( mediaTypes.fromFile( "file" ) );
        assertEquals( "application/octet-stream", mediaTypes.fromFile( "file" ).toString() );
    }

    @Test
    public void testProviders()
    {
        final MediaType unknown = MediaType.parse( "other/unknown" );

        final MediaType type1 = MediaTypes.instance().fromExt( "test" );
        assertEquals( "application/octet-stream", type1.toString() );
        assertNull( MediaTypes.instance().asMap().get( "test" ) );
        assertEquals( 0, Lists.newArrayList( MediaTypes.instance() ).size() );

        final MediaTypeProvider provider = new MediaTypeProvider()
        {
            @Override
            public MediaType fromExt( final String ext )
            {
                if ( ext.equals( "test" ) )
                {
                    return unknown;
                }

                return null;
            }

            @Override
            public Map<String, MediaType> asMap()
            {
                final HashMap<String, MediaType> map = Maps.newHashMap();
                map.put( "test", unknown );
                return map;
            }
        };

        MediaTypes.instance().addProvider( provider );

        final MediaType type2 = MediaTypes.instance().fromExt( "test" );
        assertEquals( unknown, type2 );
        assertEquals( unknown, MediaTypes.instance().asMap().get( "test" ) );
        assertEquals( 1, Lists.newArrayList( MediaTypes.instance() ).size() );

        MediaTypes.instance().removeProvider( provider );

        final MediaType type3 = MediaTypes.instance().fromExt( "test" );
        assertEquals( "application/octet-stream", type3.toString() );
        assertNull( MediaTypes.instance().asMap().get( "test" ) );
        assertEquals( 0, Lists.newArrayList( MediaTypes.instance() ).size() );
    }
}
