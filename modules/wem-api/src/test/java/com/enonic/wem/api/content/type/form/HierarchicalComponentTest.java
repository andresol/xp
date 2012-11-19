package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class HierarchicalComponentTest
{
    @Test
    public void setParentPath()
    {
        Input input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setParentPath( new FormItemPath( "myParent" ) );
        assertEquals( "myParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setPath( new FormItemPath( "myParent.myField" ) );
        input.setParentPath( new FormItemPath( "myNewParent" ) );
        assertEquals( "myNewParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setParentPath( new FormItemPath( "myGrandParent.myParent" ) );
        assertEquals( "myGrandParent.myParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setPath( new FormItemPath( "myParent.myField" ) );
        input.setParentPath( new FormItemPath( "myNewGrandParent.myNewParent" ) );
        assertEquals( "myNewGrandParent.myNewParent.myField", input.getPath().toString() );
    }
}
