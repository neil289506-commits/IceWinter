package backport.org.block;

import backport.org.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Ice Crystal (26.4): an amethyst-cluster-like block that chimes while standing on packed ice. */
public final class IceCrystalBlock extends AmethystClusterBlock {
	public IceCrystalBlock(BlockBehaviour.Properties props) {
		super(7.0F, 10.0F, props);
	}

	public static boolean canPlayAmbient(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos.below()).is(Blocks.PACKED_ICE);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(250) == 0 && canPlayAmbient(level, pos)) {
			level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.BLOCK_ICE_CRYSTAL_AMBIENT, SoundSource.AMBIENT, 1.0F, 1.0F, false);
		}
	}
}
