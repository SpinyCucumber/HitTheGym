package spinyq.hitthegym.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import spinyq.hitthegym.common.ModConstants;

/**
 * Handles managing and registering all of the mod sounds.
 * @author SpinyQ
 *
 */
@Mod.EventBusSubscriber
public class ModSounds {
	
	public static SoundEvent lift;
	
	public static void preInit() {
		// Set soundevent locations
		lift = new SoundEvent(new ResourceLocation(ModConstants.MODID, "lift"));
		lift.setRegistryName(new ResourceLocation(ModConstants.MODID, "lift"));
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(lift);
	}
	
}
