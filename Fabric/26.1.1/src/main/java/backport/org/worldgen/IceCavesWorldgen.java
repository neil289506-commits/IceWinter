package backport.org.worldgen;

import java.util.function.Consumer;

import backport.org.TwoSixFourBackPort;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class IceCavesWorldgen {
	public static final ResourceKey<Biome> ICE_CAVES = ResourceKey.create(Registries.BIOME, TwoSixFourBackPort.id("ice_caves"));

	private IceCavesWorldgen() {}

	private static ResourceKey<PlacedFeature> placed(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, TwoSixFourBackPort.id(name));
	}

	/** Adds the ice cave feature to every overworld biome, and the extra decoration to the Ice Caves biome. */
	public static void registerCaveDecoration() {
		BiomeModifications.addFeature(
			BiomeSelectors.tag(BiomeTags.IS_OVERWORLD).or(BiomeSelectors.includeByKey(ICE_CAVES)),
			Decoration.UNDERGROUND_DECORATION, placed("ice_cave"));
		for (String name : new String[] {"snow_layers", "ice_crystals", "ice_crevasse"}) {
			BiomeModifications.addFeature(BiomeSelectors.includeByKey(ICE_CAVES), Decoration.UNDERGROUND_DECORATION, placed(name));
		}
	}

	/** The climate point where the Ice Caves biome generates. */
	public static void addIceCaveBiome(Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> mapper) {
		Parameter cold = Parameter.span(-1.0F, -0.8F);
		Parameter humidity = Parameter.span(-1.0F, 0.7F);
		Parameter continental = Parameter.span(-1.05F, -0.19F);
		Parameter all = Parameter.span(-1.0F, 1.0F);
		Parameter undergroundDepth = Parameter.span(0.2F, 0.9F);
		Parameter iceCaveWeirdness = Parameter.span(0.5F, 1.1F);
		mapper.accept(Pair.of(new Climate.ParameterPoint(cold, humidity, continental, all, undergroundDepth, iceCaveWeirdness, 0L), ICE_CAVES));
	}
}
