package backport.org.mixin;

import backport.org.compat.FrostbiteAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AdvancementHolder.class)
abstract class FrostbiteAdvancementMixin {
   @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
   private static Advancement iceCaves$addFrostbite(Advancement value, Identifier id, Advancement original) {
      return FrostbiteAdvancements.extend(id.toString(), value);
   }
}
