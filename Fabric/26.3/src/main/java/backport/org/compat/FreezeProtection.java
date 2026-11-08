package backport.org.compat;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public final class FreezeProtection {
   private FreezeProtection() {
   }

   public static boolean wearsProtection(LivingEntity mob) {
      for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         if (mob.getItemBySlot(slot).is(ItemTags.FREEZE_IMMUNE_WEARABLES)) {
            return true;
         }
      }

      return false;
   }
}
