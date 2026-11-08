package backport.org.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
abstract class SnowLayerBlockMixin {
   @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
   private void iceCaves$survivesOnPackedIce(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (level.getBlockState(pos.below()).is(Blocks.PACKED_ICE)) {
         cir.setReturnValue(true);
      }
   }
}
