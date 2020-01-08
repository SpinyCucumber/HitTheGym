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
	
	Lifter getState();
	
	/**
	 * Sets the state, and calls handlers.
	 * @param state
	 */
	void setState(Lifter state);
	
	@CapabilityInject(ILifterCapability.class)
	public static final Capability<ILifterCapability> CAPABILITY = null;
	// Used when attaching the capability to players.
	public static final ResourceLocation ID = new ResourceLocation(ModConstants.MODID, "lifter");
	
	public static class Impl implements ILifterCapability {

		private Lifter state;
		
		public Impl(Lifter state) {
			this.state = state;
		}

		@Override
		public Lifter getState() {
			return state;
		}

		@Override
		public void setState(Lifter state) {
			if (this.state != null) this.state.onRemove();
			// Copy the player reference
			state.setPlayer(this.state.getPlayer());
			this.state = state;
			this.state.onAdd();
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
			Lifter state = new Lifter();
			state.setPlayer(player);
			instance = new Impl(state);
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
	 * Handles attaching the capability to players. Must be initialized.
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
				event.addCapability(ID, new Provider(player));
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
	public static class LiftHandler {
		
		@SubscribeEvent
		public static void onPlayerTick(PlayerTickEvent event) {
			// Only update once per tick
			if (event.phase == TickEvent.Phase.END) return;
			// Get lifter state and update
			event.player.getCapability(ILifterCapability.CAPABILITY, null).getState().tick();
		}
		
	}
	
	/**
	 * Called during preInit
	 */
	public static void register() {
		CapabilityManager.INSTANCE.register(ILifterCapability.class, new Storage(), new Factory());
	}
	
}
