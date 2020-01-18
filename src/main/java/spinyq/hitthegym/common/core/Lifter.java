package spinyq.hitthegym.common.core;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spinyq.hitthegym.common.ModSounds;
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
	
	/**
	 * Called when a player transitions to this state
	 */
	public void onAdd() {
		// Do nothing
	}
	
	/**
	 * Called when a player transitions from this state to another
	 */
	public void onRemove() {
		// Do nothing
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
	 * Updates a lifter state using another
	 * @param other
	 */
	public void update(Lifter other) {
		// By default do nothing
	}
	
	/**
	 * Notifies the server that this lifter state has changed and updates it.
	 */
	public void sendToServer() {
		Messages.CHANNEL.sendToServer(new MessageLifterChange(this));
	}
	
	public static class Active extends Lifter {
		
		public Exercise exercise; // When active, lifters are performing a specific exercise
		public double liftProgress; // When active lifters also have a certain position in their rep
		public boolean lifting;
		public double liftRate = 50.0, dropRate = 100.0, maxLiftProgress = 100.0;
		public int timer = 0;
		
		public Active(Exercise exercise) {
			this.exercise = exercise;
			this.liftProgress = 0.0;
			lifting = false;
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
		public void update(Lifter other) {
			super.update(other);
			if (other instanceof Active) {
				exercise = ((Active) other).exercise;
				lifting = ((Active) other).lifting;
			}
		}

		@Override
		public void onAdd() {
			exercise.onAdd(getPlayer());
		}

		@Override
		public void onRemove() {
			exercise.onRemove(getPlayer());
		}

		@Override
		public void tick() {
			// If player is actively lifting, increase lift progress. Else, drop lift. In both cases clamp the total progress
			liftProgress = lifting ? Math.min(liftProgress + liftRate / 20.0, maxLiftProgress)
					: Math.max(liftProgress - dropRate / 20.0, 0.0);
			// ONLY ON THE SERVER, play some sound effects every so often.
			// This way sound is synchronized.
			if (!getPlayer().world.isRemote && lifting) {
				// Update timer
				timer++;
				World world = getPlayer().world;
				if (timer % 40 == 0) {
					Vec3d pos = getPlayer().getEyePosition(0);
					world.playSound(null, pos.x, pos.y, pos.z, ModSounds.LIFT, SoundCategory.VOICE, 1.0f, 1.0f);
				}
			}
			// ONLY ON THE CLIENT, spawn some particles every so often.
			if (getPlayer().world.isRemote && lifting) {
				// Update timer
				timer++;
				World world = getPlayer().world;
				if (timer % 5 == 0) {
					Vec3d pos = getPlayer().getEyePosition(0);
					world.addParticle(ParticleTypes.SPLASH, pos.x, pos.y, pos.z, 5, 5, 5);
				}
			}
		}
		
	}
	
}
