package backport.org.block;

import backport.org.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class AmbientIceSounds {
   public static boolean canPlay(BlockGetter level, BlockPos pos) {
      if (level.getBlockState(pos.below()).is(Blocks.WATER) && level.getBlockState(pos.above()).isAir()) {
         for (Direction direction : Plane.HORIZONTAL) {
            for (int distance = 1; distance <= 8; distance++) {
               if (!level.getBlockState(pos.relative(direction, distance)).is(Blocks.ICE)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static void animate(Level level, BlockPos pos, RandomSource random) {
      if (random.nextInt(10000) == 0 && canPlay(level, pos)) {
         level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), (SoundEvent)ModSounds.BLOCK_ICE_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      }
   }
}
