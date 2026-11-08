package backport.org.mixin;

import backport.org.block.AmbientIceSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
abstract class IceAmbientMixin {
   @Inject(method = "animateTick", at = @At("TAIL"))
   private void iceCaves$ambientIce(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
      if (state.is(Blocks.ICE)) {
         AmbientIceSounds.animate(level, pos, random);
      }
   }
}
