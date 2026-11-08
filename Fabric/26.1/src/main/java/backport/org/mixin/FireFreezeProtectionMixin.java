package backport.org.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class FireFreezeProtectionMixin {
   @Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
   private void iceCaves$firePreventsFreezing(CallbackInfoReturnable<Boolean> cir) {
      if (((Entity)(Object)this).isOnFire()) {
         cir.setReturnValue(false);
      }
   }
}
