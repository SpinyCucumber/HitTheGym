package spinyq.hitthegym.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModConstants.MODID, useMetadata = true)
public class HitTheGym {

	public static final Logger log = LogManager.getLogger(ModConstants.MODID);
	
	@SidedProxy(clientSide = "spinyq.hitthegym.client.ClientProxy", serverSide = "spinyq.hitthegym.common.CommonProxy")
	public static CommonProxy proxy = null;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log.info("Preinitializing...");
		proxy.preInit();
	}
	
}
