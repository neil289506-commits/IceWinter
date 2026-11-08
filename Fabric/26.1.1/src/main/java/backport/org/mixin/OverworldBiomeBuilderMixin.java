package backport.org.mixin;

import java.util.function.Consumer;

import backport.org.worldgen.IceCavesWorldgen;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

@Mixin(OverworldBiomeBuilder.class)
abstract class OverworldBiomeBuilderMixin {
	@Inject(method = "addBiomes", at = @At("TAIL"))
	private void icewinter$addIceCaves(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> biomes, CallbackInfo ci) {
		IceCavesWorldgen.addIceCaveBiome(biomes);
	}
}
