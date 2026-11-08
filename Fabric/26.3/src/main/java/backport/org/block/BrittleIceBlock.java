package backport.org.block;

import backport.org.ModSounds;
import backport.org.TwoSixFourBackPort;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;

/**
 * Brittle Ice (26.4): cracks in four stages when living entities walk on it, then breaks.
 * Re-implemented against 26.2; behaviour follows the 26.4 snapshot 6 logic.
 */
public class BrittleIceBlock extends IceBlock {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	public static final TagKey<EntityType<?>> DOES_NOT_BREAK =
		TagKey.create(Registries.ENTITY_TYPE, TwoSixFourBackPort.id("does_not_break_brittle_ice"));
	private static final int TICK_DELAY = 24;

	public BrittleIceBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
		return neighborState.is(Blocks.ICE) || neighborState.is(Blocks.FROSTED_ICE) || super.skipRendering(state, neighborState, direction);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState onState, Entity entity) {
		if (level instanceof ServerLevel serverLevel
			&& shouldEntityIncreaseCracking(entity, pos, serverLevel)
			&& !serverLevel.getBlockTicks().hasScheduledTick(pos, this)) {
			if (slightlyMelt(onState, serverLevel, pos)) {
				propagateMelting(serverLevel, pos);
			}
			serverLevel.scheduleTick(pos, this, TICK_DELAY);
		}
		super.stepOn(level, pos, onState, entity);
	}

	private boolean shouldEntityIncreaseCracking(Entity entity, BlockPos pos, ServerLevel level) {
		if (!(entity instanceof LivingEntity) || entity.is(DOES_NOT_BREAK)) {
			return false;
		}
		return entity instanceof Player player
			? !level.getServer().isUnderSpawnProtection(level, pos, player)
			: level.getGameRules().get(GameRules.MOB_GRIEFING);
	}

	private void propagateMelting(ServerLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
		for (Direction direction : Direction.values()) {
			neighborPos.setWithOffset(pos, direction);
			BlockState neighbour = level.getBlockState(neighborPos);
			if (neighbour.is(this) && !slightlyMelt(neighbour, level, neighborPos)) {
				level.scheduleTick(neighborPos, this, TICK_DELAY);
			}
		}
	}

	/** Advances the crack stage; returns true when the block was destroyed. */
	private boolean slightlyMelt(BlockState state, Level level, BlockPos pos) {
		int age = state.getValue(AGE);
		if (age < 3) {
			level.setBlock(pos, state.setValue(AGE, age + 1), 2);
			level.playSound(null, pos, ModSounds.BLOCK_BRITTLE_ICE_CRACK, SoundSource.BLOCKS, 1.0F, 1.0F);
			if (age == 0) {
				level.levelEvent(2001, pos, Block.getId(state));
			}
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
			return false;
		}
		level.destroyBlock(pos, false);
		return true;
	}
}
