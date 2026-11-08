package backport.org.worldgen;

public final class IceCaveMask {
   private final int gridX;
   private final int gridY;
   private final int gridZ;
   private final double[][][] density;
   private final boolean hasIce;

   public IceCaveMask(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IceCaveMask.BiomeSampler sampler) {
      this.gridX = Math.floorDiv(minX, 4);
      this.gridY = Math.floorDiv(minY, 4);
      this.gridZ = Math.floorDiv(minZ, 4);
      int nx = Math.floorDiv(maxX, 4) - this.gridX + 2;
      int ny = Math.floorDiv(maxY, 4) - this.gridY + 2;
      int nz = Math.floorDiv(maxZ, 4) - this.gridZ + 2;
      boolean[][][] raw = new boolean[nx + 2][ny + 2][nz + 2];
      boolean found = false;

      for (int x = 0; x < nx + 2; x++) {
         for (int y = 0; y < ny + 2; y++) {
            for (int z = 0; z < nz + 2; z++) {
               boolean ice = sampler.isIceCave((this.gridX + x - 1) * 4, (this.gridY + y - 1) * 4, (this.gridZ + z - 1) * 4);
               raw[x][y][z] = ice;
               found |= ice;
            }
         }
      }

      this.hasIce = found;
      this.density = new double[nx][ny][nz];
      if (found) {
         int[] weights = new int[]{1, 2, 1};

         for (int x = 0; x < nx; x++) {
            for (int y = 0; y < ny; y++) {
               for (int z = 0; z < nz; z++) {
                  int total = 0;

                  for (int dx = 0; dx < 3; dx++) {
                     for (int dy = 0; dy < 3; dy++) {
                        for (int dz = 0; dz < 3; dz++) {
                           if (raw[x + dx][y + dy][z + dz]) {
                              total += weights[dx] * weights[dy] * weights[dz];
                           }
                        }
                     }
                  }

                  this.density[x][y][z] = total / 64.0;
               }
            }
         }
      }
   }

   public boolean hasIce() {
      return this.hasIce;
   }

   public double coverage(int x, int y, int z) {
      int ix = Math.floorDiv(x, 4) - this.gridX;
      int iy = Math.floorDiv(y, 4) - this.gridY;
      int iz = Math.floorDiv(z, 4) - this.gridZ;
      double tx = smooth(Math.floorMod(x, 4) / 4.0);
      double ty = smooth(Math.floorMod(y, 4) / 4.0);
      double tz = smooth(Math.floorMod(z, 4) / 4.0);
      double a = lerp(this.density[ix][iy][iz], this.density[ix + 1][iy][iz], tx);
      double b = lerp(this.density[ix][iy + 1][iz], this.density[ix + 1][iy + 1][iz], tx);
      double c = lerp(this.density[ix][iy][iz + 1], this.density[ix + 1][iy][iz + 1], tx);
      double d = lerp(this.density[ix][iy + 1][iz + 1], this.density[ix + 1][iy + 1][iz + 1], tx);
      return lerp(lerp(a, b, ty), lerp(c, d, ty), tz);
   }

   private static double smooth(double t) {
      return t * t * (3.0 - 2.0 * t);
   }

   private static double lerp(double a, double b, double t) {
      return a + (b - a) * t;
   }

   @FunctionalInterface
   public interface BiomeSampler {
      boolean isIceCave(int var1, int var2, int var3);
   }
}
