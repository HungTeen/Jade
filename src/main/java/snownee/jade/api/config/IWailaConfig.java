package snownee.jade.api.config;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import snownee.jade.JadeInternals;
import snownee.jade.api.SimpleStringRepresentable;
import snownee.jade.api.theme.Theme;

@NonExtendable
public interface IWailaConfig {

	static IWailaConfig get() {
		return JadeInternals.getWailaConfig();
	}

	General general();

	Overlay overlay();

	Formatting formatting();

	Accessibility accessibility();

	IPluginConfig plugin();

	void save();

	enum IconMode implements SimpleStringRepresentable {
		TOP, CENTERED, INLINE, HIDE
	}

	enum TTSMode implements SimpleStringRepresentable {
		TOGGLE, PRESS
	}

	enum DisplayMode implements SimpleStringRepresentable {
		HOLD_KEY, TOGGLE, LITE
	}

	enum FluidMode implements SimpleStringRepresentable {
		NONE(ClipContext.Fluid.NONE),
		ANY(ClipContext.Fluid.ANY),
		SOURCE_ONLY(ClipContext.Fluid.SOURCE_ONLY),
		FALLBACK(ClipContext.Fluid.NONE);

		public final ClipContext.Fluid ctx;

		FluidMode(ClipContext.Fluid ctx) {
			this.ctx = ctx;
		}
	}

	enum BossBarOverlapMode implements SimpleStringRepresentable {
		NO_OPERATION, HIDE_BOSS_BAR, HIDE_TOOLTIP, PUSH_DOWN
	}

	@NonExtendable
	interface General {

		void setDisplayTooltip(boolean displayTooltip);

		boolean getDisplayEntities();

		void setDisplayEntities(boolean displayEntities);

		boolean getDisplayBosses();

		void setDisplayBosses(boolean displayBosses);

		boolean getDisplayBlocks();

		void setDisplayBlocks(boolean displayBlocks);

		void setHideFromDebug(boolean hideFromDebug);

		void setHideFromTabList(boolean hideFromTabList);

		void setHideFromGUIs(boolean hideFromGUIs);

		void setItemModNameTooltip(boolean itemModNameTooltip);

		boolean shouldDisplayTooltip();

		DisplayMode getDisplayMode();

		void setDisplayMode(DisplayMode displayMode);

		boolean shouldHideFromDebug();

		boolean shouldHideFromTabList();

		boolean shouldHideFromGUIs();

		boolean shouldDisplayFluids();

		FluidMode getDisplayFluids();

		void setDisplayFluids(boolean displayFluids);

		void setDisplayFluids(FluidMode displayFluids);

		boolean showItemModNameTooltip();

		float getExtendedReach();

		void setExtendedReach(float extendedReach);

		BossBarOverlapMode getBossBarOverlapMode();

		void setBossBarOverlapMode(BossBarOverlapMode mode);

		boolean isDebug();

		void setDebug(boolean debug);

		boolean getBuiltinCamouflage();

		void setBuiltinCamouflage(boolean builtinCamouflage);
	}

	@NonExtendable
	interface Overlay {

		static int applyAlpha(int color, float alpha) {
			int prevAlphaChannel = (color >> 24) & 0xFF;
			if (prevAlphaChannel > 0) {
				alpha *= prevAlphaChannel / 256f;
			}
			int alphaChannel = Mth.clamp((int) (0xFF * alpha), 4, 255);
			return (color & 0xFFFFFF) | alphaChannel << 24;
		}

		float getOverlayPosX();

		void setOverlayPosX(float overlayPosX);

		float getOverlayPosY();

		void setOverlayPosY(float overlayPosY);

		float getOverlayScale();

		void setOverlayScale(float overlayScale);

		float getAnchorX();

		void setAnchorX(float overlayAnchorX);

		float getAnchorY();

		void setAnchorY(float overlayAnchorY);

		boolean getSquare();

		void setSquare(boolean overlaySquare);

		float getAutoScaleThreshold();

		float getAlpha();

		void setAlpha(float alpha);

		Theme getTheme();

		void applyTheme(ResourceLocation id);

		boolean shouldShowIcon();

		IconMode getIconMode();

		void setIconMode(IconMode iconMode);

		boolean getAnimation();

		void setAnimation(boolean animation);

		float getDisappearingDelay();

		void setDisappearingDelay(float delay);
	}

	@NonExtendable
	interface Formatting {

		Style getItemModNameStyle();

		void setItemModNameStyle(Style itemModNameStyle);

		Component registryName(String name);
	}

	@NonExtendable
	interface Accessibility {

		boolean shouldEnableTextToSpeech();

		void toggleTTS();

		TTSMode getTTSMode();

		void setTTSMode(TTSMode ttsMode);

		boolean getEnableAccessibilityPlugin();

		void setEnableAccessibilityPlugin(boolean showAccessibilityPlugins);

		boolean getFlipMainHand();

		void setFlipMainHand(boolean overlaySquare);

		float tryFlip(float f);
	}
}
