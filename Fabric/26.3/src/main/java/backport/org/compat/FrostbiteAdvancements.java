package backport.org.compat;

import backport.org.ModEntities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.predicates.entity.EntityPredicate.Builder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.KilledTrigger.TriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public final class FrostbiteAdvancements {
   private static final String FROSTBITE = "minecraft:frostbite";

   private FrostbiteAdvancements() {
   }

   public static boolean isTarget(String id) {
      return id.equals("minecraft:adventure/kill_a_mob") || id.equals("minecraft:adventure/kill_all_mobs");
   }

   public static Advancement extend(String id, Advancement original) {
      original = FreezingAdvancements.extend(id, original);
      if (isTarget(id) && !original.criteria().containsKey("minecraft:frostbite") && original.criteria().containsKey("minecraft:zombie")) {
         LinkedHashMap<String, Criterion<?>> criteria = new LinkedHashMap<>(original.criteria());
         Builder entity = Builder.entity();
         entity.of(BuiltInRegistries.ENTITY_TYPE, (EntityType)ModEntities.FROSTBITE);
         criteria.put("minecraft:frostbite", TriggerInstance.playerKilledEntity(entity));
         ArrayList<List<String>> requirements = new ArrayList<>();

         for (List<String> group : original.requirements().requirements()) {
            ArrayList<String> copy = new ArrayList<>(group);
            if (id.endsWith("/kill_a_mob") && copy.contains("minecraft:zombie")) {
               copy.add("minecraft:frostbite");
            }

            requirements.add(copy);
         }

         if (id.endsWith("/kill_all_mobs")) {
            requirements.add(List.of("minecraft:frostbite"));
         }

         return new Advancement(
            original.parent(), original.display(), original.rewards(), criteria, new AdvancementRequirements(requirements), original.sendsTelemetryEvent()
         );
      } else {
         return original;
      }
   }

   public static JsonElement extendJson(String id, JsonElement original) {
      original = FreezingAdvancements.extendJson(id, original);
      if (isTarget(id) && original.isJsonObject()) {
         JsonObject result = original.getAsJsonObject().deepCopy();
         JsonObject criteria = result.getAsJsonObject("criteria");
         if (criteria != null && !criteria.has("minecraft:frostbite") && criteria.has("minecraft:zombie")) {
            criteria.add("minecraft:frostbite", replaceZombie(criteria.get("minecraft:zombie")));
            JsonArray requirements = result.getAsJsonArray("requirements");
            if (requirements == null) {
               requirements = new JsonArray();

               for (String key : criteria.keySet()) {
                  if (!key.equals("minecraft:frostbite")) {
                     JsonArray group = new JsonArray();
                     group.add(key);
                     requirements.add(group);
                  }
               }

               result.add("requirements", requirements);
            }

            if (id.endsWith("/kill_all_mobs")) {
               JsonArray group = new JsonArray();
               group.add("minecraft:frostbite");
               requirements.add(group);
            } else {
               for (JsonElement group : requirements) {
                  boolean zombie = false;

                  for (JsonElement name : group.getAsJsonArray()) {
                     if (name.getAsString().equals("minecraft:zombie")) {
                        zombie = true;
                     }
                  }

                  if (zombie) {
                     group.getAsJsonArray().add("minecraft:frostbite");
                  }
               }
            }

            return result;
         } else {
            return original;
         }
      } else {
         return original;
      }
   }

   private static JsonElement replaceZombie(JsonElement element) {
      if (element.isJsonObject()) {
         JsonObject copy = new JsonObject();
         element.getAsJsonObject().entrySet().forEach(entry -> copy.add((String)entry.getKey(), replaceZombie((JsonElement)entry.getValue())));
         return copy;
      } else if (element.isJsonArray()) {
         JsonArray copy = new JsonArray();
         element.getAsJsonArray().forEach(value -> copy.add(replaceZombie(value)));
         return copy;
      } else {
         return (JsonElement)(element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() && element.getAsString().equals("minecraft:zombie")
            ? new JsonPrimitive("minecraft:frostbite")
            : element.deepCopy());
      }
   }
}
