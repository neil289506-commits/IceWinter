package backport.org.mixin;

import backport.org.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
abstract class FreezingExtinguishSoundMixin {
   @Unique
   private int iceCaves$nextExtinguishSoundTick;

   @Inject(method = "playEntityOnFireExtinguishedSound", at = @At("HEAD"), cancellable = true)
   private void iceCaves$limitExtinguishSound(CallbackInfo ci) {
      Entity entity = (Entity)(Object)this;
      if (entity instanceof LivingEntity living) {
         boolean freezing = living.hasEffect(ModEffects.FREEZING);
         if (freezing) {
            if (!entity.level().isClientSide() && entity.tickCount >= this.iceCaves$nextExtinguishSoundTick) {
               this.iceCaves$nextExtinguishSoundTick = entity.tickCount + 20;
            } else {
               ci.cancel();
            }
         }
      }
   }
}
