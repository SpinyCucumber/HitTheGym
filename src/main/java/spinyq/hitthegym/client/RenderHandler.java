package spinyq.hitthegym.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import spinyq.hitthegym.common.capability.LifterCapability;

public class RenderHandler {
	
	public static void setRotationAngles(Entity entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			// Cast to player
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
			// Pass on logic to player's lifting state
			player.getCapability(LifterCapability.CAPABILITY, null).ifPresent((capability) -> {
				capability.getLifter().animate();
			});
		}
	}
	
}
