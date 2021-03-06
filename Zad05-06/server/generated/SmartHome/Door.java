//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.7
//
// <auto-generated>
//
// Generated from file `SmartHome.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package SmartHome;

public class Door extends com.zeroc.Ice.Value
{
    public Door()
    {
        this.name = "";
    }

    public Door(String name, boolean isClosed)
    {
        this.name = name;
        this.isClosed = isClosed;
    }

    public String name;

    public boolean isClosed;

    public Door clone()
    {
        return (Door)super.clone();
    }

    public static String ice_staticId()
    {
        return "::SmartHome::Door";
    }

    @Override
    public String ice_id()
    {
        return ice_staticId();
    }

    /** @hidden */
    public static final long serialVersionUID = -7515113047124111106L;

    /** @hidden */
    @Override
    protected void _iceWriteImpl(com.zeroc.Ice.OutputStream ostr_)
    {
        ostr_.startSlice(ice_staticId(), -1, true);
        ostr_.writeString(name);
        ostr_.writeBool(isClosed);
        ostr_.endSlice();
    }

    /** @hidden */
    @Override
    protected void _iceReadImpl(com.zeroc.Ice.InputStream istr_)
    {
        istr_.startSlice();
        name = istr_.readString();
        isClosed = istr_.readBool();
        istr_.endSlice();
    }
}
