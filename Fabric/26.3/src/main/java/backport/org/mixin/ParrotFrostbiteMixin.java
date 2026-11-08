package backport.org.mixin;

import backport.org.ModEntities;
import backport.org.ModSounds;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Parrot.class)
abstract class ParrotFrostbiteMixin {
   @Shadow
   @Final
   @Mutable
   private static Map<EntityType<?>, SoundEvent> MOB_SOUND_MAP;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void iceCaves$imitateFrostbite(CallbackInfo ci) {
      if (!MOB_SOUND_MAP.containsKey(ModEntities.FROSTBITE)) {
         HashMap<EntityType<?>, SoundEvent> updated = new HashMap<>(MOB_SOUND_MAP);
         updated.put((EntityType<?>)ModEntities.FROSTBITE, (SoundEvent)ModSounds.ENTITY_PARROT_IMITATE_FROSTBITE);
         MOB_SOUND_MAP = updated;
      }
   }
}
