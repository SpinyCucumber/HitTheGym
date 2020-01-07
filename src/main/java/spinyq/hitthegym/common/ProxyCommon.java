package spinyq.hitthegym.common;

import spinyq.hitthegym.common.network.Messages;

public class ProxyCommon {

	public void preInit() {
		ILifter.register();
		Messages.register();
	}
	
}