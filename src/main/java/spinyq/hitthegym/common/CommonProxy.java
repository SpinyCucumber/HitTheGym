package spinyq.hitthegym.common;

import spinyq.hitthegym.common.capability.ILifterCapability;
import spinyq.hitthegym.common.network.Messages;

public class CommonProxy {

	public void preInit() {
		ILifterCapability.register();
		Messages.register();
	}
	
}