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
	void setState(LifterState state);
	
	@CapabilityInject(ILifter.class)
	public static final Capability<ILifter> CAPABILITY = null;
	// Used when attaching the capability to players.
	public static final ResourceLocation KEY = new ResourceLocation(HitTheGymMod.MODID, "lifter");
	
	public static class Impl implements ILifter {

		private LifterState state;
		
		public Impl(LifterState state) {
			this.state = state;
		}

		@Override
		public LifterState getState() {
			return state;
		}

		@Override
		public void setState(LifterState state) {
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

		private ILifter instance;
		
		public Provider(EntityPlayer player) {
			// Initialize instance and pass reference to player
			LifterState state = new LifterState();
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
				HitTheGymMod.log.info("Attaching Lifter Capability to player {}", player);
				event.addCapability(KEY, new Provider(player));
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
			return new Impl(new LifterState());
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
