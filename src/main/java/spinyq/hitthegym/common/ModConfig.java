package spinyq.hitthegym.common;

import java.util.function.BiFunction;

// TODO Forge config
public class ModConfig {

	/**
	 * How much strength each muscle group starts with.
	 */
	public static final double DEFAULT_STRENGTH = 1.0;

	/**
	 * The minimum amount of strength required to perform an exercise,
	 * represented as a fraction of an exercise's difficulty.
	 */
	public static final double REQUIRED_STRENGTH_MULTIPLIER = 1.0;
	
	/**
	 * How much strength a muscle group gains upon a successful rep,
	 * represented as a fraction of an exercise's difficulty.
	 */
	public static final double REWARD_MULTIPLIER = 0.1;
	
	/**
	 * How lift speed is calculated, where lift speed is the number of reps a player performs in a second.
	 * Default: liftSpeed(difficulty, strength) = log2(strength / difficulty) + 1
	 */
	public static final BiFunction<Double, Double, Double> LIFT_SPEED_FUNC = (difficulty, strength) -> {
		return (Math.log(strength / difficulty) / Math.log(2.0)) + 1.0;
	};
	
	/**
	 * This number is multiplied by lift speed to determine how quickly a player drops a weight.
	 */
	public static final double DROP_SPEED_MULTIPLIER = 2.0;
	
}
