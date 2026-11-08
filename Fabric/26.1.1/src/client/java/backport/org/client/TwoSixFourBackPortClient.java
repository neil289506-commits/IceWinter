package backport.org.client;

import backport.org.ModEntities;
import backport.org.TwoSixFourBackPort;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class TwoSixFourBackPortClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRenderers.register(ModEntities.ICE_BALL, ThrownItemRenderer::new);
		EntityRenderers.register(ModEntities.FROSTBITE, FrostbiteRenderer::new);
	}

	/** Zombie renderer with the Frostbite skin. */
	public static class FrostbiteRenderer extends ZombieRenderer {
		private static final Identifier TEXTURE = TwoSixFourBackPort.id("textures/entity/zombie/frostbite.png");
		private static final Identifier BABY_TEXTURE = TwoSixFourBackPort.id("textures/entity/zombie/frostbite_baby.png");

		public FrostbiteRenderer(EntityRendererProvider.Context context) {
			super(context);
		}

		@Override
		public Identifier getTextureLocation(ZombieRenderState state) {
			return state.isBaby ? BABY_TEXTURE : TEXTURE;
		}
	}
}
