package backport.org;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoSixFourBackPort implements ModInitializer {
	public static final String MOD_ID = "icewinter";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModSounds.init();
		ModEffects.init();
		ModPotions.init();
		ModEntities.init();
		ModItems.init();
		ModBlocks.init();
		net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (source.isBuiltin() && key.identifier().equals(Identifier.withDefaultNamespace("chests/igloo_chest"))) {
				tableBuilder.withPool(net.minecraft.world.level.storage.loot.LootPool.lootPool()
					.when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.3F))
					.add(net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem(ModItems.MUSIC_DISC_FLURRY)));
			}
		});
		ModFeatures.init();
		backport.org.worldgen.IceCavesWorldgen.registerCaveDecoration();
		LOGGER.info("26.4 BackPort loaded: packed ice bricks and snow bricks");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
