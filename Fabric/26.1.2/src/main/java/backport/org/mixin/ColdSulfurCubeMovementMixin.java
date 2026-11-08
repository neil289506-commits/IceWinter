package backport.org.mixin;

import backport.org.compat.ColdSulfurCubeCompat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class ColdSulfurCubeMovementMixin {
   @Inject(method = "onChangedBlock", at = @At("TAIL"))
   private void iceCaves$freezeWater(CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)(Object)this;
      if (entity.level() instanceof ServerLevel level) {
         ColdSulfurCubeCompat.onChangedBlock(level, entity);
      }
   }
}
