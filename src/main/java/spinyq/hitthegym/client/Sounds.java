package spinyq.hitthegym.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import spinyq.hitthegym.common.HitTheGymMod;

public class Sounds {

	public static final Sounds instance = new Sounds();
	
	public static SoundEvent lift;
	
	public void preInit() {
		// Set soundevent locations
		lift = new SoundEvent(new ResourceLocation(HitTheGymMod.MODID, "lift"));
		lift.setRegistryName(new ResourceLocation(HitTheGymMod.MODID, "lift"));
		// Register as event handler
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(lift);
	}
	
}
