package backport.org.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrostedIceBlock.class)
abstract class FrostedIceVibrationMixin {
   @Inject(method = "slightlyMelt", at = @At("RETURN"))
   private void iceCaves$emitChange(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (!(Boolean)cir.getReturnValue()) {
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(state));
      }
   }
}
