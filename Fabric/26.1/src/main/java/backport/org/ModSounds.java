package backport.org;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

/** Sound events and block sound types for the backported 26.4 content. */
public final class ModSounds {
	private ModSounds() {}

	public static final SoundEvent BLOCK_ICE_CRYSTAL_BREAK = register("block.ice_crystal.break");
	public static final SoundEvent BLOCK_ICE_CRYSTAL_FALL = register("block.ice_crystal.fall");
	public static final SoundEvent BLOCK_ICE_CRYSTAL_HIT = register("block.ice_crystal.hit");
	public static final SoundEvent BLOCK_ICE_CRYSTAL_PLACE = register("block.ice_crystal.place");
	public static final SoundEvent BLOCK_ICE_CRYSTAL_STEP = register("block.ice_crystal.step");
	public static final SoundEvent BLOCK_ICE_BREAK = register("block.ice.break");
	public static final SoundEvent BLOCK_ICE_FALL = register("block.ice.fall");
	public static final SoundEvent BLOCK_ICE_HIT = register("block.ice.hit");
	public static final SoundEvent BLOCK_ICE_PLACE = register("block.ice.place");
	public static final SoundEvent BLOCK_ICE_STEP = register("block.ice.step");
	public static final SoundEvent BLOCK_ICICLE_BREAK = register("block.icicle.break");
	public static final SoundEvent BLOCK_ICICLE_FALL = register("block.icicle.fall");
	public static final SoundEvent BLOCK_ICICLE_HIT = register("block.icicle.hit");
	public static final SoundEvent BLOCK_ICICLE_LAND = register("block.icicle.land");
	public static final SoundEvent BLOCK_ICICLE_PLACE = register("block.icicle.place");
	public static final SoundEvent BLOCK_ICICLE_STEP = register("block.icicle.step");
	public static final SoundEvent ENTITY_FROSTBITE_AMBIENT = register("entity.frostbite.ambient");
	public static final SoundEvent ENTITY_FROSTBITE_CONVERTED_TO_ZOMBIE = register("entity.frostbite.converted_to_zombie");
	public static final SoundEvent ENTITY_FROSTBITE_DEATH = register("entity.frostbite.death");
	public static final SoundEvent ENTITY_FROSTBITE_HURT = register("entity.frostbite.hurt");
	public static final SoundEvent ENTITY_FROSTBITE_STEP = register("entity.frostbite.step");
	public static final SoundEvent ENTITY_ICE_BALL_BREAK = register("entity.ice_ball.break");
	public static final SoundEvent ENTITY_ICE_BALL_THROW = register("entity.ice_ball.throw");
	public static final SoundEvent ENTITY_ZOMBIE_CONVERTED_TO_FROSTBITE = register("entity.zombie.converted_to_frostbite");
	public static final SoundEvent BLOCK_BLUE_ICE_BREAK = register("block.blue_ice.break");
	public static final SoundEvent BLOCK_BLUE_ICE_FALL = register("block.blue_ice.fall");
	public static final SoundEvent BLOCK_BLUE_ICE_HIT = register("block.blue_ice.hit");
	public static final SoundEvent BLOCK_BLUE_ICE_PLACE = register("block.blue_ice.place");
	public static final SoundEvent BLOCK_BLUE_ICE_STEP = register("block.blue_ice.step");
	public static final SoundEvent BLOCK_BRITTLE_ICE_BREAK = register("block.brittle_ice.break");
	public static final SoundEvent BLOCK_BRITTLE_ICE_FALL = register("block.brittle_ice.fall");
	public static final SoundEvent BLOCK_BRITTLE_ICE_HIT = register("block.brittle_ice.hit");
	public static final SoundEvent BLOCK_BRITTLE_ICE_PLACE = register("block.brittle_ice.place");
	public static final SoundEvent BLOCK_BRITTLE_ICE_STEP = register("block.brittle_ice.step");
	public static final SoundEvent BLOCK_BRITTLE_ICE_CRACK = register("block.brittle_ice.crack");
	public static final SoundEvent BLOCK_FROSTED_ICE_BREAK = register("block.frosted_ice.break");
	public static final SoundEvent BLOCK_FROSTED_ICE_FALL = register("block.frosted_ice.fall");
	public static final SoundEvent BLOCK_FROSTED_ICE_HIT = register("block.frosted_ice.hit");
	public static final SoundEvent BLOCK_FROSTED_ICE_PLACE = register("block.frosted_ice.place");
	public static final SoundEvent BLOCK_FROSTED_ICE_STEP = register("block.frosted_ice.step");
	public static final SoundEvent BLOCK_PACKED_ICE_BREAK = register("block.packed_ice.break");
	public static final SoundEvent BLOCK_PACKED_ICE_FALL = register("block.packed_ice.fall");
	public static final SoundEvent BLOCK_PACKED_ICE_HIT = register("block.packed_ice.hit");
	public static final SoundEvent BLOCK_PACKED_ICE_PLACE = register("block.packed_ice.place");
	public static final SoundEvent BLOCK_PACKED_ICE_STEP = register("block.packed_ice.step");
	public static final SoundEvent ENTITY_DROWNED_CONVERTED_TO_ZOMBIE = register("entity.drowned.converted_to_zombie");
	public static final SoundEvent BLOCK_ICE_AMBIENT = register("block.ice.ambient");
	public static final SoundEvent BLOCK_ICE_CRYSTAL_AMBIENT = register("block.ice_crystal.ambient");
	public static final SoundEvent BLOCK_CHISELED_SOULSHALE_BREAK = register("block.chiseled_soulshale.break");
	public static final SoundEvent BLOCK_CHISELED_SOULSHALE_FALL = register("block.chiseled_soulshale.fall");
	public static final SoundEvent BLOCK_CHISELED_SOULSHALE_HIT = register("block.chiseled_soulshale.hit");
	public static final SoundEvent BLOCK_CHISELED_SOULSHALE_PLACE = register("block.chiseled_soulshale.place");
	public static final SoundEvent BLOCK_CHISELED_SOULSHALE_STEP = register("block.chiseled_soulshale.step");
	public static final SoundEvent ENTITY_PARROT_IMITATE_FROSTBITE = register("entity.parrot.imitate.frostbite");
	public static final SoundEvent MUSIC_DISC_FLURRY = register("music_disc.flurry");

