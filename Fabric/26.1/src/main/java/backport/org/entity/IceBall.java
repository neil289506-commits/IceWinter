package backport.org.entity;

import backport.org.ModEntities;
import backport.org.ModItems;
import backport.org.ModSounds;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Ice Ball projectile (26.4): like a snowball, but deals damage scaled by its speed. */
public class IceBall extends ThrowableItemProjectile {
	public IceBall(EntityType<? extends IceBall> type, Level level) {
		super(type, level);
	}

	public IceBall(Level level, LivingEntity mob, ItemStack itemStack) {
		super(ModEntities.ICE_BALL, mob, level, itemStack);
	}

	public IceBall(Level level, double x, double y, double z, ItemStack itemStack) {
		super(ModEntities.ICE_BALL, x, y, z, level, itemStack);
	}

	@Override
	protected Item getDefaultItem() {
		return ModItems.ICE_BALL;
	}

	private ParticleOptions getParticle() {
		ItemStack item = this.getItem();
		return item.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(item));
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			ParticleOptions particle = this.getParticle();
			for (int i = 0; i < 8; i++) {
				this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult) {
		super.onHitEntity(hitResult);
		Entity entity = hitResult.getEntity();
		float damage = Math.max(0.0F, (float)Math.ceil(this.getDeltaMovement().length() * 1.395));
		if (this.level() instanceof ServerLevel server) {
			entity.hurtServer(server, this.damageSources().thrown(this, this.getOwner()), damage);
		}
	}

	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		if (!this.level().isClientSide()) {
			this.playSound(ModSounds.ENTITY_ICE_BALL_BREAK, 0.5F, 1.0F / (0.8F + this.random.nextFloat() * 0.4F));
			this.level().broadcastEntityEvent(this, (byte)3);
			this.discard();
		}
	}
}
