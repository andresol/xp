package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class DecimalNumber
    extends BasePropertyType
{
    DecimalNumber( int key )
    {
        super( key, JavaType.DOUBLE );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.DecimalNumber( JavaType.DOUBLE.convertFrom( value ) );
    }

    @Override
    public Value.AbstractValueBuilder<Value.DecimalNumber, Double> newValueBuilder()
    {
        return new Value.DecimalNumber.ValueBuilder();
    }

    @Override
    public Property newProperty( final String name, final Value value )
    {
        return new Property.DecimalNumber( name, value );
    }
}
