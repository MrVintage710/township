package com.mrvintage.township.event;

import com.mrvintage.township.Township;
import com.mrvintage.township.proficiency.Proficiency;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Township.MODID, value = Dist.DEDICATED_SERVER)
public class ServerModEvents {

    public static final ResourceKey<Registry<Proficiency>> PROFICIENCY_REGISTRY_KEY = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(Township.MODID, "proficiencies")
    );

    @SubscribeEvent
    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                // The registry key.
                PROFICIENCY_REGISTRY_KEY,
                // The codec of the registry contents.
                Proficiency.CODEC,
                // The network codec of the registry contents. Often identical to the normal codec.
                // May be a reduced variant of the normal codec that omits data that is not needed on the client.
                // May be null. If null, registry entries will not be synced to the client at all.
                // May be omitted, which is functionally identical to passing null (a method overload
                // with two parameters is called that passes null to the normal three parameter method).
                Proficiency.CODEC
        );
    }
}
