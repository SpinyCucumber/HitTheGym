package spinyq.hitthegym.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = HitTheGymMod.MODID, useMetadata = true)
public class HitTheGymMod {

	public static final String MODID = "hitthegym";
	
	public static Logger log = LogManager.getLogger(MODID);
	
	@SidedProxy(clientSide = "spinyq.hitthegym.client.ProxyClient", serverSide = "spinyq.hitthegym.common.ProxyCommon")
	public static ProxyCommon proxy = null;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log.info("Preinitializing...");
		proxy.preInit();
	}
	
}
