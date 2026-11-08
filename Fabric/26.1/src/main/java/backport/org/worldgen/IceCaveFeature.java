package backport.org.worldgen;

import backport.org.block.IcicleBlock;
import backport.org.ModBlocks;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class IceCaveFeature extends Feature<NoneFeatureConfiguration> {
   private static final int MAX_DECORATION_Y = 256;

   public IceCaveFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      BlockPos origin = context.origin();
      MutableBlockPos cursor = new MutableBlockPos();
      boolean changed = false;
      int low = level.getMinY() + 6;
      int high = Math.min(256, level.getMaxY() - 6);
      int minX = origin.getX() & -16;
      int minZ = origin.getZ() & -16;
      int maxX = minX + 15;
      int maxZ = minZ + 15;
      IceCaveMask mask = new IceCaveMask(
         minX, low, minZ, maxX, high - 1, maxZ, (x, y, z) -> level.getBiome(new BlockPos(x, y, z)).is(IceCavesWorldgen.ICE_CAVES)
      );
      if (!mask.hasIce()) {
         return false;
      }

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            for (int y = low; y < high; y++) {
               cursor.set(x, y, z);
               if (isCaveStone(level.getBlockState(cursor))) {
                  double blend = mask.coverage(x, y, z);
                  if (!(blend <= 0.0) && !(transitionNoise(cursor) > smooth(blend))) {
                     for (Direction direction : Direction.values()) {
                        if (level.getBlockState(cursor.relative(direction)).isAir() || level.getBlockState(cursor.relative(direction, 2)).isAir()) {
                           if (!touchesOre(level, cursor)) {
                              level.setBlock(cursor, bandMaterial(cursor), 2);
                              changed = true;
                           }
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      List<BlockPos> ceilings = new ArrayList<>();

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            for (int y = low; y < high; y++) {
               cursor.set(x, y, z);
               if (level.getBlockState(cursor).isAir()
                  && level.getBiome(cursor).is(IceCavesWorldgen.ICE_CAVES)
                  && isIceSurface(level.getBlockState(cursor.above()))) {
                  ceilings.add(cursor.immutable());
               }
            }
         }
      }

      int largeAttempts = Math.min(8, ceilings.size());

      for (int attempt = 0; attempt < largeAttempts; attempt++) {
         BlockPos ceiling = ceilings.get(random.nextInt(ceilings.size()));
         if (placeLargeIciclePair(level, random, ceiling, minX, maxX, minZ, maxZ)) {
            changed = true;
         }
      }

      for (BlockPos ceiling : ceilings) {
         if (level.getBlockState(ceiling).isAir() && random.nextFloat() < 0.13F) {
            int length = 3 + random.nextInt(3);
            if (placeIcicle(level, ceiling, Direction.DOWN, length)) {
               changed = true;
            }
         }
      }

      return changed;
   }

   private static BlockState bandMaterial(BlockPos pos) {
      double broad = valueNoise(pos.getX() / 30.0, pos.getY() / 22.0, pos.getZ() / 30.0);
      double medium = valueNoise(pos.getX() / 15.0, pos.getY() / 11.0, pos.getZ() / 15.0);
      double fine = valueNoise(pos.getX() / 7.5, pos.getY() / 5.5, pos.getZ() / 7.5);
      double noise = broad * 0.57 + medium * 0.29 + fine * 0.14;
      noise += Math.sin(pos.getY() * 0.17 + broad * 2.4) * 0.16;
      if (!(noise < -0.6) && !(noise > 0.7)) {
         return !(noise < -0.5) && (!(noise > 0.2) || !(noise < 0.3)) && !(noise > 0.5)
            ? Blocks.PACKED_ICE.defaultBlockState()
            : Blocks.CALCITE.defaultBlockState();
      } else {
         return Blocks.STONE.defaultBlockState();
      }
   }

   private static boolean placeLargeIciclePair(WorldGenLevel level, RandomSource random, BlockPos root, int minX, int maxX, int minZ, int maxZ) {
      int gap = 0;

      while (gap < 48 && level.getBlockState(root.below(gap)).isAir()) {
         gap++;
      }

      if (gap < 11) {
         return false;
      } else {
         int available = gap - 2;
         int ceilingLength = Math.min(available / 2, 6 + random.nextInt(Math.max(1, Math.min(10, available / 2))));
         int floorLength = Math.min(available - ceilingLength, Math.max(4, ceilingLength - 2 + random.nextInt(5)));
         int radius = Math.max(2, Math.min(5, gap / 7));
         if (root.getX() - radius >= minX && root.getX() + radius <= maxX && root.getZ() - radius >= minZ && root.getZ() + radius <= maxZ) {
            buildPackedIceCone(level, root, Direction.DOWN, ceilingLength, radius, minX, maxX, minZ, maxZ);
            BlockPos floorAir = root.below(gap - 1);
            buildPackedIceCone(level, floorAir, Direction.UP, floorLength, Math.max(2, radius - random.nextInt(2)), minX, maxX, minZ, maxZ);
            return true;
         } else {
            return false;
         }
      }
   }

   private static void buildPackedIceCone(
      WorldGenLevel level, BlockPos root, Direction direction, int length, int radius, int minX, int maxX, int minZ, int maxZ
   ) {
      for (int dy = 0; dy < length; dy++) {
         double taper = 1.0 - dy / Math.max(1.0, length - 1.0);
         double currentRadius = Math.max(0.35, radius * taper);
         int rounded = (int)Math.ceil(currentRadius);

         for (int dx = -rounded; dx <= rounded; dx++) {
            for (int dz = -rounded; dz <= rounded; dz++) {
               if (!(dx * dx + dz * dz > currentRadius * currentRadius)) {
                  BlockPos target = root.relative(direction, dy).offset(dx, 0, dz);
                  if (insideChunk(target, minX, maxX, minZ, maxZ)) {
                     BlockPos support = target.relative(direction.getOpposite());
                     if (level.getBlockState(target).isAir() && level.getBlockState(support).isFaceSturdy(level, support, direction)) {
                        level.setBlock(target, Blocks.PACKED_ICE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean touchesOre(WorldGenLevel level, BlockPos pos) {
      for (Direction direction : Direction.values()) {
         if (isOre(level.getBlockState(pos.relative(direction)))) {
            return true;
         }
      }

      return false;
   }

   private static boolean placeIcicle(WorldGenLevel level, BlockPos start, Direction direction, int requestedLength) {
      int length = 0;

      while (length < requestedLength && level.getBlockState(start.relative(direction, length)).isAir()) {
         length++;
      }

      if (length == 0) {
         return false;
      }

      for (int index = 0; index < length; index++) {
         BlockPos target = start.relative(direction, index);
         BlockState state = ModBlocks.ICICLE.defaultBlockState().setValue(IcicleBlock.ATTACHED, index == 0);
         level.setBlock(target, state, 2);
      }

      return true;
   }

   private static boolean insideChunk(BlockPos pos, int minX, int maxX, int minZ, int maxZ) {
      return pos.getX() >= minX && pos.getX() <= maxX && pos.getZ() >= minZ && pos.getZ() <= maxZ;
   }

   private static boolean isIceSurface(BlockState state) {
      return state.is(Blocks.PACKED_ICE) || state.is(Blocks.CALCITE);
   }

   private static boolean isCaveStone(BlockState state) {
      return state.is(Blocks.STONE)
         || state.is(Blocks.DEEPSLATE)
         || state.is(Blocks.TUFF)
         || state.is(Blocks.ANDESITE)
         || state.is(Blocks.DIORITE)
         || state.is(Blocks.GRANITE);
   }

   private static boolean isOre(BlockState state) {
      return state.is(Blocks.COAL_ORE)
         || state.is(Blocks.DEEPSLATE_COAL_ORE)
         || state.is(Blocks.COPPER_ORE)
         || state.is(Blocks.DEEPSLATE_COPPER_ORE)
         || state.is(Blocks.IRON_ORE)
         || state.is(Blocks.DEEPSLATE_IRON_ORE)
         || state.is(Blocks.GOLD_ORE)
         || state.is(Blocks.DEEPSLATE_GOLD_ORE)
         || state.is(Blocks.REDSTONE_ORE)
         || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)
         || state.is(Blocks.LAPIS_ORE)
         || state.is(Blocks.DEEPSLATE_LAPIS_ORE)
         || state.is(Blocks.DIAMOND_ORE)
         || state.is(Blocks.DEEPSLATE_DIAMOND_ORE);
   }

   private static double transitionNoise(BlockPos pos) {
      return (valueNoise(pos.getX() / 9.0, pos.getY() / 7.0, pos.getZ() / 9.0) + 1.0) * 0.5;
   }

   private static double valueNoise(double x, double y, double z) {
      int x0 = (int)Math.floor(x);
      int y0 = (int)Math.floor(y);
      int z0 = (int)Math.floor(z);
      double tx = smooth(x - x0);
      double ty = smooth(y - y0);
      double tz = smooth(z - z0);
      double x00 = lerp(hash(x0, y0, z0), hash(x0 + 1, y0, z0), tx);
      double x10 = lerp(hash(x0, y0 + 1, z0), hash(x0 + 1, y0 + 1, z0), tx);
      double x01 = lerp(hash(x0, y0, z0 + 1), hash(x0 + 1, y0, z0 + 1), tx);
      double x11 = lerp(hash(x0, y0 + 1, z0 + 1), hash(x0 + 1, y0 + 1, z0 + 1), tx);
      return lerp(lerp(x00, x10, ty), lerp(x01, x11, ty), tz);
   }

   private static double hash(int x, int y, int z) {
      long value = x * 341873128712L ^ y * 132897987541L ^ z * 42317861L;
      value ^= value >>> 13;
      value *= 1274126177L;
      value ^= value >>> 16;
      return (value & 16777215L) / 8388607.0 - 1.0;
   }

   private static double smooth(double value) {
      return value * value * (3.0 - 2.0 * value);
   }

   private static double lerp(double a, double b, double amount) {
      return a + (b - a) * amount;
   }
}
