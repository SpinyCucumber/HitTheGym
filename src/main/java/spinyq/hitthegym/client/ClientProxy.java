package spinyq.hitthegym.client;

import spinyq.hitthegym.common.CommonProxy;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
		ModSounds.preInit();
	}

}
