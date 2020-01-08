package spinyq.hitthegym.common.capability;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import spinyq.hitthegym.common.HitTheGym;
import spinyq.hitthegym.common.ModConstants;
import spinyq.hitthegym.common.core.Lifter;

/**
 * A forge capability that is attached to all players. Contains the lifter state.
 * @author spinyq
 *
 */
public interface ILifterCapability {
	
	Lifter getLifter();
	
	/**
	 * Sets the state, and calls handlers.
	 * @param lifter
	 */
	void setLifter(Lifter lifter);
	
	@CapabilityInject(ILifterCapability.class)
	public static final Capability<ILifterCapability> CAPABILITY = null;
	
	// Used when attaching the capability to players.
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ModConstants.MODID, "lifter");
	
	public static class Impl implements ILifterCapability {

		private Lifter lifter;
		
		public Impl(Lifter state) {
			this.lifter = state;
		}

		@Override
		public Lifter getLifter() {
			return lifter;
		}

		@Override
		public void setLifter(Lifter lifter) {
			if (this.lifter != null) this.lifter.onRemove();
			// Copy the player reference
			lifter.setPlayer(this.lifter.getPlayer());
			this.lifter = lifter;
			this.lifter.onAdd();
		}
		
	}
	
	/**
	 * Exposes the capability by also including a side...
	 * @author spinyq
	 *
	 */
	public static class Provider implements ICapabilityProvider {

		private ILifterCapability instance;
		
		public Provider(EntityPlayer player) {
			// Initialize instance and pass reference to player
			Lifter lifter = new Lifter();
			lifter.setPlayer(player);
			instance = new Impl(lifter);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability.equals(CAPABILITY);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return (hasCapability(capability, facing)) ? CAPABILITY.cast(instance) : null;
		}
		
	}
	
	/**
	 * Handles attaching the capability to players.
	 * @author spinyq
	 *
	 */
	@Mod.EventBusSubscriber
	public static class Attacher {
		
		@SubscribeEvent
		public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
			// Only attach to players
			if (event.getObject() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getObject();
				// DEBUG
				HitTheGym.log.info("Attaching Lifter Capability to player {}", player);
				event.addCapability(RESOURCE_LOCATION, new Provider(player));
			}
		}
		
	}
	
	/**
	 * Handles creating a default ILifter instance.
	 * @author spinyq
	 *
	 */
	public static class Factory implements Callable<ILifterCapability> {

		@Override
		public ILifterCapability call() throws Exception {
			return new Impl(new Lifter());
		}
		
	}
	
	public static class Storage implements Capability.IStorage<ILifterCapability> {

		@Override
		public NBTBase writeNBT(Capability<ILifterCapability> capability, ILifterCapability instance, EnumFacing side) {
			return null; // Do nothing
		}

		@Override
		public void readNBT(Capability<ILifterCapability> capability, ILifterCapability instance, EnumFacing side, NBTBase nbt) {
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
			event.player.getCapability(ILifterCapability.CAPABILITY, null).getLifter().tick();
		}
		
	}
	
	/**
	 * Called during preInit
	 */
	public static void register() {
		CapabilityManager.INSTANCE.register(ILifterCapability.class, new Storage(), new Factory());
	}
	
}
