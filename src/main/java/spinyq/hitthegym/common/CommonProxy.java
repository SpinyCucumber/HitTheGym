package spinyq.hitthegym.common;

import spinyq.hitthegym.common.capability.ILifter;
import spinyq.hitthegym.common.network.Messages;

public class CommonProxy {

	public void preInit() {
		ILifter.register();
		Messages.register();
	}
	
}