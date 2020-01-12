package spinyq.hitthegym.common.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import spinyq.hitthegym.common.ModConstants;
import spinyq.hitthegym.common.core.Exercise;
import spinyq.hitthegym.common.core.ExerciseSet;

public class ItemBarbell extends ItemWeight implements IItemBase {

	private ExerciseSet exercises = new ExerciseSet(ImmutableList.of(Exercise.SQUAT));
	
	public ItemBarbell() {
		super(new Properties().maxStackSize(1));
		// Set registry name
		this.setRegistryName(new ResourceLocation(ModConstants.MODID, "barbell"));
	}

	public void registerModels() {
		Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(this, new ModelResourceLocation(ModConstants.MODID, "barbell"));
	}

	@Override
	public ExerciseSet getExerciseSet() {
		return exercises;
	}
	
}
