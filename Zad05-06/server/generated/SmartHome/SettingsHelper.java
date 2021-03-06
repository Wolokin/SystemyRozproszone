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

/**
 * Helper class for marshaling/unmarshaling Settings.
 **/
public final class SettingsHelper
{
    public static void write(com.zeroc.Ice.OutputStream ostr, BaseSetting[] v)
    {
        if(v == null)
        {
            ostr.writeSize(0);
        }
        else
        {
            ostr.writeSize(v.length);
            for(int i0 = 0; i0 < v.length; i0++)
            {
                ostr.writeValue(v[i0]);
            }
        }
    }

    public static BaseSetting[] read(com.zeroc.Ice.InputStream istr)
    {
        final BaseSetting[] v;
        final int len0 = istr.readAndCheckSeqSize(1);
        v = new BaseSetting[len0];
        for(int i0 = 0; i0 < len0; i0++)
        {
            final int fi0 = i0;
            istr.readValue(value -> v[fi0] = value, BaseSetting.class);
        }
        return v;
    }

    public static void write(com.zeroc.Ice.OutputStream ostr, int tag, java.util.Optional<BaseSetting[]> v)
    {
        if(v != null && v.isPresent())
        {
            write(ostr, tag, v.get());
        }
    }

    public static void write(com.zeroc.Ice.OutputStream ostr, int tag, BaseSetting[] v)
    {
        if(ostr.writeOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            int pos = ostr.startSize();
            SettingsHelper.write(ostr, v);
            ostr.endSize(pos);
        }
    }

    public static java.util.Optional<BaseSetting[]> read(com.zeroc.Ice.InputStream istr, int tag)
    {
        if(istr.readOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            istr.skip(4);
            BaseSetting[] v;
            v = SettingsHelper.read(istr);
            return java.util.Optional.of(v);
        }
        else
        {
            return java.util.Optional.empty();
        }
    }
}
