package spinyq.hitthegym.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import spinyq.hitthegym.common.HitTheGymMod;

public class Messages {

	public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(HitTheGymMod.MODID);
	
	public static void register()
	{
		int id = 0;
		instance.registerMessage(MessageLifterState.Handler.class, MessageLifterState.class, id++, Side.SERVER);
		instance.registerMessage(MessageLifterState.Handler.class, MessageLifterState.class, id++, Side.CLIENT);
	}
	
}
