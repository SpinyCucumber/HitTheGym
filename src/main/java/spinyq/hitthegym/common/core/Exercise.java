package spinyq.hitthegym.common.core;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import spinyq.hitthegym.common.HitTheGym;
import spinyq.hitthegym.common.ModConstants;
import spinyq.hitthegym.common.capability.CapabilityUtils;
import spinyq.hitthegym.common.capability.CapabilityUtils.MissingCapabilityException;
import spinyq.hitthegym.common.capability.StrengthsCapability;

/**
 * Represents a type of exercise the player can perform (e.g. curls, squats, etc.)
 * Contains information like how to animate the player and what abilities it works out
 * @author spinyq
 *
 */
public abstract class Exercise extends ForgeRegistryEntry<Exercise> {
	
	/**
	 * What is "gained" by performing an exercise. Has muscle groups and how much each group is worked.
	 * @author SpinyQ
	 *
	 */
	public static class RepResult {
		
		private ImmutableMap<MuscleGroup, Double> gains;

		/**
		 * @param gains A map containing musclegroups mapped to how much muscle is added to them when a rep is completed.
		 */
		public RepResult(ImmutableMap<MuscleGroup, Double> gains) {
			super();
			this.gains = gains;
		}
		
		public void onRep(Strengths strengths) {
			// Increase strengths
			gains.forEach((group, amt) -> {
				strengths.addStrength(group, amt);
			});
		}
		
		/**
		 * Increases a player's strength values.
		 * @param player
		 */
		public void onRep(PlayerEntity player) {
			player.getCapability(StrengthsCapability.CAPABILITY).ifPresent((cap) -> {
				onRep(cap.getStrengths());
			});
		}
		
	}
	
	/**
	 * A requirement players must meet before doing an exercise
	 * @author SpinyQ
	 *
	 */
	public static class StrengthRequirement {
		
		private ImmutableMap<MuscleGroup, Double> requirements;

		public StrengthRequirement(ImmutableMap<MuscleGroup, Double> requirements) {
			super();
			this.requirements = requirements;
		}
		
		/**
		 * @param strengths
		 * @return Whether the particular strengths satisfy the requirement
		 */
		public boolean isMet(Strengths strengths) {
			for (Map.Entry<MuscleGroup, Double> entry : requirements.entrySet()) {
				if (strengths.getStrength(entry.getKey()) < entry.getValue()) return false;
			}
			return true;
		}

		/**
		 * Overload of isMet that retrieves the capability of a player.
		 * @throws MissingCapabilityException 
		 */
		public boolean isMet(PlayerEntity player) throws MissingCapabilityException {
			return isMet(CapabilityUtils.getCapability(player, StrengthsCapability.CAPABILITY).getStrengths());
		}
		
		public ITextComponent getStatusMessage(Strengths strengths) {
			for (Map.Entry<MuscleGroup, Double> entry : requirements.entrySet()) {
				if (strengths.getStrength(entry.getKey()) < entry.getValue()) {
					// TODO Translate muscle groups
					return new TranslationTextComponent("message.hitthegym.tooweak", entry.getKey().getPluralName());
				}
			}
			// TODO Error
			return null;
		}

		/**
		 * @param player
		 * @return A message to display to the player when they are not strong enough to perform an exercise. If the player is strong enough, returns null.
		 * @throws MissingCapabilityException 
		 */
		public ITextComponent getStatusMessage(PlayerEntity player) throws MissingCapabilityException {
			return getStatusMessage(CapabilityUtils.getCapability(player, StrengthsCapability.CAPABILITY).getStrengths());
		}
		
	}
	
	public abstract void animate(AbstractClientPlayerEntity player, double liftProgress);
	
	public RepResult getResult() {
		return result;
	}

	public StrengthRequirement getRequirement() {
		return requirement;
	}

	/**
	 * Called when a player starts performing this exercise
	 */
	public void onAdd(PlayerEntity player) {}
	
	/**
	 * Called when a player stops performing this exercise
	 */
	public void onRemove(PlayerEntity player) {}
	
	public Exercise(ResourceLocation regName, RepResult result, StrengthRequirement requirement) {
		this.setRegistryName(regName);
		this.result = result;
		this.requirement = requirement;
	}
	
	private RepResult result;
	private StrengthRequirement requirement;
	
	public static Exercise CURL = new Exercise(new ResourceLocation(ModConstants.MODID, "curl"),
			new RepResult(ImmutableMap.of(MuscleGroup.BICEP, 1.0)),
			new StrengthRequirement(ImmutableMap.of())) {
		@Override
		public void animate(AbstractClientPlayerEntity player, double liftProgress) {
			RendererModel arm = getActiveArmRenderer(player);
			arm.rotateAngleZ = 0;
			arm.rotateAngleX = (float) (liftProgress / 100.0 * -Math.PI / 2.0);
		}
		
	};
	
