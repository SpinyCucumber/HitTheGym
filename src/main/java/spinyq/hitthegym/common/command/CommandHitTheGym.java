package spinyq.hitthegym.common.command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import spinyq.hitthegym.common.capability.CapabilityUtils;
import spinyq.hitthegym.common.capability.CapabilityUtils.MissingCapabilityException;
import spinyq.hitthegym.common.capability.StrengthsCapability;
import spinyq.hitthegym.common.core.MuscleGroup;
import spinyq.hitthegym.common.core.Strengths;

@EventBusSubscriber
public class CommandHitTheGym {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(literal("htg")
			// The command sender needs higher permissions to use this command
			.requires(commandSource -> commandSource.hasPermissionLevel(2))
			.then(literal("strength")
				.then(literal("get")
					.then(argument("muscleGroup", new MuscleGroupArgument())
						.executes(ctx -> {
							try {
								MuscleGroup group = ctx.getArgument("muscleGroup", MuscleGroup.class);
								ServerPlayerEntity player = ctx.getSource().asPlayer();
								Strengths strengths = CapabilityUtils.getCapability(player, StrengthsCapability.CAPABILITY).getStrengths();
								double strength = strengths.getStrength(group);
								// Send info
								ctx.getSource().sendFeedback(new TranslationTextComponent("commands.hitthegym.strength.get", group, strength), true);
							} catch (MissingCapabilityException e) {
								throw new RuntimeException("Error occured while executing command.", e);
							}
							return SINGLE_SUCCESS;
						})
					)
					.executes(ctx -> {
						try {
							ServerPlayerEntity player = ctx.getSource().asPlayer();
							Strengths strengths = CapabilityUtils.getCapability(player, StrengthsCapability.CAPABILITY).getStrengths();
							// Iterate over all muscle groups and send a message for each
							for (MuscleGroup group : MuscleGroup.values()) {
								ctx.getSource().sendFeedback(new TranslationTextComponent("commands.hitthegym.strength.get", group, strengths.getStrength(group)), true);
							}
						} catch (MissingCapabilityException e) {
							throw new RuntimeException("Error occured while executing command.", e);
						}
						return SINGLE_SUCCESS;
					})
				)
			)
		);
	}
	
	@SubscribeEvent
	public static void onServerStart(FMLServerStartingEvent event) {
		register(event.getCommandDispatcher());
	}

}
