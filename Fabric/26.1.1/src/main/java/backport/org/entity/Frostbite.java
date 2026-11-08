package backport.org.entity;

import backport.org.ModEffects;
import backport.org.ModItems;
import backport.org.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
// EntityType constants are used directly on this target.
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

/** Frostbite (26.4): a zombie variant that throws ice balls and freezes what it hits. */
public final class Frostbite extends Zombie implements RangedAttackMob {
	public Frostbite(EntityType<? extends Frostbite> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.POWDER_SNOW, 0.0F);
		this.setPathfindingMalus(PathType.ON_TOP_OF_POWDER_SNOW, 0.0F);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Zombie.createAttributes().add(Attributes.MAX_HEALTH, 24.0).add(Attributes.MOVEMENT_SPEED, 0.21);
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
		if (random.nextFloat() > 0.75F) {
			this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ICE_BALL));
		}
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, this.rangedAttackIntervalMin(), this.rangedAttackIntervalMax(), 16.0F) {
			private boolean ranged;

			@Override
			public boolean canUse() {
				LivingEntity target = Frostbite.this.getTarget();
				if (target != null && Frostbite.this.getMainHandItem().is(ModItems.ICE_BALL)) {
					boolean use = Frostbite.this.distanceToSqr(target) >= (this.ranged ? 9.0 : 25.0) && super.canUse();
					if (use) {
						this.ranged = true;
					}
					return use;
				}
				return false;
			}

			@Override
			public boolean canContinueToUse() {
				LivingEntity target = Frostbite.this.getTarget();
				boolean keep = target != null
					&& Frostbite.this.getMainHandItem().is(ModItems.ICE_BALL)
					&& Frostbite.this.distanceToSqr(target) >= 9.0
					&& super.canContinueToUse();
				if (!keep) {
					this.ranged = false;
				}
				return keep;
			}
		});
	}

	private int rangedAttackIntervalMin() {
		return switch (this.level().getDifficulty()) {
			case PEACEFUL, EASY -> 20;
			case NORMAL -> 60;
			case HARD -> 15;
		};
	}

	private int rangedAttackIntervalMax() {
		return switch (this.level().getDifficulty()) {
			case PEACEFUL, EASY -> 40;
			case NORMAL -> 60;
			case HARD -> 30;
		};
	}

	@Override
	public boolean doHurtTarget(ServerLevel level, Entity target) {
		boolean hit = super.doHurtTarget(level, target);
		if (hit && target instanceof LivingEntity living) {
			living.addEffect(new MobEffectInstance(ModEffects.FREEZING, 60), this);
		}
		return hit;
	}

	@Override
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		if (this.level() instanceof ServerLevel server) {
			IceBall iceBall = new IceBall(this.level(), this, this.getMainHandItem());
			double dy = target.getY(0.3333333333333333) - iceBall.getY();
			double dx = target.getX() - this.getX();
			double dz = target.getZ() - this.getZ();
			double arc = Math.sqrt(dx * dx + dz * dz) * 0.2;
			iceBall.shoot(dx, dy + arc, dz, 1.6F, 14.0F - this.level().getDifficulty().getId() * 4.0F);
			server.addFreshEntity(iceBall);
			this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (0.8F + this.random.nextFloat() * 0.4F));
			this.swing(InteractionHand.MAIN_HAND);
		}
	}

	@Override
	public boolean canFreeze() {
		return false;
	}

	@Override
	public void makeStuckInBlock(BlockState state, Vec3 multiplier) {
		if (!state.is(Blocks.POWDER_SNOW)) {
			super.makeStuckInBlock(state, multiplier);
		}
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		if (effect.is(MobEffects.POISON) || effect.is(MobEffects.REGENERATION)) {
			return false;
		}
		return effect.is(ModEffects.FREEZING) ? false : super.canBeAffected(effect);
	}

	@Override
	protected void doUnderWaterConversion(ServerLevel level) {
		this.playSound(ModSounds.ENTITY_FROSTBITE_CONVERTED_TO_ZOMBIE, 1.0F, 1.0F);
		this.convertToZombieType(level, EntityType.ZOMBIE);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSounds.ENTITY_FROSTBITE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return ModSounds.ENTITY_FROSTBITE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.ENTITY_FROSTBITE_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(ModSounds.ENTITY_FROSTBITE_STEP, 0.15F, 1.0F);
	}
}
