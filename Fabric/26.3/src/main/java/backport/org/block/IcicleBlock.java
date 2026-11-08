package backport.org.block;

import backport.org.TwoSixFourBackPort;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import org.jspecify.annotations.Nullable;

/** Icicle (26.4): a stalactite that grows below packed ice and falls when it would melt. */
public class IcicleBlock extends SpeleothemBlock {
	public static final MapCodec<IcicleBlock> CODEC = simpleCodec(p -> new IcicleBlock(Blocks.PACKED_ICE.defaultBlockState(), p));
	public static final TagKey<Block> MELTS_ICICLE_ABOVE =
		TagKey.create(Registries.BLOCK, TwoSixFourBackPort.id("melts_icicle_above"));
	public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;

	public IcicleBlock(BlockState blockToGrowOn, BlockBehaviour.Properties properties) {
		super(blockToGrowOn, properties);
		this.registerDefaultState(this.defaultBlockState().setValue(ATTACHED, false));
	}

	@Override
	public MapCodec<? extends SpeleothemBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ATTACHED);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state == null) {
			return null;
		}
		return state.setValue(ATTACHED, isStartPos(state, context.getLevel(), context.getClickedPos()));
	}

	@Override
	protected int getStalactiteLandingSound() {
		return 1045;
	}

	@Override
	protected int getMaxGrowthLength() {
		return 4;
	}

	@Override
	protected boolean canGrow(LevelReader level, BlockPos pos) {
		return super.canGrow(level, pos) && !wouldMelt(level, pos) && isStalactite(level.getBlockState(pos));
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!isStartPos(state, level, pos) && wouldMelt(level, pos)) {
			if (!isUpTip(state) && (!isTipState(state, true) || level.getBlockState(pos.below()).isAir())) {
				dropAsFallingStalactite(state, level, pos);
			} else {
				level.destroyBlock(pos, true);
			}
		} else {
			super.randomTick(state, level, pos, random);
		}
	}

	private static boolean isUpTip(BlockState state) {
		return state.is(BlockTags.SPELEOTHEMS) && state.getValue(TIP_DIRECTION) == Direction.UP;
	}

	private static boolean isTipState(BlockState state, boolean includeMergedTip) {
		if (!state.is(BlockTags.SPELEOTHEMS)) {
			return false;
		}
		SpeleothemThickness thickness = state.getValue(THICKNESS);
		return thickness == SpeleothemThickness.TIP || includeMergedTip && thickness == SpeleothemThickness.TIP_MERGE;
	}

	private static boolean isStartPos(BlockState state, LevelReader level, BlockPos pos) {
		return isStalactiteStartPos(state, level, pos)
			|| isUpTip(state) && !level.getBlockState(pos.below()).is(state.getBlock());
	}

	private static void dropAsFallingStalactite(BlockState state, ServerLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos fallPos = pos.mutable();
		BlockState fallState = state;
		while (isStalactite(fallState)) {
			FallingBlockEntity entity = FallingBlockEntity.fall(level, fallPos, fallState);
			if (isTipState(fallState, true)) {
				int size = Math.max(1 + pos.getY() - fallPos.getY(), 6);
				entity.setHurtsEntities(1.0F * size, 40);
				break;
			}
			fallPos.move(Direction.DOWN);
			fallState = level.getBlockState(fallPos);
		}
	}

	private boolean wouldMelt(LevelReader level, BlockPos pos) {
		return level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)
			|| level.getBrightness(LightLayer.BLOCK, pos) > 4
			|| level.getBlockState(pos.below()).is(MELTS_ICICLE_ABOVE)
			|| level.getBlockState(pos.below(2)).is(MELTS_ICICLE_ABOVE);
	}
}
