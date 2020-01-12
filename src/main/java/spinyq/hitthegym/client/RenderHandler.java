package spinyq.hitthegym.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spinyq.hitthegym.common.capability.CapabilityUtils;
import spinyq.hitthegym.common.capability.CapabilityUtils.MissingCapabilityException;
import spinyq.hitthegym.common.capability.LifterCapability;

@OnlyIn(Dist.CLIENT)
public class RenderHandler {
	
	public static void setRotationAngles(Entity entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			// Cast to player
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
			// Pass on logic to player's lifting state
			try {
				CapabilityUtils.getCapability(player, LifterCapability.CAPABILITY).getLifter().animate();
			} catch (MissingCapabilityException e) {
				throw new RuntimeException("Error occurred while animating player.", e);
			}
		}
	}
	
}
