package backport.org.mixin;

import backport.org.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Shulker.class)
abstract class ShulkerBrittleIceMixin {
   @Inject(method = "tick", at = @At("TAIL"))
   private void iceCaves$crackSupportingIce(CallbackInfo ci) {
      Shulker shulker = (Shulker)(Object)this;
      if (!shulker.level().isClientSide() && shulker.getAttachFace() == Direction.DOWN) {
         BlockPos pos = shulker.blockPosition().below();
         BlockState state = shulker.level().getBlockState(pos);
         if (state.is((Block)ModBlocks.BRITTLE_ICE)) {
            state.getBlock().stepOn(shulker.level(), pos, state, shulker);
         }
      }
   }
}
