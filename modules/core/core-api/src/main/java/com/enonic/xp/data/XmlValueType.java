package com.enonic.xp.data;

final class XmlValueType
    extends ValueType<String>
{
    XmlValueType()
    {
        super( "Xml", JavaTypeConverters.STRING );
    }

    @Override
    Value fromJsonValue( final Object object )
    {
        return ValueFactory.newXml( convertNullSafe( object ) );
    }
}
