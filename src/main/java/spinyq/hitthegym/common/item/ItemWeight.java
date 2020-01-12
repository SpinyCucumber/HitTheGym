package spinyq.hitthegym.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import spinyq.hitthegym.client.gui.LiftScreen;
import spinyq.hitthegym.common.core.ExerciseSet;

public abstract class ItemWeight extends Item {

	public ItemWeight(Properties properties) {
		super(properties);
	}

	public abstract ExerciseSet getExerciseSet();
	
	/**
     * Called when the equipped item is right clicked.
     */
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        // Set active hand
        playerIn.setActiveHand(handIn);
        // Open GUI if client
        if (worldIn.isRemote) {
        	// Open GUI with this item's exercises
        	Minecraft.getInstance().displayGuiScreen(new LiftScreen(getExerciseSet()));
        }
        return ActionResult.newResult(ActionResultType.SUCCESS, itemstack);
    }
	
}
