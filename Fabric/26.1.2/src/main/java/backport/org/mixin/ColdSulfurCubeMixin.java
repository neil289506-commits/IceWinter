package backport.org.mixin;

import backport.org.compat.ColdSulfurCubeCompat;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"net.minecraft.world.entity.monster.cubemob.SulfurCube", "com.blackgear.vanillabackport.common.level.entities.mob.monster.sulfur_cube.SulfurCube"},
   remap = false
)
abstract class ColdSulfurCubeMixin {
   @Inject(method = "matchingArchetypes", at = @At("RETURN"), cancellable = true, remap = false)
   private void iceCaves$coldArchetype(ItemStack item, CallbackInfoReturnable<List<?>> cir) {
      cir.setReturnValue(ColdSulfurCubeCompat.adapt(item, (List<?>)cir.getReturnValue()));
   }
}
