package backport.org.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
abstract class SnowballMixin {
   @Inject(method = "onHitEntity", at = @At("TAIL"))
   private void iceCaves$knockPlayersBack(EntityHitResult hit, CallbackInfo ci) {
      if (hit.getEntity() instanceof Player player) {
         Vec3 motion = ((Snowball)(Object)this).getDeltaMovement();
         double horizontal = Math.max(0.001, Math.sqrt(motion.x * motion.x + motion.z * motion.z));
         player.push(motion.x / horizontal * 0.45, 0.12, motion.z / horizontal * 0.45);
         player.hurtMarked = true;
      }
   }
}
