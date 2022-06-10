import sys, Ice
import SmartHome  # Working code
# import SmartHome.SmartHome_ice as SmartHome  # Type hints

device = None


def change_device(key):
    global device
    ret = 1
    if key == 'bulbulator1':
        bulbulator1_base = communicator.propertyToProxy("Bulbulator1.Proxy")
        device = SmartHome.IBulbulatorPrx.checkedCast(bulbulator1_base)
    elif key == 'bulbulator2':
        bulbulator2_base = communicator.propertyToProxy("Bulbulator2.Proxy")
        device = SmartHome.IBulbulatorPrx.checkedCast(bulbulator2_base)
    elif key == 'fridge':
        fridge_base = communicator.propertyToProxy("Fridge.Proxy")
        device = SmartHome.IFridgePrx.checkedCast(fridge_base)
    elif key == 'automatic_fridge':
        autofridge_base = communicator.propertyToProxy("AutomaticFridge.Proxy")
        device = SmartHome.IAutomaticFridgePrx.checkedCast(autofridge_base)
    elif key == 'light':
        light_base = communicator.propertyToProxy("LightController.Proxy")
        device = SmartHome.ILightControllerPrx.checkedCast(light_base)
    elif key == 'premium_light':
        premiumlight_base = communicator.propertyToProxy("PremiumLightController.Proxy")
        device = SmartHome.IPremiumLightControllerPrx.checkedCast(premiumlight_base)
    elif key.startswith('door'):
        door_base = communicator.stringToProxy("IoT/"+key.capitalize()+":default -p 10000")
        device = SmartHome.IDoorPrx.checkedCast(door_base)
        ret += 1
    else:
        print("Unknown device")
    if device is None:
        print("Invalid proxy")
    return ret


def change_settings_helper(dev, args):
    i = 1
    list_of_changes = []
    while i < len(args):
        name = args[i]
        i += 1
        if name == 'name':
            list_of_changes.append(SmartHome.NameSetting(args[i]))
            i += 1
        elif name == 'currentintensity':
            list_of_changes.append(SmartHome.CurrentIntensitySetting(int(args[i])))
            i += 1
        elif name == 'targettemperature':
            list_of_changes.append(SmartHome.TargetTemperatureSetting(int(args[i])))
            i += 1
        elif name == 'lightmode':
            modes = {
                "off": 0,
                "normal": 1,
                "musicsync": 2,
                "videosync": 3,
            }
            if args[i] not in modes:
                raise SmartHome.InvalidSettingValueException(f"Unknown light mode: {args[i]}")
            list_of_changes.append(SmartHome.LightModeSetting(SmartHome.LightMode(args[i], modes[args[i]])))
            i += 1
        elif name == 'lampstates':
            d = dict()
            for j in range(i, len(args)):
                if ':' in args[j]:
                    idd, state = args[j].split(':')
                    d[int(idd)] = bool(int(state))
                    i += 1
                else:
                    break
            list_of_changes.append(SmartHome.LampStatesSetting(d))
        else:
            print("Unknown setting")
    dev.changeSettings(list_of_changes)
    return len(args)


if __name__ == '__main__':
    with Ice.initialize(sys.argv) as communicator:
        call_context = dict()
        device_context = dict()
        default_context = dict()

        call_context.update({
            'findmissingfood': (lambda dev, food: (print(dev.findMissingFood(food[1:])), len(food))[1], call_context),
            'bulbul': (lambda dev, capital: (print(dev.bulbul(bool(capital[1]) if len(capital) > 1 else False)), 2)[1], call_context),
            'performselfcheck': (lambda dev, *args: print(dev.performSelfCheck()), call_context),
            'flash': (lambda dev, *args: print(dev.flash()), call_context),
            'colorfulflash': (lambda dev, color: (print(dev.colorfulFlash(int(color[1]) if len(color) > 1 else 0)), 2)[1], call_context),
            'getdata': (lambda dev, *args: print(dev.getData()), call_context),
            'changesettings': (lambda dev, args: change_settings_helper(dev, args), call_context),
            'close': (lambda dev, args: print(dev.close()), call_context),
            'open': (lambda dev, args: print(dev.open()), call_context),
        })
        device_context.update({
            'bulbulator1': (lambda _, args: change_device(args[0]), call_context),
            'bulbulator2': (lambda _, args: change_device(args[0]), call_context),
            'fridge': (lambda _, args: change_device(args[0]), call_context),
            'automatic_fridge': (lambda _, args: change_device(args[0]), call_context),
            'light': (lambda _, args: change_device(args[0]), call_context),
            'premium_light': (lambda _, args: change_device(args[0]), call_context),
            'door': (lambda _, args: change_device(args[0]+args[1] if len(args) > 1 else ''), call_context),
        })
        default_context.update({
            'device': (lambda _, __: print(f'Select device.'), device_context),
            'call': (lambda _, __: print(f'Entering \'call\' mode.'), call_context)
        })

        f, context = default_context['device']
        f(None, None)

        while True:
            print(
                f'All commands: {list(context.keys()) + list(default_context.keys()) if context != default_context else []}')
            cmd = input("> ")
            cmds = cmd.split(' ')
            cmds = list(map(lambda x: x.lower(), cmds))
            i = 0
            while i < len(cmds):
                c = cmds[i]
                if len(c) < 1:
                    break
                if c not in default_context and c not in context:
                    print(f'Unknown command: {c}')
                    print(list(cmd), cmds)
                    break
                if c in default_context:
                    f, context = default_context[c]
                else:
                    f, context = context[c]
                try:
                    r = f(device, cmds[i:])
                except AttributeError as e:
                    if device:
                        print(f"Device {device.ice_id()} does not support that command. {e}")
                    else:
                        print(f"No device selected! Select a device first")
                        context = device_context
                    break
                except SmartHome.UnsupportedSettingException as e:
                    print(f"Device {device.ice_id()} does not support that setting. {e}")
                    break
                except SmartHome.InvalidSettingValueException as e:
                    print(f"Invalid setting value. {e}")
                    break
                if r:
                    i += r
                else:
                    i += 1
