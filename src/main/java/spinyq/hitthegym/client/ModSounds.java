package spinyq.hitthegym.client;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import spinyq.hitthegym.common.ModConstants;

/**
 * Handles managing and registering all of the mod sounds.
 * @author SpinyQ
 *
 */
@EventBusSubscriber(bus = Bus.MOD)
public class ModSounds {
	
	public static final SoundEvent LIFT = new SoundEvent(
			new ResourceLocation(ModConstants.MODID, "lift"))
			.setRegistryName(new ResourceLocation(ModConstants.MODID, "lift"));
	
	public static final ImmutableList<SoundEvent> SOUNDS = ImmutableList.of(LIFT);
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		SOUNDS.forEach((sound) -> {
			event.getRegistry().register(sound);
		});
	}
	
}
