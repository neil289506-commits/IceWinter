package backport.org.mixin;

import backport.org.ModSounds;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStateBase.class)
abstract class IceSoundMixin {
   @Inject(method = "getSoundType", at = @At("HEAD"), cancellable = true)
   private void iceCaves$useNewIceSounds(CallbackInfoReturnable<SoundType> cir) {
      BlockState state = (BlockState)(Object)this;
      if (state.is(Blocks.ICE)) {
         cir.setReturnValue(ModSounds.ICE_SOUNDS);
      } else if (state.is(Blocks.PACKED_ICE)) {
         cir.setReturnValue(ModSounds.PACKED_ICE_SOUNDS);
      } else if (state.is(Blocks.BLUE_ICE)) {
         cir.setReturnValue(ModSounds.BLUE_ICE_SOUNDS);
      } else if (state.is(Blocks.FROSTED_ICE)) {
         cir.setReturnValue(ModSounds.FROSTED_ICE_SOUNDS);
      }
   }
}
