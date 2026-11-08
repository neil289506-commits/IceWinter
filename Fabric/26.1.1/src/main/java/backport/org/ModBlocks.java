package backport.org;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import backport.org.block.BrittleIceBlock;
import backport.org.block.IceCrystalBlock;
import backport.org.block.IcicleBlock;
// EntityType constants are used directly on this target.
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * 26.4 "Winter Drop" building blocks, re-implemented for 26.2.
 * Properties follow the 26.4 snapshot 6 values (sounds fall back to the 26.2 sound types).
 */
public final class ModBlocks {
	private ModBlocks() {}

	// ---- Packed Ice Bricks ----
	public static final Block PACKED_ICE_BRICKS = register("packed_ice_bricks", Block::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.ICE)
			.instrument(NoteBlockInstrument.CHIME)
			.requiresCorrectToolForDrops()
			.friction(0.6F)
			.strength(0.5F)
			.sound(ModSounds.PACKED_ICE_SOUNDS));
	public static final Block PACKED_ICE_BRICK_SLAB = register("packed_ice_brick_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(PACKED_ICE_BRICKS));
	public static final Block PACKED_ICE_BRICK_STAIRS = register("packed_ice_brick_stairs",
		p -> new StairBlock(PACKED_ICE_BRICKS.defaultBlockState(), p),
		BlockBehaviour.Properties.ofFullCopy(PACKED_ICE_BRICKS));
	public static final Block PACKED_ICE_BRICK_WALL = register("packed_ice_brick_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(PACKED_ICE_BRICKS));
	public static final Block CHISELED_PACKED_ICE = register("chiseled_packed_ice", Block::new,
		BlockBehaviour.Properties.ofFullCopy(PACKED_ICE_BRICKS));

	// ---- Snow Bricks ----
	public static final Block SNOW_BRICKS = register("snow_bricks", Block::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.SNOW)
			.friction(0.6F)
			.requiresCorrectToolForDrops()
			.strength(0.6F)
			.sound(SoundType.SNOW));
	public static final Block SNOW_BRICK_SLAB = register("snow_brick_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(SNOW_BRICKS));
	public static final Block SNOW_BRICK_STAIRS = register("snow_brick_stairs",
		p -> new StairBlock(SNOW_BRICKS.defaultBlockState(), p),
		BlockBehaviour.Properties.ofFullCopy(SNOW_BRICKS));
	public static final Block SNOW_BRICK_WALL = register("snow_brick_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(SNOW_BRICKS));

	// ---- Calcite family (26.4 snapshot 7) ----
	public static final Block CALCITE_PILLAR = register("calcite_pillar", RotatedPillarBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
	public static final Block CALCITE_SLAB = register("calcite_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
	public static final Block CALCITE_STAIRS = register("calcite_stairs",
		p -> new StairBlock(Blocks.CALCITE.defaultBlockState(), p),
		BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
	public static final Block CALCITE_WALL = register("calcite_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
	public static final Block POLISHED_CALCITE = register("polished_calcite", Block::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
	public static final Block POLISHED_CALCITE_SLAB = register("polished_calcite_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_STAIRS = register("polished_calcite_stairs",
		p -> new StairBlock(POLISHED_CALCITE.defaultBlockState(), p),
		BlockBehaviour.Properties.ofFullCopy(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_WALL = register("polished_calcite_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_BRICKS = register("polished_calcite_bricks", Block::new,
		BlockBehaviour.Properties.ofFullCopy(POLISHED_CALCITE));
	public static final Block POLISHED_CALCITE_BRICK_SLAB = register("polished_calcite_brick_slab", SlabBlock::new,
		BlockBehaviour.Properties.ofFullCopy(POLISHED_CALCITE_BRICKS));
	public static final Block POLISHED_CALCITE_BRICK_STAIRS = register("polished_calcite_brick_stairs",
		p -> new StairBlock(POLISHED_CALCITE_BRICKS.defaultBlockState(), p),
		BlockBehaviour.Properties.ofFullCopy(POLISHED_CALCITE_BRICKS));
	public static final Block POLISHED_CALCITE_BRICK_WALL = register("polished_calcite_brick_wall", WallBlock::new,
		BlockBehaviour.Properties.ofFullCopy(POLISHED_CALCITE_BRICKS));

	// ---- Chiseled Soulshale ----
	public static final Block CHISELED_SOULSHALE = register("chiseled_soulshale", Block::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.WARPED_STEM)
			.strength(2.0F, 6.0F)
			.sound(ModSounds.SOULSHALE_SOUNDS));

	// ---- Brittle Ice ----
	public static final Block BRITTLE_ICE = register("brittle_ice", BrittleIceBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.ICE)
			.friction(0.98F)
			.strength(0.5F)
			.sound(ModSounds.BRITTLE_ICE_SOUNDS)
			.noOcclusion()
			.instrument(NoteBlockInstrument.CHIME)
			.isValidSpawn((state, level, pos, type) -> type == EntityType.POLAR_BEAR)
			.isRedstoneConductor((state, level, pos) -> false)
			.pushReaction(PushReaction.DESTROY));

	// ---- Ice Crystal and Icicle ----
	public static final Block ICE_CRYSTAL = register("ice_crystal", IceCrystalBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.ICE)
			.sound(ModSounds.ICE_CRYSTAL_SOUNDS)
			.instrument(NoteBlockInstrument.CHIME)
			.strength(1.5F, 1.5F)
			.lightLevel(state -> 4)
			.pushReaction(PushReaction.DESTROY)
			.isRedstoneConductor((state, level, pos) -> false)
			.noOcclusion());
	public static final Block ICICLE = register("icicle",
		p -> new IcicleBlock(Blocks.PACKED_ICE.defaultBlockState(), p),
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_BLUE)
			.forceSolidOn()
			.instrument(NoteBlockInstrument.CHIME)
			.noOcclusion()
			.sound(ModSounds.ICICLE_SOUNDS)
			.randomTicks()
			.strength(0.5F, 0.5F)
			.dynamicShape()
			.offsetType(BlockBehaviour.OffsetType.XZ)
			.pushReaction(PushReaction.DESTROY)
			.isRedstoneConductor((state, level, pos) -> false));

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		Identifier id = TwoSixFourBackPort.id(name);
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

		Block block = factory.apply(properties.setId(blockKey));
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

		BlockItem item = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		item.registerBlocks(Item.BY_BLOCK, item);
		return block;
	}

	public static void init() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			entries.accept(PACKED_ICE_BRICKS);
			entries.accept(PACKED_ICE_BRICK_STAIRS);
			entries.accept(PACKED_ICE_BRICK_SLAB);
			entries.accept(PACKED_ICE_BRICK_WALL);
			entries.accept(CHISELED_PACKED_ICE);
			entries.accept(SNOW_BRICKS);
			entries.accept(SNOW_BRICK_STAIRS);
			entries.accept(SNOW_BRICK_SLAB);
			entries.accept(SNOW_BRICK_WALL);
			entries.accept(CALCITE_PILLAR);
			entries.accept(CALCITE_STAIRS);
			entries.accept(CALCITE_SLAB);
			entries.accept(CALCITE_WALL);
			entries.accept(POLISHED_CALCITE);
			entries.accept(POLISHED_CALCITE_STAIRS);
			entries.accept(POLISHED_CALCITE_SLAB);
			entries.accept(POLISHED_CALCITE_WALL);
			entries.accept(POLISHED_CALCITE_BRICKS);
			entries.accept(POLISHED_CALCITE_BRICK_STAIRS);
			entries.accept(POLISHED_CALCITE_BRICK_SLAB);
			entries.accept(POLISHED_CALCITE_BRICK_WALL);
			entries.accept(CHISELED_SOULSHALE);
			entries.accept(BRITTLE_ICE);
			entries.accept(ICICLE);
			entries.accept(ICE_CRYSTAL);
		});
	}
}
