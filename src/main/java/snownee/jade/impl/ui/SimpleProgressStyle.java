package snownee.jade.impl.ui;

import org.joml.Vector3f;

import com.google.common.base.Preconditions;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.config.IWailaConfig.Overlay;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Color;
import snownee.jade.api.ui.ProgressStyle;
import snownee.jade.api.ui.ScreenDirection;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.OverlayRenderer;

public class SimpleProgressStyle extends ProgressStyle {

	public boolean autoTextColor = true; // TODO
	public int color;
	public int color2;
	public int textColor;
	public boolean vertical;

	public SimpleProgressStyle() {
		color(-1);
	}

	private static Vector3f RGBtoHSV(int rgb) {
		int r = (rgb >> 16) & 255;
		int g = (rgb >> 8) & 255;
		int b = rgb & 255;
		int max = Math.max(r, Math.max(g, b));
		int min = Math.min(r, Math.min(g, b));
		float v = max;
		float delta = max - min;
		float h, s;
		if (max != 0) {
			s = delta / max; // s
		} else {
			// r = g = b = 0        // s = 0, v is undefined
			s = 0;
			h = -1;
			return new Vector3f(h, s, 0 /*Float.NaN*/);
		}
		if (r == max) {
			h = (g - b) / delta; // between yellow & magenta
		} else if (g == max) {
			h = 2 + (b - r) / delta; // between cyan & yellow
		} else {
			h = 4 + (r - g) / delta; // between magenta & cyan
		}
		h /= 6; // degrees
		if (h < 0) {
			h += 1;
		}
		return new Vector3f(h, s, v / 255);
	}

	@Override
	public ProgressStyle color(int color, int color2) {
		this.color = color;
		this.color2 = color2;
		return this;
	}

	@Override
	public ProgressStyle direction(ScreenDirection direction) {
		Preconditions.checkArgument(
				direction == ScreenDirection.UP || direction == ScreenDirection.RIGHT,
				"Only UP and RIGHT are supported");
		super.direction(direction);
		vertical = direction.isVertical();
		return this;
	}

	@Override
	public void render(GuiGraphics guiGraphics, float x, float y, float width, float height, float progress, Component text) {
		progress *= choose(true, width, height);
		float progressY = y;
		if (vertical) {
			progressY += height - progress;
		}
		if (progress > 0) {
			if (overlay != null) {
				Vec2 size = new Vec2(choose(true, progress, width), choose(false, progress, height));
				overlay.size(size);
				overlay.render(guiGraphics, x, progressY, size.x, size.y);
			} else {
				Color color3 = Color.rgb(color);
				int lighter = Color.hsl(color3.getHue(), color3.getSaturation(), color3.getLightness() * 0.7f, color3.getOpacity()).toInt();

				float half = choose(true, height, width) / 2;
				DisplayHelper.INSTANCE.drawGradientRect(
						guiGraphics,
						x,
						progressY,
						choose(true, progress, half),
						choose(false, progress, half),
						lighter,
						color,
						vertical);
				DisplayHelper.INSTANCE.drawGradientRect(
						guiGraphics,
						x + choose(false, half, 0),
						progressY + choose(true, half, 0),
						choose(true, progress, half),
						choose(false, progress, half),
						color,
						lighter,
						vertical);
				if (color != color2) {
					if (vertical) {
						for (float yy = y + height; yy > progressY; yy -= 2) {
							float fy = Math.max(progressY, yy + 1);
							DisplayHelper.fill(guiGraphics, x, yy, x + width, fy, color2);
						}
					} else {
						for (float xx = x + 1; xx < x + progress; xx += 2) {
							float fx = Math.min(x + width, xx + 1);
							DisplayHelper.fill(guiGraphics, xx, y, fx, y + height, color2);
						}
					}
				}
			}
		}
		if (text != null) {
			Font font = DisplayHelper.font();
			if (autoTextColor) {
				autoTextColor = false;
				if (overlay == null && RGBtoHSV(color2).z() > 0.75f) {
					textColor = 0xFF000000;
				} else {
					textColor = IThemeHelper.get().getNormalColor();
				}
			} else if (textColor == -1) {
				textColor = IThemeHelper.get().getNormalColor();
			}
			y += height - font.lineHeight;
			if (vertical && font.lineHeight < progress) {
				y -= progress;
				y += font.lineHeight + 2;
			}
			int color = Overlay.applyAlpha(textColor, OverlayRenderer.alpha);
			DisplayHelper.font().drawInBatch(
					text,
					(int) x + 1,
					(int) y - 1,
					color,
					true,
					guiGraphics.pose().last().pose(),
					guiGraphics.bufferSource,
					Font.DisplayMode.NORMAL,
					ARGB.as8BitChannel(IWailaConfig.get().accessibility().getTextBackgroundOpacity()) << 24,
					0xF000F0);
		}
	}

	private float choose(boolean expand, float x, float y) {
		return vertical ^ expand ? x : y;
	}

	@Override
	public ProgressStyle textColor(int color) {
		textColor = color;
		autoTextColor = false;
		return this;
	}

}
