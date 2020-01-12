package spinyq.hitthegym.common.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityUtils {

	public static class MissingCapabilityException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1464997522756250701L;
		
		public MissingCapabilityException(Capability<?> missing) {
			super(String.format("Capability {} is missing.", missing.getName()));
		}
		
	}
	
	/**
	 * Might rename to make less confusing.
	 * @param <T>
	 * @param provider
	 * @param capability
	 * @return
	 * @throws MissingCapabilityException 
	 */
	public static <T> T getCapability(ICapabilityProvider provider, Capability<T> capability) throws MissingCapabilityException {
		return provider.getCapability(capability).orElseThrow(() -> {
			return new MissingCapabilityException(capability);
		});
	}
	
}
