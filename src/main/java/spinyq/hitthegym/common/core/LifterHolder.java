package spinyq.hitthegym.common.core;

public class LifterHolder {

	public Lifter getLifter() {
		return lifter;
	}
	
	/**
	 * Sets the lifter, and calls handlers.
	 * @param lifter
	 */
	public void setLifter(Lifter lifter) {
		if (this.lifter != null) this.lifter.onRemove();
		// Copy the player reference
		lifter.setPlayer(this.lifter.getPlayer());
		this.lifter = lifter;
		this.lifter.onAdd();
	}
	
	public LifterHolder(Lifter lifter) {
		super();
		this.lifter = lifter;
	}

	private Lifter lifter;
	
}
