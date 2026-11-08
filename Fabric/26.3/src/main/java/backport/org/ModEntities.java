package backport.org;

import backport.org.entity.Frostbite;
import backport.org.entity.IceBall;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	private ModEntities() {}

	public static final EntityType<Frostbite> FROSTBITE = register("frostbite",
		EntityType.Builder.of(Frostbite::new, MobCategory.MONSTER)
			.sized(0.6F, 1.95F)
			.eyeHeight(1.74F)
			.passengerAttachments(2.0125F)
			.ridingOffset(-0.7F)
			.clientTrackingRange(8)
			.notInPeaceful());
	public static final EntityType<IceBall> ICE_BALL = register("ice_ball",
		EntityType.Builder.<IceBall>of(IceBall::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.25F, 0.25F)
			.clientTrackingRange(4)
			.updateInterval(10));

	private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, TwoSixFourBackPort.id(name));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void init() {
		FabricDefaultAttributeRegistry.register(FROSTBITE, Frostbite.createAttributes());
	}
}
