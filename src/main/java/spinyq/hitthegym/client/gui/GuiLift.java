package spinyq.hitthegym.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import spinyq.hitthegym.common.capability.ILifterCapability;
import spinyq.hitthegym.common.core.ExerciseSet;
import spinyq.hitthegym.common.core.Lifter;
import spinyq.hitthegym.common.core.Lifter.Active;

public class GuiLift extends GuiScreen {
	
	private static final int LIFT_BUTTON = 0, CYCLE_BUTTON = 1;
	
	private Active lifter;
	// The list of exercises to cycle through.
	private ExerciseSet exercises;
	// The current exercise.
	private int iExercise;
	
	public GuiLift(ExerciseSet exercises) {
		super();
		// When GUI is opened, set lifter state to active
		this.exercises = exercises;
		iExercise = 0;
		// Start with first exercise
		lifter = new Active(exercises.getList().get(iExercise));
		Minecraft.getMinecraft().player.getCapability(ILifterCapability.CAPABILITY, null).setLifter(lifter);
		lifter.sendToServer();
	}

	@Override
	public void onGuiClosed() {
		// When GUI is closed, set lifter state back to idle
		Lifter newState = new Lifter();
		Minecraft.getMinecraft().player.getCapability(ILifterCapability.CAPABILITY, null).setLifter(newState);
		newState.sendToServer();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		// Draw bar... first bind texture
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("hitthegym:textures/gui.png"));
		// Draw interior of bar using lift progress
		int barHeight = (int) (64 * lifter.liftProgress / lifter.maxLiftProgress);
		drawTexturedModalRect(this.width - 32, this.height - barHeight, 32, 64 - barHeight, 32, barHeight);
		// Draw bar exterior
		drawTexturedModalRect(this.width - 32, this.height - 64, 0, 0, 32, 64);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Start lifting if left click
		if (mouseButton == LIFT_BUTTON) {
			// Check to see if the player is strong enough
			// If they are, start lifting.
			if (lifter.canUseExercise()) {
				lifter.lifting = true;
				lifter.sendToServer();
			}
			// If not, tell them they are not strong enough
			else {
				ITextComponent text = new TextComponentString(lifter.exercise.getStatusMessage(lifter.getPlayer()));
				lifter.getPlayer().sendStatusMessage(text, true);
			}
		}
		// Cycle exercise if right click
		if (lifter.liftProgress == 0.0 && mouseButton == CYCLE_BUTTON) {
			iExercise = (iExercise + 1) % exercises.getList().size();
			lifter.exercise = exercises.getList().get(iExercise);
			lifter.sendToServer();
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		// Stop lifting if left click and lifting
		if (state == LIFT_BUTTON && lifter.lifting) {
			lifter.lifting = false;
			lifter.sendToServer();
		}
	}
	
}
