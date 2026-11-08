package backport.org.worldgen;

import backport.org.TwoSixFourBackPort;
import backport.org.block.BrittleIceBlock;
import backport.org.ModBlocks;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class IceCrevasseFeature extends Feature<NoneFeatureConfiguration> {
   private static final TagKey<Block> CARVABLE = TagKey.create(Registries.BLOCK, TwoSixFourBackPort.id("ice_crevasse_replaceables"));

   public IceCrevasseFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   private static int minY(WorldGenLevel level) {
      return level.getMinY();
   }

   private static long key(int x, int z) {
      return (long)z << 32 | x & 4294967295L;
   }

   private static IceCrevasseFeature.Column at(Long2ObjectOpenHashMap<IceCrevasseFeature.Column> columns, int x, int z) {
      return (IceCrevasseFeature.Column)columns.get(key(x, z));
   }

   private static boolean carvable(WorldGenLevel level, BlockPos pos) {
      return level.getBlockState(pos).is(CARVABLE);
   }

   private static boolean barrier(BlockState state) {
      return state.blocksMotion() || state.is(BlockTags.ALL_SIGNS);
   }

   private static IceCrevasseFeature.Surface surface(WorldGenLevel level, BlockPos center) {
      MutableBlockPos cursor = center.above(4).mutable();
      boolean airAbove = level.getBlockState(cursor.above()).isAir();
      int bottom = Math.max(minY(level), cursor.getY() - 8);

      while (cursor.getY() >= bottom) {
         BlockState state = level.getBlockState(cursor);
         if (state.is(CARVABLE) && airAbove) {
            return new IceCrevasseFeature.Surface(cursor.immutable(), state);
         }

         airAbove = state.isAir();
         cursor.move(Direction.DOWN);
      }

      return null;
   }

   private static void appendBranch(
      Long2ObjectOpenHashMap<IceCrevasseFeature.Column> columns, WorldGenLevel level, BlockPos origin, int carveDepth, float yaw, RandomSource random
   ) {
      double cursorX = origin.getX();
      double cursorZ = origin.getZ();
      int previousY = origin.getY();
      float acceleration = 0.0F;

      for (int step = 0; step < 8; step++) {
         cursorX += Math.cos(yaw);
         cursorZ += Math.sin(yaw);
         float turn = (float)random.nextGaussian();
         float magnitude = random.nextFloat();
         acceleration = acceleration * 0.5F + turn * magnitude * 4.0F;
         yaw += acceleration * 0.05F;
         int blockX = Mth.floor(cursorX);
         int blockZ = Mth.floor(cursorZ);
         IceCrevasseFeature.Surface path = surface(level, new BlockPos(blockX, previousY, blockZ));
         if (path != null) {
            float taper = 1.0F - step / 8.0F * 0.6F;
            float radius = Math.max(2.0F * taper, 0.5F);
            int maxDepth = Math.max((int)(carveDepth * taper), 1);
            int reach = Mth.ceil(radius);

            for (int x = blockX - reach; x <= blockX + reach; x++) {
               for (int z = blockZ - reach; z <= blockZ + reach; z++) {
                  int dx = x - blockX;
                  int dz = z - blockZ;
                  if (!(dx * dx + dz * dz > radius * radius) && !columns.containsKey(key(x, z))) {
                     IceCrevasseFeature.Surface candidate = surface(level, new BlockPos(x, path.pos.getY(), z));
                     if (candidate != null) {
                        int depth = 1;
                        MutableBlockPos test = candidate.pos.below().mutable();

                        while (depth < maxDepth && carvable(level, test)) {
                           depth++;
                           test.move(Direction.DOWN);
                        }

                        columns.put(key(x, z), new IceCrevasseFeature.Column(candidate.pos, candidate.original, depth - 1));
                     }
                  }
               }
            }

            previousY = path.pos.getY();
         }
      }
   }

   private static boolean steep(Long2ObjectOpenHashMap<IceCrevasseFeature.Column> columns, IceCrevasseFeature.Column column) {
      for (Direction direction : Plane.HORIZONTAL) {
         BlockPos p = column.pos.relative(direction);
         IceCrevasseFeature.Column neighbor = at(columns, p.getX(), p.getZ());
         if (neighbor != null && Math.abs(column.pos.getY() - neighbor.pos.getY()) > 1) {
            return true;
         }
      }

      return false;
   }

   private static void keepConnected(Long2ObjectOpenHashMap<IceCrevasseFeature.Column> columns, BlockPos origin) {
      IceCrevasseFeature.Column start = at(columns, origin.getX(), origin.getZ());
      if (start != null && !steep(columns, start)) {
         List<IceCrevasseFeature.Column> connected = new ArrayList<>();
         LongOpenHashSet visited = new LongOpenHashSet();
         connected.add(start);
         visited.add(key(origin.getX(), origin.getZ()));

         for (int index = 0; index < connected.size(); index++) {
            for (Direction direction : Plane.HORIZONTAL) {
               BlockPos p = connected.get(index).pos.relative(direction);
               IceCrevasseFeature.Column neighbor = at(columns, p.getX(), p.getZ());
               if (neighbor != null && !visited.contains(key(p.getX(), p.getZ())) && !steep(columns, neighbor)) {
                  connected.add(neighbor);
                  visited.add(key(p.getX(), p.getZ()));
               }
            }
         }

         columns.clear();

         for (IceCrevasseFeature.Column column : connected) {
            columns.put(key(column.pos.getX(), column.pos.getZ()), column);
         }
      } else {
         columns.clear();
      }
   }

   private static boolean sideLiquid(WorldGenLevel level, BlockPos pos) {
      for (Direction direction : Plane.HORIZONTAL) {
         if (!level.getFluidState(pos.relative(direction)).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private static void sealSides(
      WorldGenLevel level, Long2ObjectOpenHashMap<IceCrevasseFeature.Column> columns, IceCrevasseFeature.Column column, BlockPos pos, Block placement
   ) {
      for (Direction direction : Plane.HORIZONTAL) {
         BlockPos p = pos.relative(direction);
         BlockState old = level.getBlockState(p);
         if (!barrier(old)) {
            IceCrevasseFeature.Column neighbor = at(columns, p.getX(), p.getZ());
            if ((neighbor == null || !neighbor.containsY(pos.getY())) && !old.is(placement)) {
               level.setBlock(p, column.original, 2);
            }
         }
      }
   }

   private static void carve(WorldGenLevel level, Long2ObjectOpenHashMap<IceCrevasseFeature.Column> columns, IceCrevasseFeature.Column column, int waterY) {
      boolean changed = false;

      for (int y = column.pos.getY(); y >= column.bottomY(); y--) {
         BlockPos p = new BlockPos(column.pos.getX(), y, column.pos.getZ());
         boolean filled = y <= waterY;
         if (!filled && sideLiquid(level, p)) {
            break;
         }

         Block placement = filled ? Blocks.WATER : Blocks.AIR;
         sealSides(level, columns, column, p, placement);
         level.setBlock(p, placement.defaultBlockState(), 2);
         if (filled && y == column.bottomY()) {
            BlockPos bottom = p.below();
            BlockState old = level.getBlockState(bottom);
            if (!barrier(old) && !old.is(placement)) {
               level.setBlock(bottom, column.original, 2);
            }
         }

         changed = true;
      }

      if (changed) {
         level.setBlock(column.pos, ModBlocks.BRITTLE_ICE.defaultBlockState(), 2);
      }
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      BlockPos origin = context.origin();
      RandomSource random = context.random();
      if (!level.getBiome(origin).is(IceCavesWorldgen.ICE_CAVES)) {
         return false;
      }

      int carveDepth = 6 + random.nextInt(5);
      if (origin.getY() <= minY(level) + carveDepth) {
         return false;
      }

      float yaw = random.nextFloat() * (float) (Math.PI * 2);
      int fillDepth = 4 + random.nextInt(3);
      Long2ObjectOpenHashMap<IceCrevasseFeature.Column> columns = new Long2ObjectOpenHashMap(50);

      for (int direction = 0; direction < 2; direction++) {
         appendBranch(columns, level, origin, carveDepth, yaw + direction * (float) Math.PI, random);
      }

      keepConnected(columns, origin);
      if (columns.size() < 10) {
         return false;
      }

      int lowestSurface = columns.values().stream().mapToInt(c -> c.pos.getY()).min().orElseThrow();
      int waterY = Math.min(origin.getY() - fillDepth, lowestSurface - 1);

      for (IceCrevasseFeature.Column column : columns.values().stream().sorted(Comparator.naturalOrder()).toList()) {
         carve(level, columns, column, waterY);
      }

      return true;
   }

   private record Column(BlockPos pos, BlockState original, int depth) implements Comparable<IceCrevasseFeature.Column> {
      int bottomY() {
         return this.pos.getY() - this.depth;
      }

      boolean containsY(int y) {
         return y >= this.bottomY() && y <= this.pos.getY();
      }

      public int compareTo(IceCrevasseFeature.Column other) {
         int x = Integer.compare(this.pos.getX(), other.pos.getX());
         return x != 0 ? x : Integer.compare(this.pos.getZ(), other.pos.getZ());
      }
   }

   private record Surface(BlockPos pos, BlockState original) {
   }
}
