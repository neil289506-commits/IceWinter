package backport.org;

import backport.org.worldgen.IceCaveFeature;
import backport.org.worldgen.IceCrevasseFeature;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class ModFeatures {
	private ModFeatures() {}

	public static final Feature<NoneFeatureConfiguration> ICE_CAVE = Registry.register(
		BuiltInRegistries.FEATURE, TwoSixFourBackPort.id("ice_cave"), new IceCaveFeature(NoneFeatureConfiguration.CODEC));
	public static final Feature<NoneFeatureConfiguration> ICE_CREVASSE = Registry.register(
		BuiltInRegistries.FEATURE, TwoSixFourBackPort.id("crevasse"), new IceCrevasseFeature(NoneFeatureConfiguration.CODEC));

	public static void init() {}
}
