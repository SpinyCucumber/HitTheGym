package spinyq.hitthegym.common;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import spinyq.hitthegym.common.item.IItemBase;
import spinyq.hitthegym.common.item.ItemBarbell;
import spinyq.hitthegym.common.item.ItemDumbell;

@EventBusSubscriber
public class ModItems {
	
	public static final ItemDumbell DUMBELL = new ItemDumbell();
	public static final ItemBarbell BARBELL = new ItemBarbell();
	
	public static final ImmutableList<IItemBase> ITEMS = ImmutableList.of(DUMBELL, BARBELL);
	
	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<Item> event) {
		// Register items
		// Yay functional programming
		ITEMS.stream().map(IItemBase::asItem).forEach(event.getRegistry()::register);
		// Register models
		ITEMS.forEach(IItemBase::registerModels);
	}
	
}
