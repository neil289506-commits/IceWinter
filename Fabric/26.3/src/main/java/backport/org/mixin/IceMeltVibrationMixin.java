package backport.org.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
abstract class IceMeltVibrationMixin {
   @Inject(method = "melt", at = @At("HEAD"))
   private void iceCaves$meltVibration(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
      level.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
   }
}
