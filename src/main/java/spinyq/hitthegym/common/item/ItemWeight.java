package spinyq.hitthegym.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import spinyq.hitthegym.client.GuiLift;
import spinyq.hitthegym.common.ExerciseSet;

public abstract class ItemWeight extends Item {

	public abstract ExerciseSet getExerciseSet();
	
	/**
     * Called when the equipped item is right clicked.
     */
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        // Set active hand
        playerIn.setActiveHand(handIn);
        // Open GUI if client
        if (worldIn.isRemote) {
        	// Open GUI with this item's exercises
        	Minecraft.getMinecraft().displayGuiScreen(new GuiLift(getExerciseSet()));
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }
	
}
