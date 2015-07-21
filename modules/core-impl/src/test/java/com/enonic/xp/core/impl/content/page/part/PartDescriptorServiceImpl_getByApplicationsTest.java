package com.enonic.xp.core.impl.content.page.part;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.PartDescriptors;

public class PartDescriptorServiceImpl_getByApplicationsTest
    extends AbstractPartDescriptorServiceTest
{
    @Test
    public void getDescriptorsFromSingleModule()
        throws Exception
    {
        final Application application = createModule( "foomodule" );
        createDescriptors( "foomodule:foomodule-part-descr" );

        mockResources( application, "/app/parts", "*", false, "app/parts/foomodule-part-descr" );
        final PartDescriptors result = this.service.getByModule( application.getKey() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void getDescriptorsFromMultipleModules()
        throws Exception
    {
        final Applications applications = createModules( "foomodule", "barmodule" );
        createDescriptors( "foomodule:foomodule-part-descr", "barmodule:barmodule-part-descr" );

        mockResources( applications.getModule( ApplicationKey.from( "foomodule" ) ), "/app/parts", "*", false,
                       "app/parts/foomodule-part-descr" );
        mockResources( applications.getModule( ApplicationKey.from( "barmodule" ) ), "/app/parts", "*", false,
                       "app/parts/barmodule-part-descr" );

        final PartDescriptors result = this.service.getByModules( applications.getApplicationKeys() );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}