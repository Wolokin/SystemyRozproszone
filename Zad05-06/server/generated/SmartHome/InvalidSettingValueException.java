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

public class InvalidSettingValueException extends com.zeroc.Ice.UserException
{
    public InvalidSettingValueException()
    {
        this.reason = "";
    }

    public InvalidSettingValueException(Throwable cause)
    {
        super(cause);
        this.reason = "";
    }

    public InvalidSettingValueException(String reason)
    {
        this.reason = reason;
    }

    public InvalidSettingValueException(String reason, Throwable cause)
    {
        super(cause);
        this.reason = reason;
    }

    public String ice_id()
    {
        return "::SmartHome::InvalidSettingValueException";
    }

    public String reason;

    /** @hidden */
    @Override
    protected void _writeImpl(com.zeroc.Ice.OutputStream ostr_)
    {
        ostr_.startSlice("::SmartHome::InvalidSettingValueException", -1, true);
        ostr_.writeString(reason);
        ostr_.endSlice();
    }

    /** @hidden */
    @Override
    protected void _readImpl(com.zeroc.Ice.InputStream istr_)
    {
        istr_.startSlice();
        reason = istr_.readString();
        istr_.endSlice();
    }

    /** @hidden */
    public static final long serialVersionUID = -5854886120384709051L;
}
