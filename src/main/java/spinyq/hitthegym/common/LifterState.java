package spinyq.hitthegym.common;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import spinyq.hitthegym.client.Sounds;

/**
 * Represents the state of a lifter...
 * Lifters can be idle (i.e. not in the lift GUI)
 * or active (i.e. in the lift GUI)
 * @author spinyq
 *
 */
public class LifterState {
	
	/**
	 * Used in networking
	 */
	public static enum Enum {
		IDLE, ACTIVE;
	}

	public static final LifterState IDLE = new LifterState(); // Nothing special about this state
	
	/**
	 * Called during player rendering to set a player's arms' rotation, etc.
	 * @param player
	 */
	public void animate(AbstractClientPlayer player) {
		// By default do nothing
	}
	
	/**
	 * Called when a player transitions to this state
	 */
	public void onAdd(EntityPlayer player) {
		// Do nothing
	}
	
	/**
	 * Called when a player transitions from this state to another
	 */
	public void onRemove(EntityPlayer player) {
		// Do nothing
	}
	
	public Enum getEnum() {
		// By default idling
		return Enum.IDLE;
	}
	
	/**
	 * Called every tick (server-side and client-side)
	 * @param player
	 */
	public void tick(EntityPlayer player) {
		// By default do nothing
	}
	
	/**
	 * Updates a lifter state using another
	 * @param other
	 */
	public void update(LifterState other) {
		// By default do nothing
	}
	
	public static class Active extends LifterState {
		
		public Exercise exercise; // When active, lifters are performing a specific exercise
		public double liftProgress; // When active lifters also have a certain position in their rep
		public boolean lifting;
		public double liftRate = 50.0, dropRate = 100.0, maxLiftProgress = 100.0;
		public int timer = 0;
		
		public Active(Exercise exercise) {
			super();
			this.exercise = exercise;
			this.liftProgress = 0.0;
			lifting = false;
		}

		@Override
		public void animate(AbstractClientPlayer player) {
			exercise.animate(player, liftProgress);
		}

		@Override
		public Enum getEnum() {
			return Enum.ACTIVE;
		}

		@Override
		public void update(LifterState other) {
			super.update(other);
			if (other instanceof Active) {
				exercise = ((Active) other).exercise;
				lifting = ((Active) other).lifting;
			}
		}

		@Override
		public void onAdd(EntityPlayer player) {
			exercise.onAdd(player);
		}

		@Override
		public void onRemove(EntityPlayer player) {
			exercise.onRemove(player);
		}

		@Override
		public void tick(EntityPlayer player) {
			// If player is actively lifting, increase lift progress. Else, drop lift. In both cases clamp the total progress
			liftProgress = lifting ? Math.min(liftProgress + liftRate / 20.0, maxLiftProgress)
					: Math.max(liftProgress - dropRate / 20.0, 0.0);
			// ONLY ON THE SERVER, play some sound effects every so often.
			if (!player.world.isRemote && lifting) {
				// Update timer
				timer++;
				World world = player.world;
				if (timer % 40 == 0) {
					Vec3d pos = player.getPositionEyes(0);
					world.playSound(null, pos.x, pos.y, pos.z, Sounds.lift, SoundCategory.VOICE, 1.0f, 1.0f);
				}
			}
			// ONLY ON THE CLIENT, spawn some particles every so often.
			if (player.world.isRemote && lifting) {
				// Update timer
				timer++;
				World world = player.world;
				if (timer % 5 == 0) {
					Vec3d pos = player.getPositionEyes(0);
					world.spawnParticle(EnumParticleTypes.WATER_SPLASH, pos.x, pos.y, pos.z, 5, 5, 5);
				}
			}
		}
		
	}
	
}
