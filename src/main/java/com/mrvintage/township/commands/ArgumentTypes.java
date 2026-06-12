package com.mrvintage.township.commands;

import com.mrvintage.township.Township;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ArgumentTypes {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Township.MODID);

    public static final Supplier<ArgumentTypeInfo<?, ?>> MERIT_PATH_ARGUMENT_TYPE = REGISTRY.register(
        "merit_path",
        () -> SingletonArgumentInfo.contextFree(MeritPathArgument::new)
    );
}
