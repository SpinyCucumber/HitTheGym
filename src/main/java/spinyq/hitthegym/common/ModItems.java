package spinyq.hitthegym.common;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import spinyq.hitthegym.common.core.Exercise;
import spinyq.hitthegym.common.core.LifterContext;
import spinyq.hitthegym.common.item.ItemWeight;

@EventBusSubscriber(bus = Bus.MOD)
public class ModItems {
	
	public static final ItemWeight DUMBBELL = new ItemWeight(
			new ResourceLocation(ModConstants.MODID, "dumbbell"),
			new Item.Properties().maxStackSize(1),
			new LifterContext(ImmutableList.of(Exercise.CURL, Exercise.LATERAL), 1.0));
	
	public static final ItemWeight BARBELL = new ItemWeight(
			new ResourceLocation(ModConstants.MODID, "barbell"),
			new Item.Properties().maxStackSize(1),
			new LifterContext(ImmutableList.of(Exercise.SQUAT), 1.0));
	
	public static final ImmutableList<IItemProvider> ITEMS = ImmutableList.of(DUMBBELL, BARBELL);
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		// Register items
		ITEMS.stream().map(IItemProvider::asItem).forEach(event.getRegistry()::register);
	}
	
}
