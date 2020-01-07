package spinyq.hitthegym.common;

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

/**
 * A forge capability that is attached to all players. Contains the lifter state.
 * @author spinyq
 *
 */
public interface ILifter {
	
	LifterState getState();
	
	/**
	 * Sets the state, and calls handlers.
	 * @param state
	 */
	void setState(LifterState state, EntityPlayer player);
	
	@CapabilityInject(ILifter.class)
	public static final Capability<ILifter> CAPABILITY = null;
	// Used when attaching the capability to players.
	public static final ResourceLocation KEY = new ResourceLocation(HitTheGymMod.MODID, "lifter");
	
	public static class Impl implements ILifter {

		private LifterState state;
		
		public Impl() {
			// By default nobody lifts when they spawn...
			state = LifterState.IDLE;
		}

		@Override
		public LifterState getState() {
			return state;
		}

		@Override
		public void setState(LifterState state, EntityPlayer player) {
			if (this.state != null) this.state.onRemove(player);
			this.state = state;
			state.onAdd(player);
		}
		
	}
	
	/**
	 * Exposes the capability by also including a side...
	 * @author spinyq
	 *
	 */
	public static class Provider implements ICapabilityProvider {

		private ILifter instance;
		
		public Provider() {
			// Initialize instance
			instance = CAPABILITY.getDefaultInstance();
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
				// DEBUG
				HitTheGymMod.log.info("Attaching Lifter Capability");
				event.addCapability(KEY, new Provider());
			}
		}
		
	}
	
	/**
	 * Handles creating a default ILifter instance.
	 * @author spinyq
	 *
	 */
	public static class Factory implements Callable<ILifter> {

		@Override
		public ILifter call() throws Exception {
			return new Impl();
		}
		
	}
	
	public static class Storage implements Capability.IStorage<ILifter> {

		@Override
		public NBTBase writeNBT(Capability<ILifter> capability, ILifter instance, EnumFacing side) {
			return null; // Do nothing
		}

		@Override
		public void readNBT(Capability<ILifter> capability, ILifter instance, EnumFacing side, NBTBase nbt) {
			// Do nothing
		}
		
	}
	
	public static void register() {
		CapabilityManager.INSTANCE.register(ILifter.class, new Storage(), new Factory());
	}
	
}
