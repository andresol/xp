package com.enonic.xp.portal.impl.view;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalContextAccessor;

import static org.junit.Assert.*;

public class LocalizeFunctionTest
    extends AbstractUrlViewFunctionTest
{
    private LocaleService localeService = Mockito.mock( LocaleService.class );

    private MessageBundle messageBundle = Mockito.mock( MessageBundle.class );

    @Before
    public final void setupTest()
    {
        Site site = Site.newSite().
            description( "This is my site" ).
            name( "my-content" ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build();

        this.context.setSite( site );

        PortalContextAccessor.set( this.context );
    }

    @Override
    protected void setupFunction()
        throws Exception
    {
        final LocalizeFunction function = new LocalizeFunction();
        function.setLocaleService( localeService );
        register( function );
    }

    @Test
    public void no_bundle()
    {
        Mockito.when( localeService.getBundle( Mockito.eq( this.context.getModule() ), Mockito.eq( new Locale( "en", "US" ) ) ) ).
            thenReturn( null );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "_locale=en-US  ", "a=5", "b=2" );
        assertEquals( "no localization bundle found in module 'mymodule'", result );
    }

    @Test
    public void arrayd_params()
    {
        Mockito.when( localeService.getBundle( Mockito.eq( this.context.getModule() ), Mockito.eq( new Locale( "en", "US" ) ) ) ).
            thenReturn( messageBundle );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "_locale=en-US  ", "a={1,2,3}" );
        assertEquals( "no localization bundle found in module 'mymodule'", result );
    }


    @Test
    public void all_params()
    {
        Mockito.when( localeService.getBundle( Mockito.eq( this.context.getModule() ), Mockito.eq( new Locale( "en", "US" ) ) ) ).
            thenReturn( messageBundle );

        Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Matchers.<String>anyVararg() ) ).thenReturn( "localizedString" );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "_locale=en-US  ", "a=5", "b=2" );
        assertEquals( "localizedString", result );

    }

    @Test
    public void no_locale()
    {
        Mockito.when( localeService.getBundle( Mockito.eq( this.context.getModule() ), Mockito.eq( new Locale( "en" ) ) ) ).
            thenReturn( messageBundle );

        Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Matchers.<String>anyVararg() ) ).thenReturn( "localizedString" );

        final Object result = execute( "i18n.localize", "_key=myPhrase", "a=5", "b=2" );
        assertEquals( "localizedString", result );

    }

    @Test
    public void no_params()
    {
        Mockito.when( localeService.getBundle( Mockito.eq( this.context.getModule() ), Mockito.eq( new Locale( "en" ) ) ) ).
            thenReturn( messageBundle );

        Mockito.when( messageBundle.localize( Mockito.eq( "myPhrase" ), Matchers.<String>anyVararg() ) ).thenReturn( "localizedString" );

        final Object result = execute( "i18n.localize", "_key=myPhrase" );
        assertEquals( "localizedString", result );

    }

}
