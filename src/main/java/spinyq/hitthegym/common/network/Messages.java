package spinyq.hitthegym.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import spinyq.hitthegym.common.ModConstants;

@EventBusSubscriber(bus = Bus.MOD)
public class Messages {
	
	/**
	 * All the information needed to register a new message
	 * @author SpinyQ
	 */
	public static class MessageType<MSG> {

		public MessageType(Class<MSG> clazz, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
				BiConsumer<MSG, Supplier<Context>> handler) {
			super();
			this.clazz = clazz;
			this.encoder = encoder;
			this.decoder = decoder;
			this.handler = handler;
		}
		
		public void register(SimpleChannel channel, int index) {
			channel.registerMessage(index, clazz, encoder, decoder, handler);
		}
		
		private Class<MSG> clazz;
		private BiConsumer<MSG, PacketBuffer> encoder;
		private Function<PacketBuffer, MSG> decoder;
		private BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler;
		
	}
	
	private static final String PROTOCOL_VERSION = "1";
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ModConstants.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);
	
	public static final ImmutableList<MessageType<?>> MESSAGES = ImmutableList.of(MessageLifterChange.TYPE);
	
	public static void register() {
		int id = 0;
		for (MessageType<?> messageType : MESSAGES) {
			messageType.register(CHANNEL, id++);
		}
	}
	
	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		register();
	}
	
}
