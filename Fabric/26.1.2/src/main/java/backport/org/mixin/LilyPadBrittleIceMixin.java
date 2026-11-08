package backport.org.mixin;

import backport.org.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LilyPadBlock.class)
abstract class LilyPadBrittleIceMixin {
   @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
   private void iceCaves$brittleIce(BlockState below, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (below.is((Block)ModBlocks.BRITTLE_ICE) && level.getFluidState(pos.above()).isEmpty()) {
         cir.setReturnValue(true);
      }
   }
}
