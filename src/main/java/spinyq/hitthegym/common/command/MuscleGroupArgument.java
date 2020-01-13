package spinyq.hitthegym.common.command;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.util.text.TranslationTextComponent;
import spinyq.hitthegym.common.core.MuscleGroup;

public class MuscleGroupArgument implements ArgumentType<MuscleGroup> {

	public static final DynamicCommandExceptionType INVALID_MUSCLEGROUP_EXCEPTION = new DynamicCommandExceptionType((musclegroup) -> {
        return new TranslationTextComponent("argument.hitthegym.musclegroup.invalid", new Object[]{musclegroup});
    });
	
	@Override
	public MuscleGroup parse(StringReader reader) throws CommandSyntaxException {
		String id = reader.readString();
		Optional<MuscleGroup> group = Enums.getIfPresent(MuscleGroup.class, id);
		if (group.isPresent()) return group.get();
		else throw INVALID_MUSCLEGROUP_EXCEPTION.create(id);
	}

	// TODO Suggestions. Waiting until 1.15
	
}
