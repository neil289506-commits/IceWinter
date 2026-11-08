package backport.org.mixin;

import backport.org.block.BrittleIceBlock;
import backport.org.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlock.class)
abstract class WaterOnBlueIceMixin {
   @Inject(method = "onPlace", at = @At("HEAD"))
   private void iceCaves$onWaterPlaced(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
      iceCaves$freeze(state, level, pos);
   }

   @Inject(method = "neighborChanged", at = @At("HEAD"))
   private void iceCaves$onWaterNeighbor(
      BlockState state, Level level, BlockPos pos, Block neighbor, Orientation orientation, boolean movedByPiston, CallbackInfo ci
   ) {
      iceCaves$freeze(state, level, pos);
   }

   private static void iceCaves$freeze(BlockState state, Level level, BlockPos pos) {
      if (!level.isClientSide() && state.getFluidState().is(FluidTags.WATER)) {
         for (Direction direction : Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(direction)).is(Blocks.BLUE_ICE)) {
               level.setBlock(pos, ((BrittleIceBlock)ModBlocks.BRITTLE_ICE).defaultBlockState(), 3);
               return;
            }
         }
      }
   }
}
