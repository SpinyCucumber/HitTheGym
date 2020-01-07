package spinyq.hitthegym.client;

import spinyq.hitthegym.common.ProxyCommon;

public class ProxyClient extends ProxyCommon {

	@Override
	public void preInit() {
		super.preInit();
		Sounds.instance.preInit();
	}

}
