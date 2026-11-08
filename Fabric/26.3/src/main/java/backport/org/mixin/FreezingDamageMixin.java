package backport.org.mixin;

import backport.org.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
abstract class FreezingDamageMixin {
   @ModifyArg(
      method = "aiStep",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      ),
      index = 2
   )
   private float iceCaves$effectDamage(ServerLevel level, DamageSource source, float damage) {
      LivingEntity mob = (LivingEntity)(Object)this;
      boolean freezing = mob.hasEffect(ModEffects.FREEZING);
      return freezing && source.is(DamageTypeTags.IS_FREEZING) ? 1.5F : damage;
   }
}
