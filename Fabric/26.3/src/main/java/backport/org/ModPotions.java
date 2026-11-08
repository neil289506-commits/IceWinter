package backport.org;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public final class ModPotions {
	private ModPotions() {}

	public static final Holder<Potion> FREEZING = register("freezing", 3600);
	public static final Holder<Potion> LONG_FREEZING = register("long_freezing", 9600);

	private static Holder<Potion> register(String id, int duration) {
		ResourceKey<Potion> key = ResourceKey.create(Registries.POTION, TwoSixFourBackPort.id(id));
		return Registry.registerForHolder(BuiltInRegistries.POTION, key,
			new Potion("freezing", new MobEffectInstance(ModEffects.FREEZING, duration)));
	}

	public static void init() {}
}
