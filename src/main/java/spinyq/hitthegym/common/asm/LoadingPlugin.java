package spinyq.hitthegym.common.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Hit The Gym Plugin")
@IFMLLoadingPlugin.SortingIndex(1001) // After runtime deobfuscation
public class LoadingPlugin implements IFMLLoadingPlugin {

	public static boolean runtimeDeobfEnabled = false;

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{ "spinyq.hitthegym.common.asm.ClassTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}