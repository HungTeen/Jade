package snownee.jade.addon.vanilla;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.CompoundElement;

public enum WaxedProvider implements IBlockComponentProvider {

	INSTANCE;

	@Override
	public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
		if (accessor.getPickedResult().isEmpty()) {
			return currentIcon;
		}
		IElementHelper helper = IElementHelper.get();
		IElement largeIcon = helper.item(accessor.getPickedResult());
		if (accessor.getBlockEntity() instanceof SignBlockEntity sign) {
			if (sign.isWaxed()) {
				return new CompoundElement(largeIcon, helper.item(Items.HONEYCOMB.getDefaultInstance(), 0.5f));
			} else {
				return largeIcon;
			}
		}
		return currentIcon;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {

	}

	@Override
	public ResourceLocation getUid() {
		return Identifiers.MC_WAXED;
	}
}
