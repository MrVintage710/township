package com.mrvintage.township.event;

import com.mrvintage.township.Township;
import com.mrvintage.township.profession.Profession;
import com.mrvintage.township.profession.goal.Goals;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = Township.MODID, value = Dist.DEDICATED_SERVER)
public class ServerModEvents {

    @SubscribeEvent
    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                // The registry key.
                Profession.REGISTRY_KEY,
                // The codec of the registry contents.
                Profession.CODEC,
                // The network codec of the registry contents. Often identical to the normal codec.
                // May be a reduced variant of the normal codec that omits data that is not needed on the client.
                // May be null. If null, registry entries will not be synced to the client at all.
                // May be omitted, which is functionally identical to passing null (a method overload
                // with two parameters is called that passes null to the normal three parameter method).
                Profession.CODEC
        );
    }

    @SubscribeEvent
    public void newRegistry(NewRegistryEvent event) {
        event.register(Goals.DISPATCH);
    }
}
