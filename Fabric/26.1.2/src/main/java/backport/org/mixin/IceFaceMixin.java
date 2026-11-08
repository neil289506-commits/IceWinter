package backport.org.mixin;

import backport.org.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HalfTransparentBlock.class)
abstract class IceFaceMixin {
   @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
   private void iceCaves$cullIceFaces(BlockState state, BlockState adjacent, Direction direction, CallbackInfoReturnable<Boolean> cir) {
      Block self = (Block)(Object)this;
      boolean isFaceOccludingIce = self == Blocks.ICE || self == Blocks.FROSTED_ICE || self == ModBlocks.BRITTLE_ICE;
      if (isFaceOccludingIce && (adjacent.is(Blocks.ICE) || adjacent.is(Blocks.FROSTED_ICE) || adjacent.is((Block)ModBlocks.BRITTLE_ICE))) {
         cir.setReturnValue(true);
      }
   }
}
