package backport.org.mixin;

import backport.org.ModBlocks;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class SnowBrickWallToolMixin {
   @Inject(method = "isCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
   private void iceCaves$snowWallTool(BlockState state, CallbackInfoReturnable<Boolean> cir) {
      if (state.is((Block)ModBlocks.SNOW_BRICK_WALL)) {
         cir.setReturnValue(((ItemStack)(Object)this).is(ItemTags.SHOVELS));
      }
   }

   @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
   private void iceCaves$snowWallSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
      if (state.is((Block)ModBlocks.SNOW_BRICK_WALL) && !((ItemStack)(Object)this).is(ItemTags.SHOVELS)) {
         cir.setReturnValue(1.0F);
      }
   }
}
