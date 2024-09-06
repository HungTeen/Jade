package mcp.mobius.waila.gui;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionValue;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

public abstract class OptionsScreen extends Screen {

	private final Screen parent;
	private final Runnable saver;
	private final Runnable canceller;
	private OptionsListWidget options;
	private final Set<GuiEventListener> entryWidgets = Sets.newIdentityHashSet();

	public OptionsScreen(Screen parent, Component title, Runnable saver, Runnable canceller) {
		super(title);

		this.parent = parent;
		this.saver = saver;
		this.canceller = canceller;
	}

	public OptionsScreen(Screen parent, String title, Runnable saver, Runnable canceller) {
		this(parent, OptionsListWidget.Entry.makeTitle(title), saver, canceller);
	}

	public OptionsScreen(Screen parent, String title) {
		this(parent, title, null, null);
	}

	@Override
	protected void init() {
		entryWidgets.clear();
		options = getOptions();
		addRenderableWidget(options);

		if (saver != null && canceller != null) {
			addRenderableWidget(new Button(width / 2 - 100, height - 25, 100, 20, new TranslatableComponent("gui.done"), w -> {
				options.save();
				saver.run();
				minecraft.setScreen(parent);
			}));
			addRenderableWidget(new Button(width / 2 + 5, height - 25, 100, 20, new TranslatableComponent("gui.cancel"), w -> {
				canceller.run();
				minecraft.setScreen(parent);
			}));
		} else {
			addRenderableWidget(new Button(width / 2 - 50, height - 25, 100, 20, new TranslatableComponent("gui.done"), w -> {
				options.save();
				minecraft.setScreen(parent);
			}));
		}
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		if (mouseY < 32 || mouseY > height - 32)
			return;

		OptionsListWidget.Entry entry = options.getSelected();
		if (entry instanceof OptionValue) {
			OptionValue<?> value = (OptionValue<?>) entry;

			if (I18n.exists(value.getDescription())) {
				int valueX = value.getX() + 10;
				String title = value.getTitle().getString();
				if (mouseX < valueX || mouseX > valueX + font.width(title))
					return;

				List<FormattedCharSequence> tooltip = Lists.newArrayList(value.getTitle().getVisualOrderText());
				List<FormattedCharSequence> tooltip2 = font.split(new TranslatableComponent(value.getDescription()), 200);
				tooltip.addAll(tooltip2);
				matrixStack.pushPose();
				matrixStack.translate(0, 0, 100);
				renderTooltip(matrixStack, tooltip, mouseX, mouseY);
				RenderSystem.enableDepthTest();
				matrixStack.popPose();
			}
		}
	}

	@Override
	public void renderBackground(PoseStack matrixStack) {
		this.renderBackground(matrixStack, 0);
	}

	@Override
	public void renderBackground(PoseStack matrixStack, int vOffset) {
		if (minecraft.level != null) {
			this.fillGradient(matrixStack, 0, 0, width, height, -1072689136, -804253680);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, matrixStack));
		} else {
			renderDirtBackground(vOffset);
		}
	}

	public abstract OptionsListWidget getOptions();

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		return options.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		if (canceller != null)
			canceller.run();
		super.onClose();
	}

	public <T extends GuiEventListener & NarratableEntry> T addEntryWidget(T widget) {
		entryWidgets.add(widget);
		return super.addWidget(widget);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int p_94697_) {
		boolean onList = options.isMouseOver(mouseX, mouseY);
		for (GuiEventListener guieventlistener : children()) {
			if (!onList && entryWidgets.contains(guieventlistener)) {
				continue;
			}
			if (guieventlistener.mouseClicked(mouseX, mouseY, p_94697_)) {
				setFocused(guieventlistener);
				if (p_94697_ == 0) {
					setDragging(true);
				}

				return true;
			}
		}
		return false;
	}

	@Override
	public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
		boolean onList = options.isMouseOver(mouseX, mouseY);
		for (GuiEventListener guieventlistener : children()) {
			if (!onList && entryWidgets.contains(guieventlistener)) {
				continue;
			}
			if (guieventlistener.isMouseOver(mouseX, mouseY)) {
				return Optional.of(guieventlistener);
			}
		}

		return Optional.empty();
	}
}
