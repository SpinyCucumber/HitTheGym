package spinyq.hitthegym.common.capability;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import spinyq.hitthegym.common.HitTheGym;
import spinyq.hitthegym.common.ModConstants;
import spinyq.hitthegym.common.core.Lifter;
import spinyq.hitthegym.common.core.LifterHolder;

/**
 * Attaches a "LifterHolder" instance to all players using the capability system.
 * @author spinyq
 *
 */
@EventBusSubscriber(bus = Bus.MOD)
public class LifterCapability {
	
	@CapabilityInject(LifterHolder.class)
	public static final Capability<LifterHolder> CAPABILITY = null;
	
	// Used when attaching the capability to players.
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ModConstants.MODID, "lifter");
	
	/**
	 * Exposes the capability
	 * @author spinyq
	 *
	 */
	public static class Provider implements ICapabilityProvider {

		private LifterHolder instance;
		
		public Provider(PlayerEntity player) {
			// Initialize instance and pass reference to player
			Lifter lifter = new Lifter();
			lifter.setPlayer(player);
			instance = new LifterHolder(lifter);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return CAPABILITY.orEmpty(cap, LazyOptional.of(() -> instance));
		}
		
	}
	
	/**
	 * Handles attaching the capability to players.
	 * @author spinyq
	 *
	 */
	@EventBusSubscriber
	public static class Attacher {
		
		@SubscribeEvent
		public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
			// Only attach to players
			if (event.getObject() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getObject();
				// DEBUG
				HitTheGym.LOGGER.info("Attaching Lifter Capability to player {}", player);
				event.addCapability(RESOURCE_LOCATION, new Provider(player));
			}
		}
		
	}
	
	/**
	 * Handles creating a default ILifter instance.
	 * @author spinyq
	 *
	 */
	public static class Factory implements Callable<LifterHolder> {

		@Override
		public LifterHolder call() throws Exception {
			return new LifterHolder(new Lifter());
		}
		
	}
	
	public static class Storage implements Capability.IStorage<LifterHolder> {

		@Override
		public INBT writeNBT(Capability<LifterHolder> capability, LifterHolder instance, Direction dir) {
			return null; // Do nothing
		}

		@Override
		public void readNBT(Capability<LifterHolder> capability, LifterHolder instance, Direction dir, INBT nbt) {
			// Do nothing
		}
		
	}
	
	/**
	 * Handles increasing the player's lift progress if they are lifting.
	 * @author spinyq
	 *
	 */
	@Mod.EventBusSubscriber
	public static class TickHandler {
		
		@SubscribeEvent
		public static void onPlayerTick(PlayerTickEvent event) {
			// Only update once per tick
			if (event.phase == TickEvent.Phase.END) return;
			// Get lifter state and update
			event.player.getCapability(LifterCapability.CAPABILITY, null).ifPresent((capability) -> {
				capability.getLifter().tick();
			});
		}
		
	}
	
	public static void register() {
		CapabilityManager.INSTANCE.register(LifterHolder.class, new Storage(), new Factory());
	}
	
	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		register();
	}
	
}
