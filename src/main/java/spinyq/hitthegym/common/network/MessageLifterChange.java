package spinyq.hitthegym.common.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import spinyq.hitthegym.common.capability.ILifterCapability;
import spinyq.hitthegym.common.core.Exercise;
import spinyq.hitthegym.common.core.Lifter;

public class MessageLifterChange implements IMessage {

	public static class Handler implements IMessageHandler<MessageLifterChange, IMessage> {

		@Override
		public IMessage onMessage(MessageLifterChange message, MessageContext ctx) {
			// Find world depending on side
			World world = ctx.side == Side.SERVER ? ctx.getServerHandler().player.world : Minecraft.getMinecraft().world;
			EntityPlayer player = world.getPlayerEntityByUUID(message.player);
			// Give the new state a reference to the player
			message.state.setPlayer(player);
			// Get state and update with new one
			player.getCapability(ILifterCapability.CAPABILITY, null).setLifter(message.state);
			// Don't reply
			return null;
		}
		
	}
	
	public Lifter state;
	public UUID player;
	
	public MessageLifterChange() {
		// Default constructor necessary
	}

	public MessageLifterChange(Lifter state) {
		super();
		this.state = state;
		this.player = state.getPlayer().getUniqueID();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Read UUID
		player = new UUID(buf.readLong(), buf.readLong());
		// Read state type
		Lifter.Enum type = Lifter.Enum.values()[buf.readInt()];
		// Only care about active states
		if (type == Lifter.Enum.ACTIVE) {
			// Read exercise
			Lifter.Active active = new Lifter.Active(ByteBufUtils.readRegistryEntry(buf, GameRegistry.findRegistry(Exercise.class)));
			// Read lifting
			active.lifting = buf.readBoolean();
			state = active;
		} else if (type == Lifter.Enum.IDLE) {
			state = new Lifter();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Write UUID
		buf.writeLong(player.getMostSignificantBits());
		buf.writeLong(player.getLeastSignificantBits());
		// Write state type
		buf.writeInt(state.getEnum().ordinal());
		// If state is active, write some more stuff
		if (state instanceof Lifter.Active) {
			Lifter.Active active = (Lifter.Active) state;
			// Write exercise
			ByteBufUtils.writeRegistryEntry(buf, active.exercise);
			// Write lifting
			buf.writeBoolean(active.lifting);
		}
	}

}
