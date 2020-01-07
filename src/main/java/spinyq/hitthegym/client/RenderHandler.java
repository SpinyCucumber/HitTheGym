package spinyq.hitthegym.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import spinyq.hitthegym.common.ILifter;

public class RenderHandler {
	
	public static final RenderHandler instance = new RenderHandler();
	
	public static void setRotationAngles(Entity entity) {
		if (entity instanceof AbstractClientPlayer) {
			// Cast to player
			AbstractClientPlayer player = (AbstractClientPlayer) entity;
			// Pass on logic to player's lifting state
			player.getCapability(ILifter.CAPABILITY, null).getState().animate(player);
		}
	}
	
}