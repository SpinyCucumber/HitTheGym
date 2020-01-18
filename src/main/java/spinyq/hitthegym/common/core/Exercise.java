package spinyq.hitthegym.common.core;

import java.util.Map;

import com.google.common.collect.ImmutableList;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import spinyq.hitthegym.common.HitTheGym;
import spinyq.hitthegym.common.ModConstants;

/**
 * Represents a type of exercise the player can perform (e.g. curls, squats, etc.)
 * Contains information like how to animate the player and what abilities it works out
 * @author spinyq
 *
 */
public class Exercise extends ForgeRegistryEntry<Exercise> {
	
	/**
	 * Something the player gains when completing an exercise.
	 * @author SpinyQ
	 *
	 */
	public static interface IExerciseReward {
		
		void reward(Strengths strengths);
		
	}
	
	/**
	 * A condition that must be satisfied in order to perform an exercise.
	 * @author SpinyQ
	 *
	 */
	public static interface IExerciseRequirement {
		
		boolean isMet(Strengths strengths);
		
		ITextComponent getStatusMessage(Strengths strengths);
		
	}
	
	/**
	 * Animates a player performing an exercise.
	 * @author SpinyQ
	 *
	 */
	@OnlyIn(Dist.CLIENT)
	public static interface IExerciseAnimator {
		
		void animate(AbstractClientPlayerEntity player, double liftProgress);
		default void start(AbstractClientPlayerEntity player) { }
		default void end(AbstractClientPlayerEntity player) { }
		
	}
	
	public static class ExerciseStats implements IExerciseReward, IExerciseRequirement {
		
		private ImmutableMap<MuscleGroup, Double> difficulty;

		public ExerciseStats(ImmutableMap<MuscleGroup, Double> difficulty) {
			this.difficulty = difficulty;
		}
		
		/**
		 * @param strengths
		 * @return Whether the particular strengths satisfy the requirement
		 */
		@Override
		public boolean isMet(Strengths strengths) {
			for (Map.Entry<MuscleGroup, Double> entry : difficulty.entrySet()) {
				if (strengths.getStrength(entry.getKey()) < entry.getValue()) return false;
			}
			return true;
		}
		
		@Override
		public ITextComponent getStatusMessage(Strengths strengths) {
			for (Map.Entry<MuscleGroup, Double> entry : difficulty.entrySet()) {
				if (strengths.getStrength(entry.getKey()) < entry.getValue()) {
					// TODO Translate muscle groups
					return new TranslationTextComponent("message.hitthegym.tooweak", entry.getKey().getPluralName());
				}
			}
			// TODO Error
			return null;
		}

		@Override
		public void reward(Strengths strengths) {
			// Increase muscle strengths
			for (Map.Entry<MuscleGroup, Double> entry : difficulty.entrySet()) {
				strengths.addStrength(entry.getKey(), entry.getValue());
			}
		}
		
	}

	/**
	 * Called when a player starts performing this exercise
	 */
	public void onAdd(PlayerEntity player) {
		if (player.world.isRemote) animator.start((AbstractClientPlayerEntity) player);
	}
	
	/**
	 * Called when a player stops performing this exercise
	 */
	public void onRemove(PlayerEntity player) {
		if (player.world.isRemote) animator.end((AbstractClientPlayerEntity) player);
	}
	
	public IExerciseReward getReward() {
		return reward;
	}

	public IExerciseRequirement getRequirement() {
		return requirement;
	}

	public IExerciseAnimator getAnimator() {
		return animator;
	}

	public Exercise(ResourceLocation name, IExerciseReward reward, IExerciseRequirement requirement, IExerciseAnimator animator) {
		this.setRegistryName(name);
		this.reward = reward;
		this.requirement = requirement;
		this.animator = animator;
	}
	
	// Might make a builder class or something IDK.
	public Exercise(ResourceLocation name, ExerciseStats stats, IExerciseAnimator animator) {
		this(name, stats, stats, animator);
	}

	private IExerciseReward reward;
	private IExerciseRequirement requirement;
	private IExerciseAnimator animator;
	
	public static Exercise CURL = new Exercise(new ResourceLocation(ModConstants.MODID, "curl"),
		new ExerciseStats(ImmutableMap.of(MuscleGroup.BICEP, 1.0)),
		new IExerciseAnimator() {
			@Override
			public void animate(AbstractClientPlayerEntity player, double liftProgress) {
				RendererModel arm = getActiveArmRenderer(player);
				arm.rotateAngleZ = 0;
				arm.rotateAngleX = (float) (liftProgress / 100.0 * -Math.PI / 2.0);
			}
		}
	);
	
	public static Exercise LATERAL = new Exercise(new ResourceLocation(ModConstants.MODID, "lateral"),
		new ExerciseStats(ImmutableMap.of(MuscleGroup.DELTOID, 1.0)),
		new IExerciseAnimator() {
			@Override
			public void animate(AbstractClientPlayerEntity player, double liftProgress) {
				RendererModel arm = getActiveArmRenderer(player);
				arm.rotateAngleX = 0;
				int sgn = getActiveHandSide(player) == HandSide.RIGHT ? 1 : -1;
				arm.rotateAngleZ = (float) (liftProgress / 100.0 * sgn * Math.PI / 2.0);
			}
		}
	);
	
	public static Exercise SQUAT = new Exercise(new ResourceLocation(ModConstants.MODID, "squat"),
		new ExerciseStats(ImmutableMap.of(MuscleGroup.GLUTEAL, 1.0)),
		new IExerciseAnimator() {
			@Override
			public void animate(AbstractClientPlayerEntity player, double liftProgress) {
				// TODO Abstract some of this out
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
			public void end(AbstractClientPlayerEntity player) {
				PlayerModel<AbstractClientPlayerEntity> model = getPlayerModel(player);
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
	);
	
	public static final ImmutableList<Exercise> EXERCISES = ImmutableList.of(CURL, LATERAL, SQUAT);
	
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
			EXERCISES.forEach((exercise) -> {
				event.getRegistry().register(exercise);
			});
		}
		
	}
	
	// Maybe move these methods to a utility class
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
