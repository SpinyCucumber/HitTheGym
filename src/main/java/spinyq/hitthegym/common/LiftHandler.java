package spinyq.hitthegym.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * Handles increasing the player's lift progress if they are lifting.
 * @author spinyq
 *
 */
@Mod.EventBusSubscriber
public class LiftHandler {
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		// Only update once per tick
		if (event.phase == TickEvent.Phase.END) return;
		// Get lifter state and update
		event.player.getCapability(ILifter.CAPABILITY, null).getState().tick(event.player);
	}
	
}
