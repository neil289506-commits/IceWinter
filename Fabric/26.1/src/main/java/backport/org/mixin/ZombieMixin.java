package backport.org.mixin;

import backport.org.compat.FreezeProtection;
import backport.org.entity.Frostbite;
import backport.org.ModEntities;
import backport.org.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
// EntityType constants are used directly on this target.
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
abstract class ZombieMixin {
   @Unique
   private int iceCaves$powderSnowTicks;
   @Unique
   private int iceCaves$conversionTicks = -1;

   @Inject(method = "tick", at = @At("TAIL"))
   private void iceCaves$convertInPowderSnow(CallbackInfo ci) {
      Zombie zombie = (Zombie)(Object)this;
      if (!(zombie instanceof Frostbite) && zombie.level() instanceof ServerLevel && zombie.isAlive() && !zombie.isNoAi()) {
         if (zombie instanceof Drowned || zombie.getType() == EntityType.ZOMBIE || zombie.getType() == EntityType.HUSK) {
            if (!zombie.isInPowderSnow || FreezeProtection.wearsProtection(zombie)) {
               this.iceCaves$powderSnowTicks = 0;
               this.iceCaves$conversionTicks = -1;
            } else if (this.iceCaves$conversionTicks < 0) {
               if (++this.iceCaves$powderSnowTicks >= 140) {
                  this.iceCaves$conversionTicks = 300;
               }
            } else if (--this.iceCaves$conversionTicks < 0) {
               boolean drowned = zombie instanceof Drowned;
               EntityType<? extends Zombie> target;
               if (drowned) {
                  target = EntityType.ZOMBIE;
               } else {
                  target = (EntityType<? extends Zombie>)ModEntities.FROSTBITE;
               }

               Zombie converted = (Zombie)zombie.convertTo(target, ConversionParams.single(zombie, true, true), result -> {});
               if (converted != null) {
                  converted.playSound((drowned ? ModSounds.ENTITY_DROWNED_CONVERTED_TO_ZOMBIE : ModSounds.ENTITY_ZOMBIE_CONVERTED_TO_FROSTBITE), 1.0F, 1.0F);
               } else {
                  this.iceCaves$conversionTicks = 0;
               }
            }
         }
      }
   }
}
