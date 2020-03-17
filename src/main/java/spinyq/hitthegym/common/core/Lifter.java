package spinyq.hitthegym.common.core;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spinyq.hitthegym.common.HitTheGym;
import spinyq.hitthegym.common.ModConfig;
import spinyq.hitthegym.common.ModSounds;
import spinyq.hitthegym.common.capability.CapabilityUtils;
import spinyq.hitthegym.common.capability.CapabilityUtils.MissingCapabilityException;
import spinyq.hitthegym.common.capability.StrengthsCapability;
import spinyq.hitthegym.common.network.MessageLifterChange;
import spinyq.hitthegym.common.network.Messages;

/**
 * The state of a "lifter"
 * Lifters can be idle (i.e. not in the lift GUI)
 * or active (i.e. in the lift GUI)
 * @author spinyq
 *
 */
public class Lifter {
	
	/**
	 * Used in networking to differentiate between types
	 */
	public static enum Type {
		IDLE, ACTIVE;
	}

	/**
	 * A reference to the player this state is attached to
	 */
	private PlayerEntity player;
	
	/**
	 * @return The player that this state is attached to
	 */
	public PlayerEntity getPlayer() {
		return player;
	}
	
	public void setPlayer(PlayerEntity player) {
		this.player = player;
	}
	
	/**
	 * Called during player rendering to set a player's arms' rotation, etc.
	 * @param playerID
	 */
	@OnlyIn(Dist.CLIENT)
	public void animate() {
		// By default do nothing
	}
	
	public Type getType() {
		// By default idling
		return Type.IDLE;
	}
	
	/**
	 * Called every tick (server-side and client-side)
	 * @param playerID
	 */
	public void tick() {
		// By default do nothing
	}
	
	/**
	 * Notifies the server that this lifter state has changed and updates it.
	 */
	public void sendToServer() {
		Messages.CHANNEL.sendToServer(new MessageLifterChange(this));
	}
	
	public static class Active extends Lifter {
		
		/**
		 * The specific exercise the player is performing
		 */
		private Exercise exercise;
		/**
		 * A lifting context includes an exercise set and how difficult the weight is to lift.
		 * The current exercise should be part of the exercise set.
		 */
		private LifterContext context;
		/**
		 * A reference'to the player's "strengths" object, used to determine lift speed.
		 */
		private Strengths strengths;
		/**
		 * Active lifters have a position in their rep. Clamped between 0.0 and 1.0.
		 */
		public double liftProgress;
		/**
		 * Whether the lifter has done a rep. A rep is defined is transitioning from liftProgress = 0
		 * to liftProgress = 1.
		 */
		private boolean repCompleted;
		public boolean lifting;
		private double liftSpeed;
		
		private int timer = 0;
		
		public Active() {
			liftProgress = 0.0;
			lifting = false;
		}
		
		public Active(LifterContext context) {
			this();
			this.context = context;
		}

		@Override
		public void animate() {
			exercise.getAnimator().animate((AbstractClientPlayerEntity) getPlayer(), liftProgress);
		}

		@Override
		public Type getType() {
			return Type.ACTIVE;
		}
		
		@Override
		public void setPlayer(PlayerEntity player) {
			super.setPlayer(player);
			// Cache strengths object
			try {
				strengths = CapabilityUtils.getCapability(player, StrengthsCapability.CAPABILITY).getStrengths();
				// Also cache liftspeed if exercise is set
				updateLiftSpeed();
			} catch (MissingCapabilityException e) {
				throw new RuntimeException("Error while caching strengths object.", e);
			}
		}

		public Exercise getExercise() {
			return exercise;
		}

		public void setExercise(Exercise exercise) {
			if (this.exercise != null) this.exercise.onRemove(getPlayer());
			this.exercise = exercise;
			this.exercise.onAdd(getPlayer());
			// Cache the lift speed if player is set
			updateLiftSpeed();
		}

		public LifterContext getContext() {
			return context;
		}

		public void setContext(LifterContext context) {
			this.context = context;
		}

		@Override
		public void tick() {
			// If player is actively lifting, increase lift progress. Else, drop lift. In both cases clamp the total progress
			double newProgress = lifting ? Math.min(liftProgress + liftSpeed / 20.0, 1.0)
					: Math.max(liftProgress - liftSpeed * ModConfig.DROP_SPEED_MULTIPLIER / 20.0, 0.0);
			// Determine if the player just completed a rep and reward
			if (isTop(newProgress) && !isTop(liftProgress)) {
				if (!repCompleted) {
					// Only play sounds on the server
					if (!getPlayer().world.isRemote) playSound();
					exercise.getReward().reward(strengths, context);
					repCompleted = true;
				}
			}
			if (isBottom(newProgress) && !isBottom(liftProgress)) {
				if (repCompleted) repCompleted = false;
			}
			// Only on the client, add some particles every so often.
			if (getPlayer().world.isRemote && lifting) {
				timer++;
				if (timer % 5 == 0) addParticles();
			}
		}
		
		private void updateLiftSpeed() {
			if (strengths != null && exercise != null) {
				liftSpeed = exercise.getSpeedProvider().getLiftSpeed(strengths, context);
				// DEBUG
				HitTheGym.LOGGER.info("New lift speed: {}", liftSpeed);
			}
		}
		
		private void playSound() {
			Vec3d pos = getPlayer().getEyePosition(0);
			getPlayer().world.playSound(null, pos.x, pos.y, pos.z, ModSounds.LIFT, SoundCategory.VOICE, 1.0f, 1.0f);
		}
		
		private void addParticles() {
			Vec3d pos = getPlayer().getEyePosition(0);
			getPlayer().world.addParticle(ParticleTypes.SPLASH, pos.x, pos.y, pos.z, 5, 5, 5);
		}
		
		private static boolean isTop(double progress) {
			return progress == 1.0;
		}
		
		private static boolean isBottom(double progress) {
			return progress == 0.0;
		}
		
	}
	
}
