package backport.org.mixin;

import backport.org.TwoSixFourBackPort;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerType.class)
abstract class IceCaveVillagerTypeMixin {
   @Inject(method = "byBiome", at = @At("HEAD"), cancellable = true)
   private static void iceCaves$snowyVillager(Holder<Biome> biome, CallbackInfoReturnable<ResourceKey<VillagerType>> cir) {
      if (biome.is(ResourceKey.create(Registries.BIOME, TwoSixFourBackPort.id("ice_caves")))) {
         cir.setReturnValue(VillagerType.SNOW);
      }
   }
}
