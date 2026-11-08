package backport.org;

import backport.org.effect.FreezingMobEffect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ModEffects {
	private ModEffects() {}

	public static final Holder<MobEffect> FREEZING = Registry.registerForHolder(
		BuiltInRegistries.MOB_EFFECT,
		ResourceKey.create(Registries.MOB_EFFECT, TwoSixFourBackPort.id("freezing")),
		new FreezingMobEffect(MobEffectCategory.HARMFUL, 10875635));

	public static void init() {}
}
