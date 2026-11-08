package backport.org.compat;

import backport.org.ModEffects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.predicates.MobEffectsPredicate.MobEffectInstancePredicate;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.EffectsChangedTrigger.TriggerInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public final class FreezingAdvancements {
   private static boolean target(String id) {
      return id.equals("minecraft:nether/all_potions") || id.equals("minecraft:nether/all_effects");
   }

   public static Advancement extend(String id, Advancement original) {
      if (!target(id)) {
         return original;
      }

      LinkedHashMap<String, Criterion<?>> criteria = new LinkedHashMap<>(original.criteria());
      criteria.replaceAll(
         (key, criterion) -> {
            if (criterion.triggerInstance() instanceof TriggerInstance effects && !effects.effects().isEmpty()) {
               LinkedHashMap<Holder<MobEffect>, MobEffectInstancePredicate> map = new LinkedHashMap<>(
                  ((MobEffectsPredicate)effects.effects().get()).effectMap()
               );
               map.putIfAbsent(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)ModEffects.FREEZING.value()), new MobEffectInstancePredicate());
               return new Criterion(
                  CriteriaTriggers.EFFECTS_CHANGED, new TriggerInstance(effects.player(), Optional.of(new MobEffectsPredicate(map)), effects.source())
               );
            } else {
               return criterion;
            }
         }
      );
      return new Advancement(original.parent(), original.display(), original.rewards(), criteria, original.requirements(), original.sendsTelemetryEvent());
   }

   public static JsonElement extendJson(String id, JsonElement original) {
      if (target(id) && original.isJsonObject()) {
         JsonObject copy = original.getAsJsonObject().deepCopy();
         JsonObject criteria = copy.getAsJsonObject("criteria");
         if (criteria == null) {
            return original;
         }

         for (JsonElement value : criteria.asMap().values()) {
            if (value.isJsonObject()) {
               JsonObject criterion = value.getAsJsonObject();
               if (criterion.has("trigger") && criterion.get("trigger").getAsString().equals("minecraft:effects_changed")) {
                  JsonObject conditions = criterion.getAsJsonObject("conditions");
                  if (conditions != null && conditions.has("effects")) {
                     conditions.getAsJsonObject("effects").add("minecraft:freezing", new JsonObject());
                  }
               }
            }
         }

         return copy;
      } else {
         return original;
      }
   }
}
