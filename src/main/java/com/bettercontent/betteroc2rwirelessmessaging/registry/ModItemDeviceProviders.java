package com.bettercontent.betteroc2rwirelessmessaging.registry;

import com.bettercontent.betteroc2rwirelessmessaging.Oc2rWirelessMod;
import com.bettercontent.betteroc2rwirelessmessaging.device.WirelessCardItemDeviceProvider;
import li.cil.oc2.api.bus.device.provider.ItemDeviceProvider;
import li.cil.oc2.api.util.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModItemDeviceProviders {
    public static final DeferredRegister<ItemDeviceProvider> ITEM_DEVICE_PROVIDERS = DeferredRegister.create(Registries.ITEM_DEVICE_PROVIDER, Oc2rWirelessMod.MOD_ID);

    public static final RegistryObject<ItemDeviceProvider> WIRELESS_CARD = ITEM_DEVICE_PROVIDERS.register("wireless_card", WirelessCardItemDeviceProvider::new);

    private ModItemDeviceProviders() {
    }
}
