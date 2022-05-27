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

public interface AutomaticFridgeDisp extends FridgeDisp,
                                             IAutomaticFridge
{
    /** @hidden */
    static final String[] _iceIds =
    {
        "::Ice::Object",
        "::SmartHome::AutomaticFridge",
        "::SmartHome::Fridge",
        "::SmartHome::IAutomaticFridge",
        "::SmartHome::IFridge",
        "::SmartHome::ISmartDevice",
        "::SmartHome::SmartDevice"
    };

    @Override
    default String[] ice_ids(com.zeroc.Ice.Current current)
    {
        return _iceIds;
    }

    @Override
    default String ice_id(com.zeroc.Ice.Current current)
    {
        return ice_staticId();
    }

    static String ice_staticId()
    {
        return AutomaticFridge.ice_staticId();
    }

    /** @hidden */
    final static String[] _iceOps =
    {
        "changeSettings",
        "findMissingFood",
        "getData",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "performSelfCheck"
    };

    /** @hidden */
    @Override
    default java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceDispatch(com.zeroc.IceInternal.Incoming in, com.zeroc.Ice.Current current)
        throws com.zeroc.Ice.UserException
    {
        int pos = java.util.Arrays.binarySearch(_iceOps, current.operation);
        if(pos < 0)
        {
            throw new com.zeroc.Ice.OperationNotExistException(current.id, current.facet, current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ISmartDevice._iceD_changeSettings(this, in, current);
            }
            case 1:
            {
                return IAutomaticFridge._iceD_findMissingFood(this, in, current);
            }
            case 2:
            {
                return ISmartDevice._iceD_getData(this, in, current);
            }
            case 3:
            {
                return com.zeroc.Ice.Object._iceD_ice_id(this, in, current);
            }
            case 4:
            {
                return com.zeroc.Ice.Object._iceD_ice_ids(this, in, current);
            }
            case 5:
            {
                return com.zeroc.Ice.Object._iceD_ice_isA(this, in, current);
            }
            case 6:
            {
                return com.zeroc.Ice.Object._iceD_ice_ping(this, in, current);
            }
            case 7:
            {
                return IFridge._iceD_performSelfCheck(this, in, current);
            }
        }

        assert(false);
        throw new com.zeroc.Ice.OperationNotExistException(current.id, current.facet, current.operation);
    }
}
