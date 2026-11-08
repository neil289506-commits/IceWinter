package backport.org.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/** Freezing (26.4): adds one tick of powder-snow freezing every tick while active. */
public class FreezingMobEffect extends MobEffect {
	public FreezingMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity mob, int amplification) {
		if (mob.canFreeze()) {
			mob.setTicksFrozen(Math.min(mob.getTicksRequiredToFreeze() + 2, mob.getTicksFrozen() + 3));
		}
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
		return true;
	}
}
