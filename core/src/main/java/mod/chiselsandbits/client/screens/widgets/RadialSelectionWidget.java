package mod.chiselsandbits.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.chiselsandbits.api.client.screen.widget.AbstractChiselsAndBitsWidget;
import mod.chiselsandbits.api.config.IClientConfiguration;
import mod.chiselsandbits.api.item.withmode.IRenderableMode;
import mod.chiselsandbits.api.item.withmode.group.IToolModeGroup;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RadialSelectionWidget extends AbstractChiselsAndBitsWidget
{
    private static final float DRAWS = 720;

    private final Supplier<IRenderableMode>       currentlySelectedModeSupplier;
    private final Consumer<IRenderableMode>       currentlyHoveredModeCallback;
    private final Consumer<IRenderableMode>       currentlyClickedModeCallback;
    private final List<? extends IRenderableMode> modes;
    private final float                           sectionArcAngle;
    private final float                           sectionStartAngle;
    private final float                           innerSelectionRadius;
    private final float                           outerSelectionRadius;
    private final boolean                         keepSelectionWhenBeyondOuterSelectionRadius;
    private final float                           innerRadius;
    private final float                           outerRadius;
    private final float                           iconSize;
    private final float                           iconScaleFactor;
    private final int  iconTextSpacer;
    private final Font fontRenderer;

    private final float centerX;
    private final float centerY;

    public RadialSelectionWidget(
      final Screen screen,
      final int width,
      final int height,
      final Component message,
      final Supplier<IRenderableMode> currentlySelectedModeSupplier,
      final Consumer<IRenderableMode> currentlyHoveredModeCallback,
      final Consumer<IRenderableMode> currentlyClickedModeCallback,
      final List<IRenderableMode> modes,
      final float sectionArcAngle,
      final float sectionStartAngle,
      final boolean hideInactiveIcons,
      final float innerSelectionRadius,
      final float outerSelectionRadius,
      final boolean keepSelectionWhenBeyondOuterSelectionRadius,
      final float innerRadius,
      final float outerRadius,
      final float iconSize,
      final float iconScaleFactor,
      final int iconTextSpacer,
      final Font fontRenderer)
    {
        this(
          (int) (screen.width / 2f - (width / 2f)),
          (int) (screen.height / 2f - (height / 2f)),
          width,
          height,
          message,
          currentlySelectedModeSupplier,
          currentlyHoveredModeCallback,
          currentlyClickedModeCallback,
          modes,
          sectionArcAngle,
          sectionStartAngle,
          hideInactiveIcons, innerSelectionRadius,
          outerSelectionRadius,
          keepSelectionWhenBeyondOuterSelectionRadius,
          innerRadius,
          outerRadius,
          iconSize,
          iconScaleFactor,
          iconTextSpacer,
          fontRenderer
        );
    }

    public RadialSelectionWidget(
      final int x,
      final int y,
      final int width,
      final int height,
      final Component message,
      final Supplier<IRenderableMode> currentlySelectedModeSupplier,
      final Consumer<IRenderableMode> currentlyHoveredModeCallback,
      final Consumer<IRenderableMode> currentlyClickedModeCallback,
      final List<? extends IRenderableMode> modes,
      final float sectionArcAngle,
      final float sectionStartAngle,
      final boolean hideInactiveIcons,
      final float innerSelectionRadius,
      final float outerSelectionRadius,
      final boolean keepSelectionWhenBeyondOuterSelectionRadius,
      final float innerRadius,
      final float outerRadius,
      final float iconSize,
      final float iconScaleFactor,
      final int iconTextSpacer,
      final Font fontRenderer
    )
    {
        super(x, y, width, height, message);
        this.currentlySelectedModeSupplier = currentlySelectedModeSupplier;
        this.currentlyHoveredModeCallback = currentlyHoveredModeCallback;
        this.currentlyClickedModeCallback = currentlyClickedModeCallback;
        this.keepSelectionWhenBeyondOuterSelectionRadius = keepSelectionWhenBeyondOuterSelectionRadius;
        this.modes = modes.stream().filter(mode -> !hideInactiveIcons || mode.isActive()).collect(Collectors.toList());
        this.sectionArcAngle = sectionArcAngle;
        this.sectionStartAngle = sectionStartAngle;
        this.innerSelectionRadius = innerSelectionRadius;
        this.outerSelectionRadius = outerSelectionRadius;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.iconSize = iconSize;
        this.iconScaleFactor = iconScaleFactor;
        this.iconTextSpacer = iconTextSpacer;
        this.fontRenderer = fontRenderer;

        this.centerX = x + (width / 2f);
        this.centerY = y + (height / 2f);
    }

    public <G extends IToolModeGroup> RadialSelectionWidget(
      final AbstractChiselsAndBitsWidget widget,
      final int width,
      final int height,
      final Component message,
      final Supplier<IRenderableMode> currentlySelectedModeSupplier,
      final Consumer<IRenderableMode> currentlyHoveredModeCallback,
      final Consumer<IRenderableMode> currentlyClickedModeCallback,
      final List<? extends IRenderableMode> modes,
      final float sectionArcAngle,
      final float sectionStartAngle,
      final boolean hideInactiveIcons,
      final float innerSelectionRadius,
      final float outerSelectionRadius,
      final boolean keepSelectionWhenBeyondOuterSelectionRadius,
      final float innerRadius,
      final float outerRadius,
      final float iconSize,
      final float iconScaleFactor,
      final int iconTextSpacer,
      final Font fontRenderer)
    {
        this(
          (int) (widget.getX() + (widget.getWidth() / 2f) - (width / 2f)),
          (int) (widget.getY() + (widget.getHeight() / 2f) - (height / 2f)),
          width,
          height,
          message,
          currentlySelectedModeSupplier,
          currentlyHoveredModeCallback,
          currentlyClickedModeCallback,
          modes,
          sectionArcAngle,
          sectionStartAngle,
          hideInactiveIcons,
          innerSelectionRadius,
          outerSelectionRadius,
          keepSelectionWhenBeyondOuterSelectionRadius,
          innerRadius,
          outerRadius,
          iconSize,
          iconScaleFactor,
          iconTextSpacer,
          fontRenderer
        );
    }

    @Override
    public void renderWidget(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks)
    {
        final IRenderableMode current = currentlySelectedModeSupplier.get();

        final int selectableItemCount = modes.size();
        if (selectableItemCount == 0)
        {
            return;
        }

        // center of screen
        float centerX = this.getX() + (this.width / 2f);
        float centerY = this.getY() + (this.height / 2f);

        graphics.pose().pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.pose().translate(centerX, centerY, 0);

        final float itemArcAngle = sectionArcAngle / selectableItemCount;

        final float mouseAngle = calculateMouseAngle(mouseX, mouseY, centerX, centerY);
        final float mouseRadius = calculateMouseRadius(mouseX, mouseY, centerX, centerY);

        final float inSectionMouseAngle = mouseAngle - sectionStartAngle;
        final boolean mouseIsInSectionArc = inSectionMouseAngle >= 0 && inSectionMouseAngle <= sectionArcAngle;
        boolean isMouseInSection = mouseIsInSectionArc && mouseRadius >= innerSelectionRadius && mouseRadius <= outerSelectionRadius;

        int hoveredItemIndex = !isMouseInSection ? -1 : (int) (inSectionMouseAngle / itemArcAngle);
        if (!isMouseInSection && current != null && modes.contains(current) && keepSelectionWhenBeyondOuterSelectionRadius)
        {
            if (mouseIsInSectionArc && mouseRadius >= innerSelectionRadius)
            {
                //We are at least in our arc bundle
                //Lets just assume we are in a sub menu of hours and keep the current.
                isMouseInSection = true;
                hoveredItemIndex = modes.indexOf(current);
            }
        }

        RenderSystem.enableBlend();
        final int renderableHoveredItemIndex = hoveredItemIndex;
        modes.forEach(mode -> {
            final int modeIndex = modes.indexOf(mode);
            final boolean isSelected = current != null && mode == current;
            final boolean isHovered = renderableHoveredItemIndex == modeIndex;

            final float itemTargetAngle = ((modeIndex + 0.5f) * itemArcAngle) + sectionStartAngle;

            if (mode.isActive())
            {
                drawSelectableSection(
                  graphics,
                  sectionArcAngle,
                  innerRadius,
                  outerRadius,
                  selectableItemCount,
                  itemTargetAngle,
                  isSelected,
                  isHovered
                );
            }
            else
            {
                drawDeactivatedSection(
                  graphics,
                  sectionArcAngle,
                  innerRadius,
                  outerRadius,
                  selectableItemCount,
                  itemTargetAngle
                );
            }
        });
        RenderSystem.disableBlend();

        if (isMouseInSection && hoveredItemIndex >= 0 && hoveredItemIndex < modes.size() && modes.get(hoveredItemIndex).isActive())
        {
            if (IClientConfiguration.getInstance().getEnableMouseIndicatorInRadialMenu().get())
            {
                float startOfMouseArcAngle = mouseAngle - (itemArcAngle / 2);
                float mouseArcAngle = itemArcAngle;

                if (sectionArcAngle != 360)
                {
                    if (startOfMouseArcAngle < sectionStartAngle)
                    {
                        mouseArcAngle -= (sectionStartAngle - startOfMouseArcAngle);
                        startOfMouseArcAngle = sectionStartAngle;
                    }

                    if ((startOfMouseArcAngle + mouseArcAngle) > (sectionStartAngle + sectionArcAngle))
                    {
                        mouseArcAngle = (sectionStartAngle + sectionArcAngle) - startOfMouseArcAngle;
                    }
                }

                RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 0.3F);
                drawTorus(graphics, startOfMouseArcAngle - 90, mouseArcAngle, innerRadius, outerRadius);
            }

            if (hoveredItemIndex >= 0 && modes.get(hoveredItemIndex) != current)
            {
                currentlyHoveredModeCallback.accept(modes.get(hoveredItemIndex));
            }
        }
        else if (current != null)
        {
            currentlyHoveredModeCallback.accept(null);
        }

        modes.forEach(mode -> {
            if (mode.isActive())
            {
                final int modeIndex = modes.indexOf(mode);
                final float itemTargetAngle = ((modeIndex + 0.5f) * itemArcAngle) + sectionStartAngle;
                renderModeIcon(graphics, innerRadius, outerRadius, itemTargetAngle, iconScaleFactor, iconTextSpacer, mode, fontRenderer);
            }
        });

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.pose().popPose();
    }

    private static float calculateMouseAngle(final float mouseX, final float mouseY, final float centerX, final float centerY)
    {
        final float xDiff = mouseX - centerX;
        final float yDiff = mouseY - centerY;

        float mouseAngle = (float) Math.toDegrees(Math.atan2(yDiff, xDiff)) - 270f;
        while (mouseAngle < 0)
        {
            mouseAngle += 360;
        }
        return mouseAngle;
    }

    private static float calculateMouseRadius(final float mouseX, final float mouseY, final float centerX, final float centerY)
    {
        final float xDiff = mouseX - centerX;
        final float yDiff = mouseY - centerY;

        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    @SuppressWarnings("deprecation")
    private static void drawSelectableSection(
      @NotNull final GuiGraphics graphics,
      final float sectionArcAngle,
      final float innerRadius,
      final float outerRadius,
      final int itemCountInSection,
      final float itemTargetAngle,
      final boolean isSelected,
      final boolean isHovered
    )
    {
        final float itemArcAngle = sectionArcAngle / itemCountInSection;
        final float itemRenderAngle = itemTargetAngle - 90F;

        final float sectionStartAngle = itemRenderAngle - (itemArcAngle / 2);

        RenderSystem.setShaderColor(0.3f, 0.3f, 0.3f, 0.3f);
        drawTorus(
          graphics,
          sectionStartAngle,
          itemArcAngle,
          innerRadius,
          outerRadius
        );

        if (isSelected)
        {
            RenderSystem.setShaderColor(0.4F, 0.4F, 0.4F, 0.7F);
            drawTorus(
              graphics,
              sectionStartAngle,
              itemArcAngle,
              innerRadius,
              outerRadius
            );
        }

        if (isHovered)
        {
            RenderSystem.setShaderColor(0.7F, 0.7F, 0.7F, 0.7F);
            drawTorus(
              graphics,
              sectionStartAngle,
              itemArcAngle,
              innerRadius,
              outerRadius
            );
        }
    }

    @SuppressWarnings("deprecation")
    private static void drawDeactivatedSection(
      @NotNull final GuiGraphics graphics,
      final float sectionArcAngle,
      final float innerRadius,
      final float outerRadius,
      final int itemCountInSection,
      final float itemTargetAngle
    )
    {
        final float itemArcAngle = sectionArcAngle / itemCountInSection;
        final float itemRenderAngle = itemTargetAngle - 90F;

        final float sectionStartAngle = itemRenderAngle - (itemArcAngle / 2);

        RenderSystem.setShaderColor(0.1f, 0.1f, 0.1f, 0.1f);
        drawTorus(
          graphics,
          sectionStartAngle,
          itemArcAngle,
          innerRadius,
          outerRadius
        );
    }

    private static void drawTorus(GuiGraphics graphics, float startAngle, float sizeAngle, float inner, float outer)
    {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder vertexBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
        Matrix4f matrix4f = graphics.pose().last().pose();
        float draws = DRAWS * (sizeAngle / 360F);
        for (int i = 0; i <= draws; i++)
        {
            float angle = (float) Math.toRadians(startAngle + (i / DRAWS) * 360);
            vertexBuffer.addVertex(matrix4f, (float) (outer * Math.cos(angle)), (float) (outer * Math.sin(angle)), 0);
            vertexBuffer.addVertex(matrix4f, (float) (inner * Math.cos(angle)), (float) (inner * Math.sin(angle)), 0);
        }
        final MeshData buffer = vertexBuffer.buildOrThrow();
        BufferUploader.drawWithShader(buffer);
    }

    private void renderModeIcon(
      final @NotNull GuiGraphics graphics,
      final float innerRadius,
      final float outerRadius,
      final float itemTargetAngle,
      final float iconScaleFactor,
      final int iconTextSpacer,
      final @NotNull IRenderableMode mode,
      final Font fontRenderer)
    {
        float workingAngle = itemTargetAngle - 90;
        while (workingAngle < 0)
        {
            workingAngle += 360;
        }

        final float itemCenterX = (float) Math.cos(Math.toRadians(workingAngle)) * (innerRadius + outerRadius) / 2F;
        final float itemCenterY = (float) Math.sin(Math.toRadians(workingAngle)) * (innerRadius + outerRadius) / 2F;

        final Component name = mode.getMultiLineDisplayName();
        final List<FormattedCharSequence> lines = fontRenderer.split(name, 75);

        final int itemHeight = mode.shouldRenderDisplayNameInMenu() ?
                                 (int) ((iconSize * iconScaleFactor) + iconTextSpacer + (fontRenderer.lineHeight * lines.size()))
                                 : (int) (iconSize * iconScaleFactor);

        final float iconStartX = itemCenterX - ((iconSize * iconScaleFactor) / 2f);
        final float iconStartY = itemCenterY - (itemHeight / 2f);

        graphics.pose().pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(
          (float) mode.getColorVector().x(),
          (float) mode.getColorVector().y(),
          (float) mode.getColorVector().z(),
          (float) mode.getAlphaChannel()
        );
        RenderSystem.setShaderTexture(0, mode.getIcon());
        graphics.blit(mode.getIcon(), (int) iconStartX, (int) iconStartY, (int) (iconSize * iconScaleFactor), (int) (iconSize * iconScaleFactor), 0, 0, 18, 18, 18, 18);
        graphics.pose().pushPose();

        if (mode.shouldRenderDisplayNameInMenu())
        {
            graphics.pose().translate(itemCenterX, itemCenterY, 0);
            graphics.pose().scale(0.6F * iconScaleFactor, 0.6F * iconScaleFactor, 0.6F * iconScaleFactor);

            int offset = 0;
            for (final FormattedCharSequence line : lines)
            {
                graphics.drawString(fontRenderer, line, (int) (fontRenderer.width(line) / -2f), iconTextSpacer + offset, 0xCCFFFFFF);
                offset += fontRenderer.lineHeight;
            }
        }

        graphics.pose().popPose();
        graphics.pose().popPose();
    }

    @Override
    protected boolean isValidClickButton(final int usedButton)
    {
        return usedButton == 0;
    }

    @Override
    protected boolean clicked(final double mouseX, final double mouseY)
    {
        if (!this.active || !this.visible)
        {
            return false;
        }

        final int selectableItemCount = modes.size();
        if (selectableItemCount == 0)
        {
            return false;
        }

        final float itemArcAngle = sectionArcAngle / selectableItemCount;

        final float mouseAngle = calculateMouseAngle((float) mouseX, (float) mouseY, centerX, centerY);
        final float mouseRadius = calculateMouseRadius((float) mouseX, (float) mouseY, centerX, centerY);

        final float inSectionMouseAngle = mouseAngle - sectionStartAngle;
        final boolean mouseIsInSectionArc = inSectionMouseAngle >= 0 && inSectionMouseAngle <= sectionArcAngle;
        final boolean isMouseInSection = mouseIsInSectionArc && mouseRadius >= innerSelectionRadius && mouseRadius <= outerSelectionRadius;

        final int hoveredItemIndex = !isMouseInSection ? -1 : (int) (inSectionMouseAngle / itemArcAngle);
        if (hoveredItemIndex == -1)
        {
            return false;
        }

        this.currentlyClickedModeCallback.accept(modes.get(hoveredItemIndex));
        return true;
    }
}
