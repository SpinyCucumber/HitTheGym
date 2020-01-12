package spinyq.hitthegym.common.item;

import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IItemBase extends IItemProvider {
	
	@OnlyIn(Dist.CLIENT)
	void registerModels();
	
}