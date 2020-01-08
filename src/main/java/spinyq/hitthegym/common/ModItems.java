package spinyq.hitthegym.common;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import spinyq.hitthegym.common.item.ItemBarbell;
import spinyq.hitthegym.common.item.ItemDumbell;

@EventBusSubscriber
public class ModItems {

	public static final ModItems instance = new ModItems();
	
	public static ItemDumbell itemDumbell;
	public static ItemBarbell itemBarbell;
	
	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<Item> event) {
		// Initialize prototype items
		itemDumbell = new ItemDumbell();
		itemBarbell = new ItemBarbell();
		// Register items
		event.getRegistry().registerAll(itemDumbell, itemBarbell);
		// Register models
		// TODO abstract this
		itemDumbell.registerModel();
		itemBarbell.registerModel();
	}
	
}
