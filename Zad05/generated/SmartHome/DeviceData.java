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

public class DeviceData extends com.zeroc.Ice.Value
{
    public DeviceData()
    {
    }

    public DeviceData(BaseReading[] readings, BaseSetting[] settings)
    {
        this.readings = readings;
        this.settings = settings;
    }

    public BaseReading[] readings;

    public BaseSetting[] settings;

    public DeviceData clone()
    {
        return (DeviceData)super.clone();
    }

    public static String ice_staticId()
    {
        return "::SmartHome::DeviceData";
    }

    @Override
    public String ice_id()
    {
        return ice_staticId();
    }

    /** @hidden */
    public static final long serialVersionUID = -3222735734509543573L;

    /** @hidden */
    @Override
    protected void _iceWriteImpl(com.zeroc.Ice.OutputStream ostr_)
    {
        ostr_.startSlice(ice_staticId(), -1, true);
        ReadingsHelper.write(ostr_, readings);
        SettingsHelper.write(ostr_, settings);
        ostr_.endSlice();
    }

    /** @hidden */
    @Override
    protected void _iceReadImpl(com.zeroc.Ice.InputStream istr_)
    {
        istr_.startSlice();
        readings = ReadingsHelper.read(istr_);
        settings = SettingsHelper.read(istr_);
        istr_.endSlice();
    }
}
