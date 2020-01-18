package spinyq.hitthegym.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spinyq.hitthegym.common.capability.CapabilityUtils;
import spinyq.hitthegym.common.capability.CapabilityUtils.MissingCapabilityException;
import spinyq.hitthegym.common.capability.LifterCapability;
import spinyq.hitthegym.common.capability.StrengthsCapability;
import spinyq.hitthegym.common.core.Lifter;
import spinyq.hitthegym.common.core.Lifter.Active;
import spinyq.hitthegym.common.core.LifterContext;
import spinyq.hitthegym.common.core.LifterHolder;
import spinyq.hitthegym.common.core.StrengthsHolder;

@OnlyIn(Dist.CLIENT)
public class LiftScreen extends Screen {
	
	private static final int LIFT_BUTTON = 0, CYCLE_BUTTON = 1;
	
	// A reference to the player's capabilities
	private LifterHolder lifterHolder;
	private StrengthsHolder strengthsHolder;
	private Active lifter;
	// The context of the lift... contains the exercise set and difficulty.
	private LifterContext context;
	// The current exercise.
	private int iExercise;
	
	public LiftScreen(LifterContext context) {
		// Initialize the screen with a title.
		super(new TranslationTextComponent("lift.title"));
		this.context = context;
	}

	@Override
	protected void init() {
		// Retrieve capabilities for easier access.
		try {
			lifterHolder = CapabilityUtils.getCapability(this.getMinecraft().player, LifterCapability.CAPABILITY);
			strengthsHolder = CapabilityUtils.getCapability(this.getMinecraft().player, StrengthsCapability.CAPABILITY);
		} catch (MissingCapabilityException e) {
			throw new RuntimeException("Could not initialize lift screen.", e);
		}
		// When GUI is opened, set lifter state to active
		// Start with first exercise
		iExercise = 0;
		lifter = new Active(context, context.exercises.getList().get(iExercise));
		lifterHolder.setLifter(lifter);
		lifter.sendToServer();
	}

	@Override
	public void onClose() {
		super.onClose();
		// When GUI is closed, set lifter state back to idle
		Lifter newLifter = new Lifter();
		lifterHolder.setLifter(newLifter);
		newLifter.sendToServer();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		// Draw bar... first bind texture
		this.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("hitthegym:textures/gui.png"));
		// Draw interior of bar using lift progress
		int barHeight = (int) (64 * lifter.liftProgress / lifter.maxLiftProgress);
		blit(this.width - 32, this.height - barHeight, 32, 64 - barHeight, 32, barHeight);
		// Draw bar exterior
		blit(this.width - 32, this.height - 64, 0, 0, 32, 64);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Start lifting if left click
		if (mouseButton == LIFT_BUTTON) {
			// Check to see if the player is strong enough
			// If they are, start lifting.
			if (lifter.exercise.getRequirement().isMet(strengthsHolder.getStrengths())) {
				lifter.lifting = true;
				lifter.sendToServer();
			}
			// If not, tell them they are not strong enough
			else {
				ITextComponent text = lifter.exercise.getRequirement().getStatusMessage(strengthsHolder.getStrengths());
				lifter.getPlayer().sendStatusMessage(text, true);
			}
			return true;
		}
		// Cycle exercise if right click
		else if (lifter.liftProgress == 0.0 && mouseButton == CYCLE_BUTTON) {
			iExercise = (iExercise + 1) % context.exercises.getList().size();
			lifter.exercise = context.exercises.getList().get(iExercise);
			lifter.sendToServer();
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int state) {
		if (super.mouseReleased(mouseX, mouseY, state)) {
			return true;
		}
		// Stop lifting if left click and lifting
		else if (state == LIFT_BUTTON && lifter.lifting) {
			lifter.lifting = false;
			lifter.sendToServer();
			return true;
		}
		else {
			return false;
		}
	}
	
}
