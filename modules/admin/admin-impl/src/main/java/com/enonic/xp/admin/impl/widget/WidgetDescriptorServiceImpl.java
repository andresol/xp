package com.enonic.xp.admin.impl.widget;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

@Component(immediate = true)
public final class WidgetDescriptorServiceImpl
    implements WidgetDescriptorService
{
    private DescriptorService descriptorService;

    @Override
    public Descriptors<WidgetDescriptor> getByInterfaces( final String... interfaceNames )
    {
        return this.descriptorService.getAll( WidgetDescriptor.class ).
            filter( widgetDescriptor -> Arrays.stream( interfaceNames ).anyMatch( widgetDescriptor::hasInterface ) );
    }
    
    @Override
    public Descriptors<WidgetDescriptor> getAllowedByInterfaces( final String... interfaceNames )
    {
        final PrincipalKeys userPrincipalKeys = getPrincipalKeys();
        return this.descriptorService.getAll( WidgetDescriptor.class ).
            filter( widgetDescriptor -> {
                if ( Arrays.stream( interfaceNames ).noneMatch( widgetDescriptor::hasInterface ) )
                {
                    return false;
                }
                if ( !widgetDescriptor.isAccessAllowed( userPrincipalKeys ) )
                {
                    return false;
                }
                return true;
            } );
    }

    @Override
    public WidgetDescriptor getByKey( final DescriptorKey descriptorKey )
    {
        return this.descriptorService.get( WidgetDescriptor.class, descriptorKey );
    }


    private PrincipalKeys getPrincipalKeys()
    {
        return ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
    }


    @Reference
    public void setDescriptorService( final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }
}
