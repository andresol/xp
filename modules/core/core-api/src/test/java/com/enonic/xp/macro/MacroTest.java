package com.enonic.xp.macro;

import org.junit.Test;

import static org.junit.Assert.*;

public class MacroTest
{
    @Test
    public void testCreation()
    {
        final Macro macro = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            build();
        assertEquals( "[macro param1=\"value1\"]body[/macro]", macro.toString() );
    }

    @Test
    public void testBodyNull()
    {
        final Macro macro = Macro.create().name( "macro" ).param( "param1", "value1" ).build();
        assertEquals( "[macro param1=\"value1\"/]", macro.toString() );
    }

    @Test
    public void testEquals()
    {
        final Macro macro1 = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final Macro macro2 = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final Macro macro3 = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value3" ).
            build();

        assertEquals( macro1, macro2 );
        assertNotEquals( macro1, macro3 );
    }
}
