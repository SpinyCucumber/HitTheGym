package spinyq.hitthegym.common.core;

/**
 * Holds an instance of a "Strengths" object.
 * @author SpinyQ
 *
 */
public class StrengthsHolder {

	public Strengths getStrengths() {
		return strengths;
	}
	
	public void setStrengths(Strengths strengths) {
		this.strengths = strengths;
	}
	
	/**
	 * Makes this object equal to other. Used when cloning players.
	 * @param other
	 */
	public void setAll(StrengthsHolder other) {
		this.setStrengths(other.getStrengths());
	}
	
	public StrengthsHolder(Strengths strengths) {
		this.strengths = strengths;
	}

	private Strengths strengths;
	
}
