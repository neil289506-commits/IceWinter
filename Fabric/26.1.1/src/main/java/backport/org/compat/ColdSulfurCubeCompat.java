package backport.org.compat;

import net.minecraft.resources.Identifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;

public final class ColdSulfurCubeCompat {
   public static final TagKey<Item> COLD = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("sulfur_cube_archetype/cold"));
   private static final String NATIVE = "net.minecraft.world.entity.monster.cubemob.SulfurCube";
   private static final String BACKPORT = "com.blackgear.vanillabackport.common.level.entities.mob.monster.sulfur_cube.SulfurCube";

   public static boolean supported(LivingEntity entity) {
      String name = entity.getClass().getName();
      return name.equals("net.minecraft.world.entity.monster.cubemob.SulfurCube")
         || name.equals("com.blackgear.vanillabackport.common.level.entities.mob.monster.sulfur_cube.SulfurCube");
   }

   public static boolean hasColdBlock(LivingEntity entity) {
      if (!supported(entity)) {
         return false;
      } else {
         return entity.getClass().getName().equals("net.minecraft.world.entity.monster.cubemob.SulfurCube")
            ? entity.getItemBySlot(EquipmentSlot.BODY).is(COLD)
            : entity.getItemBySlot(EquipmentSlot.CHEST).is(COLD);
      }
   }

   public static List<?> adapt(ItemStack item, List<?> original) {
      if (!item.is(COLD)) {
         return original;
      }

      try {
         boolean nativeCold = false;

         for (Object archetype : original) {
            if (path(archetype).equals("sulfur_cube_archetype/cold")) {
               nativeCold = true;
            }
         }

         List<Object> result = new ArrayList<>();

         for (Object archetype : original) {
            if (!path(archetype).equals("sulfur_cube_archetype/fast_sliding")) {
               result.add(archetype);
            } else if (!nativeCold) {
               result.add(copyRecord(archetype, true));
            }
         }

         return result;
      } catch (ReflectiveOperationException error) {
         throw new IllegalStateException("Cannot adapt the supported Sulfur Cube archetype record", error);
      }
   }

   public static String path(Object archetype) throws ReflectiveOperationException {
      Object items = archetype.getClass().getMethod("items").invoke(archetype);
      if (items instanceof TagKey<?> tag) {
         return tag.location().getPath();
      } else {
         return items instanceof Named<?> named ? named.key().location().getPath() : "";
      }
   }

   private static Object copyRecord(Object source, boolean archetype) throws ReflectiveOperationException {
      RecordComponent[] components = source.getClass().getRecordComponents();
      Class<?>[] types = new Class[components.length];
      Object[] values = new Object[components.length];

      for (int i = 0; i < components.length; i++) {
         RecordComponent component = components[i];
         types[i] = component.getType();
         values[i] = component.getAccessor().invoke(source);
         if (archetype && component.getName().equals("items")) {
            values[i] = COLD;
         }

         if (archetype && component.getName().equals("buoyant")) {
            values[i] = true;
         }

         if (archetype && component.getName().equals("soundSettings")) {
            values[i] = copyRecord(values[i], false);
         }

         if (!archetype && component.getName().equals("pushSound")) {
            values[i] = source.getClass().getMethod("hitSound").invoke(source);
         }
      }

      return source.getClass().getDeclaredConstructor(types).newInstance(values);
   }

   public static void onChangedBlock(ServerLevel level, LivingEntity entity) {
      if (hasColdBlock(entity) && entity.onGround() && !entity.isPassenger()) {
         freezeDisk(level, entity);
      }
   }

   public static void freezeDisk(ServerLevel level, LivingEntity entity) {
      BlockPos center = entity.blockPosition().below();
      BlockState ice = Blocks.FROSTED_ICE.defaultBlockState();

      for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, 0, -3), center.offset(3, 0, 3))) {
         double dx = pos.getX() + 0.5 - entity.getX();
         double dz = pos.getZ() + 0.5 - entity.getZ();
         if (!(dx * dx + dz * dz >= 9.0) && level.getBlockState(pos.above()).isAir()) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.WATER)
               && state.getFluidState().isSource()
               && level.isUnobstructed(ice, pos, CollisionContext.empty())
               && level.setBlockAndUpdate(pos, ice)) {
               level.gameEvent(entity, GameEvent.BLOCK_PLACE, pos);
            }
         }
      }
   }
}
