package spinyq.hitthegym.common;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import spinyq.hitthegym.common.item.ItemBarbell;
import spinyq.hitthegym.common.item.ItemDumbbell;

@EventBusSubscriber(bus = Bus.MOD)
public class ModItems {
	
	public static final ItemDumbbell DUMBBELL = new ItemDumbbell();
	public static final ItemBarbell BARBELL = new ItemBarbell();
	
	public static final ImmutableList<IItemProvider> ITEMS = ImmutableList.of(DUMBBELL, BARBELL);
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		// Register items
		ITEMS.stream().map(IItemProvider::asItem).forEach(event.getRegistry()::register);
	}
	
}
