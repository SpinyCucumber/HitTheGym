package spinyq.hitthegym.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import spinyq.hitthegym.common.ModConstants;

public class Messages {

	public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(ModConstants.MODID);
	
	public static void register()
	{
		int id = 0;
		instance.registerMessage(MessageLifterChange.Handler.class, MessageLifterChange.class, id++, Side.SERVER);
		instance.registerMessage(MessageLifterChange.Handler.class, MessageLifterChange.class, id++, Side.CLIENT);
	}
	
}
