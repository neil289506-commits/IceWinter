package backport.org.block;

import backport.org.TwoSixFourBackPort;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jspecify.annotations.Nullable;

public class IcicleBlock extends Block {
	public static final MapCodec<IcicleBlock> CODEC = simpleCodec(IcicleBlock::new);
	public static final TagKey<Block> MELTS_ICICLE_ABOVE =
		TagKey.create(Registries.BLOCK, TwoSixFourBackPort.id("melts_icicle_above"));
	public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;

	public IcicleBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(ATTACHED, false));
	}

	public IcicleBlock(BlockState ignored, BlockBehaviour.Properties properties) {
		this(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ATTACHED);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = defaultBlockState();
		BlockPos pos = context.getClickedPos();
		return state.setValue(ATTACHED, context.getLevel().getBlockState(pos.above()).is(Blocks.PACKED_ICE));
	}
}