	public static final SoundType ICE_SOUNDS = soundType(BLOCK_ICE_BREAK, BLOCK_ICE_STEP, BLOCK_ICE_PLACE, BLOCK_ICE_HIT, BLOCK_ICE_FALL);
	public static final SoundType PACKED_ICE_SOUNDS = soundType(BLOCK_PACKED_ICE_BREAK, BLOCK_PACKED_ICE_STEP, BLOCK_PACKED_ICE_PLACE, BLOCK_PACKED_ICE_HIT, BLOCK_PACKED_ICE_FALL);
	public static final SoundType BLUE_ICE_SOUNDS = soundType(BLOCK_BLUE_ICE_BREAK, BLOCK_BLUE_ICE_STEP, BLOCK_BLUE_ICE_PLACE, BLOCK_BLUE_ICE_HIT, BLOCK_BLUE_ICE_FALL);
	public static final SoundType FROSTED_ICE_SOUNDS = soundType(BLOCK_FROSTED_ICE_BREAK, BLOCK_FROSTED_ICE_STEP, BLOCK_FROSTED_ICE_PLACE, BLOCK_FROSTED_ICE_HIT, BLOCK_FROSTED_ICE_FALL);
	public static final SoundType BRITTLE_ICE_SOUNDS = soundType(BLOCK_BRITTLE_ICE_BREAK, BLOCK_BRITTLE_ICE_STEP, BLOCK_BRITTLE_ICE_PLACE, BLOCK_BRITTLE_ICE_HIT, BLOCK_BRITTLE_ICE_FALL);
	public static final SoundType ICICLE_SOUNDS = soundType(BLOCK_ICICLE_BREAK, BLOCK_ICICLE_STEP, BLOCK_ICICLE_PLACE, BLOCK_ICICLE_HIT, BLOCK_ICICLE_FALL);
	public static final SoundType ICE_CRYSTAL_SOUNDS = soundType(BLOCK_ICE_CRYSTAL_BREAK, BLOCK_ICE_CRYSTAL_STEP, BLOCK_ICE_CRYSTAL_PLACE, BLOCK_ICE_CRYSTAL_HIT, BLOCK_ICE_CRYSTAL_FALL);
	public static final SoundType SOULSHALE_SOUNDS = soundType(BLOCK_CHISELED_SOULSHALE_BREAK, BLOCK_CHISELED_SOULSHALE_STEP, BLOCK_CHISELED_SOULSHALE_PLACE, BLOCK_CHISELED_SOULSHALE_HIT, BLOCK_CHISELED_SOULSHALE_FALL);

	private static SoundType soundType(SoundEvent breakSound, SoundEvent step, SoundEvent place, SoundEvent hit, SoundEvent fall) {
		return new SoundType(1.0F, 1.0F, breakSound, step, place, hit, fall);
	}

	private static SoundEvent register(String name) {
		var id = TwoSixFourBackPort.id(name);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	public static void init() {}
}
