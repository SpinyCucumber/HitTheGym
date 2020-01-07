package spinyq.hitthegym.common.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import spinyq.hitthegym.common.Exercise;
import spinyq.hitthegym.common.ExerciseSet;

public class ItemDumbell extends ItemWeight {
	
	private ExerciseSet exercises = new ExerciseSet(ImmutableList.of(Exercise.CURL, Exercise.LATERAL));
	
	public ItemDumbell() {
		// Set registry name
		setRegistryName("dumbbell");
		setUnlocalizedName("dumbell");
		setMaxStackSize(1);
	}
	
	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("hitthegym:dumbbell"));
	}

	@Override
	public ExerciseSet getExerciseSet() {
		return exercises;
	}
    
}
