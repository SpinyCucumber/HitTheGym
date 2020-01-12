package spinyq.hitthegym.common.capability;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import spinyq.hitthegym.common.HitTheGym;
import spinyq.hitthegym.common.ModConstants;
import spinyq.hitthegym.common.capability.CapabilityUtils.MissingCapabilityException;
import spinyq.hitthegym.common.core.Strengths;
import spinyq.hitthegym.common.core.StrengthsHolder;

/**
 * Attaches a "StrengthsHolder" instance to all players using the capability system.
 * @author SpinyQ
 *
 */
@EventBusSubscriber(bus = Bus.MOD)
public class StrengthsCapability {
	
	/**
	 * The actual capability, used to retrieve strengths from a player.
	 */
	@CapabilityInject(StrengthsHolder.class)
	public static final Capability<StrengthsHolder> CAPABILITY = null;
	
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ModConstants.MODID, "strengths");
	
	/**
	 * Exposes the capability.
	 * @author SpinyQ
	 *
	 */
	public static class Provider implements ICapabilityProvider {

		private StrengthsHolder instance;
		
		public Provider() {
			// Just provide the default instance.
			instance = CAPABILITY.getDefaultInstance();
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction direction) {
			return CAPABILITY.orEmpty(capability, LazyOptional.of(() -> instance));
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
			if (event.getObject() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getObject();
				// DEBUG
				HitTheGym.LOGGER.info("Attaching Strengths Capability to player {}", player);
				event.addCapability(RESOURCE_LOCATION, new Provider());
			}
		}
		
	}
	
	/**
	 * Handles making a default capability instance.
	 * @author SpinyQ
	 *
	 */
	public static class Factory implements Callable<StrengthsHolder> {

		@Override
		public StrengthsHolder call() throws Exception {
			return new StrengthsHolder(new Strengths());
		}
		
	}
	
	/**
	 * Handles storing the capability.
	 * @author SpinyQ
	 *
	 */
	public static class Storage implements Capability.IStorage<StrengthsHolder> {

		@Override
		public INBT writeNBT(Capability<StrengthsHolder> capability, StrengthsHolder instance,
				Direction direction) {
			return instance.getStrengths().serializeNBT();
		}

		@Override
		public void readNBT(Capability<StrengthsHolder> capability, StrengthsHolder instance, Direction direction,
				INBT nbt) {
			instance.getStrengths().deserializeNBT((CompoundNBT) nbt);
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
			try {
				StrengthsHolder newCapability = CapabilityUtils.getCapability(event.getPlayer(), CAPABILITY),
					oldCapability = CapabilityUtils.getCapability(event.getOriginal(), CAPABILITY);
				newCapability.setAll(oldCapability);
			} catch (MissingCapabilityException e) {
				throw new RuntimeException("Error copying player strength data.", e);
			}
		}
		
	}
	
	public static void register() {
		CapabilityManager.INSTANCE.register(StrengthsHolder.class, new Storage(), new Factory());
	}
	
	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		register();
	}
	
}
