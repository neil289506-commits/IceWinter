package backport.org;

import java.util.function.Function;

import backport.org.item.IceBallItem;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.JukeboxSong;

public final class ModItems {
	private ModItems() {}

	public static final Item ICE_BALL = register("ice_ball", IceBallItem::new, new Item.Properties().stacksTo(16));
	public static final Item FROSTBITE_SPAWN_EGG = register("frostbite_spawn_egg", SpawnEggItem::new,
		new Item.Properties().spawnEgg(ModEntities.FROSTBITE));

	public static final Item MUSIC_DISC_FLURRY = register("music_disc_flurry", Item::new,
		new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
			.jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TwoSixFourBackPort.id("flurry"))));

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, TwoSixFourBackPort.id(name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
	}

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.accept(ICE_BALL));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(FROSTBITE_SPAWN_EGG));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(MUSIC_DISC_FLURRY));
	}
}
