package spinyq.hitthegym.common.core;

import net.minecraft.entity.player.PlayerEntity;
import spinyq.hitthegym.common.HitTheGym;

public class LifterHolder {

	public Lifter getLifter() {
		return lifter;
	}
	
	/**
	 * Sets the lifter, and calls handlers.
	 * @param lifter
	 */
	public void setLifter(Lifter lifter) {
		// DEBUG
		HitTheGym.LOGGER.info("Changing lifter to {}", lifter);
		// Copy the player reference
		lifter.setPlayer(player);
		this.lifter = lifter;
	}
	
	public LifterHolder(PlayerEntity player) {
		this.player = player;
	}

	private Lifter lifter;
	/**
	 * A reference to the player this object belongs to
	 */
	private PlayerEntity player;
	
}
