package spinyq.hitthegym.common.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import spinyq.hitthegym.common.Exercise;
import spinyq.hitthegym.common.ExerciseSet;
import spinyq.hitthegym.common.HitTheGymMod;

public class ItemBarbell extends ItemWeight {

	private ExerciseSet exercises = new ExerciseSet(ImmutableList.of(Exercise.SQUAT));
	
	public ItemBarbell() {
		super();
		// Set registry name
		this.setRegistryName(new ResourceLocation(HitTheGymMod.MODID, "barbell"));
		this.setUnlocalizedName("barbell");
		this.setMaxStackSize(1);
	}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("hitthegym:barbell"));
	}

	@Override
	public ExerciseSet getExerciseSet() {
		return exercises;
	}
	
}
