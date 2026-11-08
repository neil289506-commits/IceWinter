package backport.org.mixin;

import backport.org.compat.FreezeProtection;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Skeleton.class)
abstract class SkeletonFreezeProtectionMixin {
   @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/skeleton/Skeleton;isInPowderSnow:Z"))
   private boolean iceCaves$protectedSkeleton(Skeleton skeleton) {
      return skeleton.isInPowderSnow && !FreezeProtection.wearsProtection(skeleton);
   }
}
