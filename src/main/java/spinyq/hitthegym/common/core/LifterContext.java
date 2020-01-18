package spinyq.hitthegym.common.core;

import java.util.List;

import net.minecraft.network.PacketBuffer;

public class LifterContext {

	/**
	 * A list of exercises that can be cycled through when lifting.
	 * @author spinyq
	 *
	 */
	public class ExerciseSet {

		private List<Exercise> list;

		public ExerciseSet(List<Exercise> list) {
			super();
			this.list = list;
		}

		public List<Exercise> getList() {
			return list;
		}
		
	}

	public ExerciseSet exercises;
	public double difficultyMultiplier;
	
	public LifterContext() { }
	
	public LifterContext(List<Exercise> exercises, double difficultyMultiplier) {
		super();
		this.exercises = new ExerciseSet(exercises);
		this.difficultyMultiplier = difficultyMultiplier;
	}
	
	/**
	 * Writes this lifter context into a packet buffer.
	 * Only writes the difficulty multiplier
	 * @param buffer
	 */
	public void write(PacketBuffer buffer) {
		buffer.writeDouble(difficultyMultiplier);
	}
	
	/**
	 * Reads a lifter context from a packet buffer
	 * This method will NOT read the exercise set
	 * @param buffer
	 */
	public void read(PacketBuffer buffer) {
		difficultyMultiplier = buffer.readDouble();
	}
	
}
