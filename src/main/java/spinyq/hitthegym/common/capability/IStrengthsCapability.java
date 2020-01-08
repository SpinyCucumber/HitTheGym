package spinyq.hitthegym.common.capability;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import spinyq.hitthegym.common.HitTheGym;
import spinyq.hitthegym.common.ModConstants;
import spinyq.hitthegym.common.core.Strengths;

/**
 * Exposes strengths. Attached to all players.
 * @author SpinyQ
 *
 */
public interface IStrengthsCapability {

	Strengths getStrengths();
	
	void setStrengths(Strengths strengths);
	
	/**
	 * Makes this object identical to other.
	 * Called when players are cloned.
	 * @param other
	 */
	default void setAll(IStrengthsCapability other) {
		setStrengths(other.getStrengths());
	}
	
	/**
	 * The actual capability, used to retrieve strengths from a player.
	 */
	@CapabilityInject(IStrengthsCapability.class)
	public static final Capability<IStrengthsCapability> CAPABILITY = null;
	
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ModConstants.MODID, "strengths");
	
	/**
	 * Default implementation
	 * @author SpinyQ
	 *
	 */
	public static class Impl implements IStrengthsCapability {
		
		private Strengths strengths;

		public Impl(Strengths strengths) {
			this.strengths = strengths;
		}

		@Override
		public Strengths getStrengths() {
			return strengths;
		}

		@Override
		public void setStrengths(Strengths strengths) {
			this.strengths = strengths;
		}
		
	}
	
	/**
	 * Exposes the capability.
	 * @author SpinyQ
	 *
	 */
	public static class Provider implements ICapabilityProvider {

		private IStrengthsCapability instance;
		
		public Provider() {
			// Just provide the default instance.
			instance = CAPABILITY.getDefaultInstance();
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability.equals(CAPABILITY);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return hasCapability(capability, facing) ? CAPABILITY.cast(instance) : null;
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
				HitTheGym.log.info("Attaching Strengths Capability to player {}", player);
				event.addCapability(RESOURCE_LOCATION, new Provider());
			}
		}
		
	}
	
	/**
	 * Handles making a default capability instance.
	 * @author SpinyQ
	 *
	 */
	public static class Factory implements Callable<IStrengthsCapability> {

		@Override
		public IStrengthsCapability call() throws Exception {
			return new Impl(new Strengths());
		}
		
	}
	
	/**
	 * Handles storing the capability.
	 * @author SpinyQ
	 *
	 */
	public static class Storage implements Capability.IStorage<IStrengthsCapability> {

		@Override
		public NBTBase writeNBT(Capability<IStrengthsCapability> capability, IStrengthsCapability instance,
				EnumFacing side) {
			return instance.getStrengths().serializeNBT();
		}

		@Override
		public void readNBT(Capability<IStrengthsCapability> capability, IStrengthsCapability instance, EnumFacing side,
				NBTBase nbt) {
			instance.getStrengths().deserializeNBT((NBTTagCompound) nbt);
		}
		
	}
	
	/**
	 * Copies capability data when players are cloned (i.e. travelling between dimensions)
	 * @author SpinyQ
	 *
	 */
	@Mod.EventBusSubscriber
	public static class CloneHandler {
		
		@SubscribeEvent
		public static void onPlayerClone(PlayerEvent.Clone event) {
			IStrengthsCapability newCapability = event.getEntityLiving().getCapability(CAPABILITY, null),
					oldCapability = event.getOriginal().getCapability(CAPABILITY, null);
			newCapability.setAll(oldCapability);
		}
		
	}
	
	/**
	 * Called during preInit to register the capability.
	 */
	public static void register() {
		CapabilityManager.INSTANCE.register(IStrengthsCapability.class, new Storage(), new Factory());
	}
	
}
