package com.mrvintage.township.profession;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.profession.goal.Goal;
import com.mrvintage.township.profession.reward.Reward;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record Merit(
    String name,
    int tier,
    int xp,
    ResourceLocation icon,
    List<Merit.Path> prereqs,
    @NotNull List<Goal<?>> goals,
    List<Reward> rewards
) {
    public static final Codec<Merit> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Merit::name),
            Codec.INT.fieldOf("tier").forGetter(Merit::tier),
            Codec.INT.fieldOf("xp").forGetter(Merit::xp),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(Merit::icon),
            Path.CODEC.xmap(
                a -> (List<Merit.Path>) new ArrayList<>(List.of(a)),
                List::getFirst
            ).optionalFieldOf("prereq", new ArrayList<>()).forGetter(Merit::prereqs),
            Goal.CODEC.listOf().orElse(new ArrayList<>()).fieldOf("goals").forGetter(Merit::goals),
            Reward.CODEC.listOf().orElse(new ArrayList<>()).fieldOf("rewards").forGetter(Merit::rewards)
        ).apply(instance, Merit::new)
    );

    public void renderIcon(GuiGraphics graphics, int x, int y) {
        Item item = BuiltInRegistries.ITEM.get(this.icon());
        graphics.renderItem(new ItemStack(item, 1), x, y);
    }

    public record Path(ResourceLocation file, String speciality, String merit) {

        public static final Codec<Merit.Path> CODEC = ResourceLocation.CODEC.comapFlatMap(
            location -> {
                Merit.Path result = Merit.Path.from(location);
                if(result == null) {
                    return DataResult.error(() -> location + " is not a valid merit path. Must follow the following format: `<namespace>:<profession>/<specialty>/<merit>`");
                } else {
                    return DataResult.success(result);
                }
            },
            Merit.Path::toResourceLocation
        );

        public static final StreamCodec<ByteBuf, Merit.Path> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Merit.Path::from, Merit.Path::toString);

        @Nullable
        public static Merit.Path from(ResourceLocation location) {
            String namespace = location.getNamespace();
            String path = location.getPath();
            String[] split = path.split("/");
            if(split.length < 3) {
                return null;
            }
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, split[0]);
            return new Merit.Path(key, split[1], split[2]);
        }

        @Nullable
        public static Merit.Path from(@Nullable String location) {
            if(location == null) return null;
            ResourceLocation resourceLocation = ResourceLocation.tryParse(location);
            if (resourceLocation != null) return Merit.Path.from(resourceLocation);
            return null;
        }

        public ResourceLocation toResourceLocation() {
            return ResourceLocation.fromNamespaceAndPath(this.file.getNamespace(), this.file.getPath() + "/" + this.speciality + "/" + this.merit);
        }

        @Override
        @NotNull
        public String toString() {
            return this.file + "/" + this.speciality + "/" + this.merit;
        }
    }
}
