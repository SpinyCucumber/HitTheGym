package spinyq.hitthegym.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import spinyq.hitthegym.common.capability.ILifter;
import spinyq.hitthegym.common.core.ExerciseSet;
import spinyq.hitthegym.common.core.LifterState;
import spinyq.hitthegym.common.core.LifterState.Active;

public class GuiLift extends GuiScreen {
	
	private static final int LIFT_BUTTON = 0, CYCLE_BUTTON = 1;
	
	private Active lifterState;
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
		lifterState = new Active(exercises.getList().get(iExercise));
		Minecraft.getMinecraft().player.getCapability(ILifter.CAPABILITY, null).setState(lifterState);
		lifterState.sendToServer();
	}

	@Override
	public void onGuiClosed() {
		// When GUI is closed, set lifter state back to idle
		LifterState newState = new LifterState();
		Minecraft.getMinecraft().player.getCapability(ILifter.CAPABILITY, null).setState(newState);
		newState.sendToServer();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		// Draw bar... first bind texture
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("hitthegym:textures/gui.png"));
		// Draw interior of bar using lift progress
		int barHeight = (int) (64 * lifterState.liftProgress / lifterState.maxLiftProgress);
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
			if (lifterState.canUseExercise()) {
				lifterState.lifting = true;
				lifterState.sendToServer();
			}
			// If not, tell them they are not strong enough
			else {
				ITextComponent text = new TextComponentString(lifterState.exercise.getStatusMessage(lifterState.getPlayer()));
				lifterState.getPlayer().sendStatusMessage(text, true);
			}
		}
		// Cycle exercise if right click
		if (lifterState.liftProgress == 0.0 && mouseButton == CYCLE_BUTTON) {
			iExercise = (iExercise + 1) % exercises.getList().size();
			lifterState.exercise = exercises.getList().get(iExercise);
			lifterState.sendToServer();
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		// Stop lifting if left click and lifting
		if (state == LIFT_BUTTON && lifterState.lifting) {
			lifterState.lifting = false;
			lifterState.sendToServer();
		}
	}
	
}
