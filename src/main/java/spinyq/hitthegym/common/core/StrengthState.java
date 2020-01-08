package spinyq.hitthegym.common.core;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Information attached to things that have "strength," like players.
 * Each muscle group is mapped to a double, which represents the strength.
 * @author SpinyQ
 *
 */
public class StrengthState implements INBTSerializable<NBTTagCompound> {

	private Map<MuscleGroup, Double> strengths;

	public StrengthState() {
		// Create empty map
		strengths = new HashMap<>();
	}
	
	public double getStrength(MuscleGroup group) {
		return strengths.getOrDefault(group, 0.0);
	}
	
	public void setStrength(MuscleGroup group, double strength) {
		strengths.put(group, strength);
	}
	
	public void addStrength(MuscleGroup group, double amt) {
		setStrength(group, getStrength(group) + amt);
	}

	@Override
	/**
	 * Converts the strength state into NBT for storage.
	 */
	public NBTTagCompound serializeNBT() {
		NBTTagCompound result = new NBTTagCompound();
		// For every strength we have, write a double using the enum value as the key.
		for (Map.Entry<MuscleGroup, Double> entry : strengths.entrySet()) {
			result.setDouble(entry.getKey().name(), entry.getValue());
		}
		return result;
	}

	@Override
	/**
	 * Reads the strength state from NBT.
	 */
	public void deserializeNBT(NBTTagCompound nbt) {
		// For every entry in the tag compound, read a musclegroup and a double.
		for (String key : nbt.getKeySet()) {
			setStrength(MuscleGroup.valueOf(key), nbt.getDouble(key));
		}
	}
	
}
