package spinyq.hitthegym.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * Represents a type of exercise the player can perform (e.g. curls, squats, etc.)
 * Contains information like how to animate the player and what abilities it works out
 * @author spinyq
 *
 */
public abstract class Exercise extends IForgeRegistryEntry.Impl<Exercise> {
	
	public abstract void animate(AbstractClientPlayer player, double liftProgress);

	/**
	 * Called when a player starts performing this exercise
	 */
	public void onAdd(EntityPlayer player) {}
	
	/**
	 * Called when a player stops performing this exercise
	 */
	public void onRemove(EntityPlayer player) {}
	
	public Exercise(ResourceLocation regName) {
		this.setRegistryName(regName);
	}
	
	public static Exercise CURL = new Exercise(new ResourceLocation(HitTheGymMod.MODID, "curl")) {
		
		@Override
		public void animate(AbstractClientPlayer player, double liftProgress) {
			ModelRenderer arm = getActiveArmRenderer(player);
			arm.rotateAngleZ = 0;
			arm.rotateAngleX = (float) (liftProgress / 100.0 * -Math.PI / 2.0);
		}
		
	};
	
	public static Exercise LATERAL = new Exercise(new ResourceLocation(HitTheGymMod.MODID, "lateral")) {

		@Override
		public void animate(AbstractClientPlayer player, double liftProgress) {
			ModelRenderer arm = getActiveArmRenderer(player);
			arm.rotateAngleX = 0;
			int sgn = getActiveHandSide(player) == EnumHandSide.RIGHT ? 1 : -1;
			arm.rotateAngleZ = (float) (liftProgress / 100.0 * sgn * Math.PI / 2.0);
		}
		
	};
	
	public static Exercise SQUAT = new Exercise(new ResourceLocation(HitTheGymMod.MODID, "squat")) {

		@Override
		public void animate(AbstractClientPlayer player, double liftProgress) {
			double progress = liftProgress / 100.0,
					angle = (1.0 - progress) * Math.PI / 4.0,
					bodyHeight = 10.0,
					bodyPosY = bodyHeight / 16.0  * (1.0 - Math.cos(angle)),
					bodyPosZ = -bodyHeight / 16.0 * Math.sin(angle);
			ModelBiped model = getPlayerModel(player);
			// Set arm z rotations to 0
			model.bipedLeftArm.rotateAngleZ = 0;
			model.bipedRightArm.rotateAngleZ = 0;
			// Rotate arms up
			model.bipedLeftArm.rotateAngleX = (float) Math.PI;
			model.bipedRightArm.rotateAngleX = (float) Math.PI;
			// Set body rotation
			model.bipedBody.rotateAngleX = (float) angle;
			// Set body position
			model.bipedBody.offsetY = (float) bodyPosY;
			model.bipedHead.offsetY = (float) bodyPosY;
			model.bipedLeftArm.offsetY = (float) bodyPosY;
			model.bipedRightArm.offsetY = (float) bodyPosY;
			model.bipedBody.offsetZ = (float) bodyPosZ;
			model.bipedHead.offsetZ = (float) bodyPosZ;
			model.bipedLeftArm.offsetZ = (float) bodyPosZ;
			model.bipedRightArm.offsetZ = (float) bodyPosZ;
		}

		@Override
		public void onRemove(EntityPlayer player) {
			// Side check
			if (player.world.isRemote) {
				ModelBiped model = getPlayerModel((AbstractClientPlayer) player);
				model.bipedBody.offsetY = 0;
				model.bipedHead.offsetY = 0;
				model.bipedRightArm.offsetY = 0;
				model.bipedLeftArm.offsetY = 0;
				model.bipedBody.offsetZ = 0;
				model.bipedHead.offsetZ = 0;
				model.bipedRightArm.offsetZ = 0;
				model.bipedLeftArm.offsetZ = 0;
			}
		}
		
	};
	
	@EventBusSubscriber
	public static class Registrar {
		
		@SubscribeEvent
		public static void newRegistry(RegistryEvent.NewRegistry event) {
			HitTheGymMod.log.info("Creating exercise registry...");
			RegistryBuilder<Exercise> builder = new RegistryBuilder<Exercise>();
			builder.setType(Exercise.class);
			builder.setName(new ResourceLocation(HitTheGymMod.MODID, "exercises"));
			builder.create();
		}

		@SubscribeEvent
		public static void registerBeliefs(RegistryEvent.Register<Exercise> event) {
			event.getRegistry().registerAll(CURL, LATERAL, SQUAT);
		}
		
	}
	
	private static RenderPlayer getRenderPlayer(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderManager manager = mc.getRenderManager();
		return manager.getSkinMap().get(player.getSkinType());
	}

	private static ModelBiped getPlayerModel(AbstractClientPlayer player) {
		RenderPlayer render = getRenderPlayer(player);
		if(render != null)
			return render.getMainModel();
		return null;
	}
	
	/**
	 * Retrieves model part based on hand side
	 * @param model
	 * @param hand
	 * @return
	 */
	private static ModelRenderer getArmRenderer(ModelBiped model, EnumHandSide hand) {
		switch (hand) {
		case RIGHT: return model.bipedRightArm;
		case LEFT: return model.bipedLeftArm;
		default: return null; // This should never happen
		}
	}
	
	/**
	 * Gets the physical side of a player's active hand
	 * @param player
	 * @return
	 */
	private static EnumHandSide getActiveHandSide(AbstractClientPlayer player) {
		if (player.getActiveHand() == EnumHand.MAIN_HAND) {
			return player.getPrimaryHand();
		}
		else
		{
			return (player.getPrimaryHand() == EnumHandSide.LEFT) ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
		}
	}
	
	/**
	 * Gets the model renderer of a player's active arm
	 * @param player
	 * @return
	 */
	private static ModelRenderer getActiveArmRenderer(AbstractClientPlayer player) {
		return getArmRenderer(getPlayerModel(player), getActiveHandSide(player));
	}
	
}