	public static Exercise LATERAL = new Exercise(new ResourceLocation(ModConstants.MODID, "lateral"),
			new RepResult(ImmutableMap.of(MuscleGroup.DELTOID, 1.0)),
			new StrengthRequirement(ImmutableMap.of(MuscleGroup.BICEP, 20.0))) {
		@Override
		public void animate(AbstractClientPlayerEntity player, double liftProgress) {
			RendererModel arm = getActiveArmRenderer(player);
			arm.rotateAngleX = 0;
			int sgn = getActiveHandSide(player) == HandSide.RIGHT ? 1 : -1;
			arm.rotateAngleZ = (float) (liftProgress / 100.0 * sgn * Math.PI / 2.0);
		}
		
	};
	
	public static Exercise SQUAT = new Exercise(new ResourceLocation(ModConstants.MODID, "squat"),
			new RepResult(ImmutableMap.of(MuscleGroup.GLUTEAL, 1.0)),
			new StrengthRequirement(ImmutableMap.of())) {
		@Override
		public void animate(AbstractClientPlayerEntity player, double liftProgress) {
			double progress = liftProgress / 100.0,
					angle = (1.0 - progress) * Math.PI / 4.0,
					bodyHeight = 10.0,
					bodyPosY = bodyHeight / 16.0  * (1.0 - Math.cos(angle)),
					bodyPosZ = -bodyHeight / 16.0 * Math.sin(angle);
			PlayerModel<AbstractClientPlayerEntity> model = getPlayerModel(player);
			// Set arm z rotations to 0
			model.bipedLeftArm.rotateAngleZ = 0;
			model.bipedRightArm.rotateAngleZ = 0;
			// Rotate arms up
			model.bipedLeftArm.rotateAngleX = (float) Math.PI;
			model.bipedRightArm.rotateAngleX = (float) Math.PI;
			// Set body rotation
			model.bipedBody.rotateAngleX = (float) angle;
			// Set body position
			model.bipedBody.rotationPointY = (float) bodyPosY;
			model.bipedHead.rotationPointY = (float) bodyPosY;
			model.bipedLeftArm.rotationPointY = (float) bodyPosY;
			model.bipedRightArm.rotationPointY = (float) bodyPosY;
			model.bipedBody.rotationPointZ = (float) bodyPosZ;
			model.bipedHead.rotationPointZ = (float) bodyPosZ;
			model.bipedLeftArm.rotationPointZ = (float) bodyPosZ;
			model.bipedRightArm.rotationPointZ = (float) bodyPosZ;
		}

		@Override
		public void onRemove(PlayerEntity player) {
			// Side check
			if (player.world.isRemote) {
				PlayerModel<AbstractClientPlayerEntity> model = getPlayerModel((AbstractClientPlayerEntity) player);
				model.bipedBody.rotationPointY = 0;
				model.bipedHead.rotationPointY = 0;
				model.bipedRightArm.rotationPointY = 0;
				model.bipedLeftArm.rotationPointY = 0;
				model.bipedBody.rotationPointZ = 0;
				model.bipedHead.rotationPointZ = 0;
				model.bipedRightArm.rotationPointZ = 0;
				model.bipedLeftArm.rotationPointZ = 0;
			}
		}
		
	};
	
	@EventBusSubscriber(bus = Bus.MOD)
	public static class Registrar {
		
		@SubscribeEvent
		public static void newRegistry(RegistryEvent.NewRegistry event) {
			HitTheGym.LOGGER.info("Creating exercise registry...");
			RegistryBuilder<Exercise> builder = new RegistryBuilder<Exercise>();
			builder.setType(Exercise.class);
			builder.setName(new ResourceLocation(ModConstants.MODID, "exercises"));
			builder.create();
		}

		@SubscribeEvent
		public static void register(RegistryEvent.Register<Exercise> event) {
			event.getRegistry().registerAll(CURL, LATERAL, SQUAT);
		}
		
	}
	
	private static PlayerRenderer getRenderPlayer(AbstractClientPlayerEntity player) {
		EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
		return manager.getSkinMap().get(player.getSkinType());
	}

	private static PlayerModel<AbstractClientPlayerEntity> getPlayerModel(AbstractClientPlayerEntity player) {
		PlayerRenderer render = getRenderPlayer(player);
		if(render != null)
			return render.getEntityModel();
		return null;
	}
	
	/**
	 * Retrieves model part based on hand side
	 * @param model
	 * @param hand
	 * @return
	 */
	private static RendererModel getArmRenderer(PlayerModel<AbstractClientPlayerEntity> model, HandSide hand) {
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
	private static HandSide getActiveHandSide(AbstractClientPlayerEntity player) {
		if (player.getActiveHand() == Hand.MAIN_HAND) {
			return player.getPrimaryHand();
		}
		else
		{
			return (player.getPrimaryHand() == HandSide.LEFT) ? HandSide.RIGHT : HandSide.LEFT;
		}
	}
	
	/**
	 * Gets the model renderer of a player's active arm
	 * @param player
	 * @return
	 */
	private static RendererModel getActiveArmRenderer(AbstractClientPlayerEntity player) {
		return getArmRenderer(getPlayerModel(player), getActiveHandSide(player));
	}
	
}
