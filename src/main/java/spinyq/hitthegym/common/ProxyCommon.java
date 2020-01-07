package spinyq.hitthegym.common;

import spinyq.hitthegym.network.Messages;

public class ProxyCommon {

	public void preInit() {
		ILifter.register();
		Messages.register();
	}
	
}