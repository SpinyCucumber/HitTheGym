package spinyq.hitthegym.common.network;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.extensions.IForgePacketBuffer;
import spinyq.hitthegym.common.capability.CapabilityUtils;
import spinyq.hitthegym.common.capability.CapabilityUtils.MissingCapabilityException;
import spinyq.hitthegym.common.capability.LifterCapability;
import spinyq.hitthegym.common.core.Exercise;
import spinyq.hitthegym.common.core.Lifter;
import spinyq.hitthegym.common.network.Messages.MessageType;

public class MessageLifterChange {

	/**
	 * Provides methods for encoding/decoding message as well as message handlers
	 */
	public static final MessageType<MessageLifterChange> TYPE = new MessageType<MessageLifterChange>(
			MessageLifterChange.class,
			(message, buffer) -> {
				// Write UUID
				buffer.writeLong(message.playerID.getMostSignificantBits());
				buffer.writeLong(message.playerID.getLeastSignificantBits());
				// Write type of lifter
				buffer.writeEnumValue(message.lifter.getType());
				// If state is active, write some more stuff
				if (message.lifter instanceof Lifter.Active) {
					Lifter.Active active = (Lifter.Active) message.lifter;
					// Write exercise
					((IForgePacketBuffer) buffer).writeRegistryId(active.exercise);
					// Write whether we are actively lifting
					buffer.writeBoolean(active.lifting);
				}
			},
			(buffer) -> {
				MessageLifterChange message = new MessageLifterChange();
				// Read UUID
				message.playerID = new UUID(buffer.readLong(), buffer.readLong());
				// Read state type
				Lifter.Type type = buffer.readEnumValue(Lifter.Type.class);
				// Only care about active states
				if (type == Lifter.Type.ACTIVE) {
					// Read exercise
					Exercise exercise = ((IForgePacketBuffer) buffer).readRegistryIdSafe(Exercise.class);
					// Construct the new lifter state
					Lifter.Active active = new Lifter.Active(exercise);
					// Read lifting
					active.lifting = buffer.readBoolean();
					message.lifter = active;
				} else if (type == Lifter.Type.IDLE) {
					message.lifter = new Lifter();
				}
				return message;
			},
			(message, contextSupplier) -> {
				// TODO Client syncing
				// Process on main thread (not network thread)
				contextSupplier.get().enqueueWork(() -> {
					// Get player
					PlayerEntity player = contextSupplier.get().getSender();
					// Pass a reference of the player to the new lifter state
					message.lifter.setPlayer(player);
					// Make the player hold the new lifter state.
					try {
						CapabilityUtils.getCapability(player, LifterCapability.CAPABILITY).setLifter(message.lifter);
					} catch (MissingCapabilityException e) {
						throw new RuntimeException("Error while handling lifter change message.", e);
					}
				});
				// Mark the message as handled
				contextSupplier.get().setPacketHandled(true);
			});
	
	public Lifter lifter;
	public UUID playerID;
	
	public MessageLifterChange() {
		// Default constructor necessary
	}

	public MessageLifterChange(Lifter lifter) {
		super();
		this.lifter = lifter;
		this.playerID = lifter.getPlayer().getUniqueID();
	}

}
