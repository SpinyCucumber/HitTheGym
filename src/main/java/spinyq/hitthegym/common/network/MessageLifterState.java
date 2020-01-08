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
import spinyq.hitthegym.common.capability.ILifter;
import spinyq.hitthegym.common.core.Exercise;
import spinyq.hitthegym.common.core.LifterState;

public class MessageLifterState implements IMessage {

	public static class Handler implements IMessageHandler<MessageLifterState, IMessage> {

		@Override
		public IMessage onMessage(MessageLifterState message, MessageContext ctx) {
			// Find world depending on side
			World world = ctx.side == Side.SERVER ? ctx.getServerHandler().player.world : Minecraft.getMinecraft().world;
			EntityPlayer player = world.getPlayerEntityByUUID(message.player);
			// Give the new state a reference to the player
			message.state.setPlayer(player);
			// Get state and update with new one
			player.getCapability(ILifter.CAPABILITY, null).setState(message.state);
			// Don't reply
			return null;
		}
		
	}
	
	public LifterState state;
	public UUID player;
	
	public MessageLifterState() {
		// Default constructor necessary
	}

	public MessageLifterState(LifterState state) {
		super();
		this.state = state;
		this.player = state.getPlayer().getUniqueID();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Read UUID
		player = new UUID(buf.readLong(), buf.readLong());
		// Read state type
		LifterState.Enum type = LifterState.Enum.values()[buf.readInt()];
		// Only care about active states
		if (type == LifterState.Enum.ACTIVE) {
			// Read exercise
			LifterState.Active active = new LifterState.Active(ByteBufUtils.readRegistryEntry(buf, GameRegistry.findRegistry(Exercise.class)));
			// Read lifting
			active.lifting = buf.readBoolean();
			state = active;
		} else if (type == LifterState.Enum.IDLE) {
			state = new LifterState();
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
		if (state instanceof LifterState.Active) {
			LifterState.Active active = (LifterState.Active) state;
			// Write exercise
			ByteBufUtils.writeRegistryEntry(buf, active.exercise);
			// Write lifting
			buf.writeBoolean(active.lifting);
		}
	}

}
