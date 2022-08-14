package daniel.text_block.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Array;
import java.util.Arrays;

public class VectorSliderWidget extends ClickableWidget {

    private final int dimensions;
    private final float[] values;

    private int selectedIndex;
    private boolean isDragging = false;

    private static final String[] COMPONENT_NAMES = new String[] {"X: ", "Y: ", "Z: ", "W: "};
    private static final int[] COMPONENT_COLORS = new int[] {
            0xff0000,
            0x00ff00,
            0x0000ff,
            0xffff00
    };

    public VectorSliderWidget(int x, int y, int width, int height, Text message, int dimensions) {
        super(x, y, width, height, message);
        this.dimensions = dimensions;
        this.values = new float[dimensions];
    }

    @Override
    protected int getYImage(boolean hovered) {
        return 0;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.isHovered() && !isDragging) {
            for (int i = 0; i < this.dimensions; i++) {
                int width = this.width / (this.dimensions * 2) - 2;
                int x = this.x + width * i * 2 + i * 5;

                if (mouseX > x && mouseX < x + width * 2) {
                    selectedIndex = i;
                }
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //System.out.println(selectedIndex);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        DrawableHelper.drawTextWithShadow(matrices, textRenderer, this.getMessage(), this.x, this.y - 9, 0xffffff);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int width = this.width / (this.dimensions * 2) - 2;
        int y = this.y;
        for (int i = 0; i < this.dimensions; i++) {
            int x = this.x + width * i * 2 + i * 5;
            int hoverOffset = (selectedIndex == i && (isHovered() || isDragging)) ? 40 : 0;



            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
            this.drawTexture(matrices, x, y, 0, 46 + hoverOffset, width, this.height);
            this.drawTexture(matrices, x + width, y, this.width - width, 46 + hoverOffset, width, this.height);
            //textRenderer.draw(matrices, compNames[i], x, this.y + (this.height - 8) / 2, 0xffffff);
            DrawableHelper.drawCenteredText(matrices, textRenderer, COMPONENT_NAMES[i] + values[i], x + width, y + (this.height - 8) / 2, COMPONENT_COLORS[i]);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        System.out.println(button);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.isDragging = true;
        this.values[selectedIndex] += Math.round(deltaX * 1000) / 1000f;
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        System.out.println("moved");
        return super.isMouseOver(mouseX, mouseY);
    }

    public float getX() {
        return this.values[0];
    }

    public float getY() {
        return this.values[1];
    }

    public float getZ() {
        return this.values[2];
    }

    public void setData(float x, float y, float z) {
        this.values[0] = x;
        this.values[1] = y;
        this.values[2] = z;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
