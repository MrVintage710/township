package com.mrvintage.township.profession.goal;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrvintage.township.profession.Merit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.*;
import java.util.stream.Collectors;

record DealDamage (
    Optional<Integer> minDamage,
    Optional<Component> desc,
    int xp,
    int maxXp,
    List<ExtraCodecs.TagOrElementLocation> weapons
) implements Goal<LivingDamageEvent.Post> {

    public static final MapCodec<DealDamage> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.optionalFieldOf("min_damage").forGetter(DealDamage::minDamage),
            ComponentSerialization.CODEC.optionalFieldOf("desc").forGetter(DealDamage::desc),
            Codec.INT.fieldOf("xp").orElse(1).forGetter(DealDamage::xp),
            Codec.INT.fieldOf("max_xp").orElse(1).forGetter(DealDamage::maxXp),
            Codec.either(
                ExtraCodecs.TAG_OR_ELEMENT_ID,
                ExtraCodecs.TAG_OR_ELEMENT_ID.listOf()
            ).xmap(
                either -> either.map(List::of, lists -> lists),
                list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list)
            ).orElse(new ArrayList<>()).fieldOf("with").forGetter(DealDamage::weapons)
        ).apply(instance, DealDamage::new)
    );

    public boolean weaponsMatch(ItemStack stack) {
        for(ExtraCodecs.TagOrElementLocation item : this.weapons) {
            if(item.tag()) {
                var tag = ItemTags.create(item.id());
                boolean result = stack.is(tag);
                if(result) return result;
            } else {
                Item i = BuiltInRegistries.ITEM.get(item.id());
                boolean result = stack.is(i);
                if(result) return result ;
            }
        }

        return false;
    }

    @Override
    public MapCodec<? extends Goal<?>> type() {
        return CODEC;
    }

    @Override
    public int calcXp(LivingDamageEvent.Post event, Merit merit) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return 0;
        if (!weaponsMatch(player.getWeaponItem())) return 0;
        if (this.minDamage.isPresent() && event.getNewDamage() < this.minDamage.get()) return 0;
        
        return 10;
    }

    @Override
    public boolean accepts(Event event) {
        return event instanceof LivingDamageEvent.Post;
    }

    private Set<Item> getWeaponItems() {
        return this.weapons.stream().map(id -> {
            if (id.tag()) {
                var tagKey = ItemTags.create(id.id());
                var optionalTag = BuiltInRegistries.ITEM.getTag(tagKey);
                if(optionalTag.isPresent()) {
                    return optionalTag.get().stream().map(Holder::value).collect(Collectors.toSet());
                }
            } else {
                var item = BuiltInRegistries.ITEM.get(id.id());
                return Sets.newHashSet(item);
            }

            return new HashSet<Item>();
        }).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    public Component toDescription() {

        if (this.desc.isPresent()) { return this.desc.get(); }

        var damageNeeded = this.minDamage
            .map(min -> Component.literal("Deal at least ").append(Component.literal(min + " damage").withStyle(style -> style.withColor(ChatFormatting.DARK_RED))))
            .orElse(Component.literal("Deal ").append(Component.literal(" damage").withStyle(style -> style.withColor(ChatFormatting.DARK_RED))));

        var weaponIds = this.getWeaponItems().stream().map(Item::getDescriptionId).map(Component::translatable).toList();
        var withWhat = weaponIds.isEmpty()
            ? Component.literal(" with any weapon.")
            : weaponIds.size() == 1 ? Component.literal(" with a ").append(weaponIds.getFirst()).append(Component.literal("."))
            : Component.literal(" with one of the following weapons: ").append(weaponIds.stream().reduce((a, b) -> a.append(Component.literal(", ").append(b))).get()).append(".");

        return damageNeeded.append(withWhat);
    }
}
