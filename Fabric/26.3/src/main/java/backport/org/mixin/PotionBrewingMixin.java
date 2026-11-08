package backport.org.mixin;

import backport.org.ModItems;
import backport.org.ModPotions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

@Mixin(PotionBrewing.class)
abstract class PotionBrewingMixin {
	@Inject(method = "addVanillaMixes", at = @At("TAIL"))
	private static void icewinter$addFreezingMixes(PotionBrewing.Builder builder, CallbackInfo ci) {
		builder.addMix(Potions.AWKWARD, ModItems.ICE_BALL, ModPotions.FREEZING);
		builder.addMix(Potions.WATER, ModItems.ICE_BALL, Potions.MUNDANE);
		builder.addMix(ModPotions.FREEZING, Items.REDSTONE, ModPotions.LONG_FREEZING);
	}
}
