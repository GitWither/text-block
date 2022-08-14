package daniel.text_block.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import daniel.text_block.TextBlock;
import daniel.text_block.block.entity.TextBlockEntity;
import daniel.text_block.client.gui.widget.NumberTextFieldWidget;
import daniel.text_block.client.gui.widget.VectorSliderWidget;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class TextBlockScreen extends Screen {

    private static final Identifier TEXTURE = new Identifier(TextBlock.MOD_ID, "textures/gui/text_block.png");
    private final TextBlockEntity textBlock;

    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;

    private int x;
    private int y;

    private CheckboxWidget billboardCheckbox;
    private CheckboxWidget distanceScaledCheckbox;
    private TextFieldWidget textWidget;

    private final NumberTextFieldWidget[] rotationWidgets = new NumberTextFieldWidget[3];
    private final NumberTextFieldWidget[] scaleWidgets = new NumberTextFieldWidget[3];
    private final NumberTextFieldWidget[] offsetWidgets = new NumberTextFieldWidget[3];

    private final DecimalFormat decimalNumberFormat = new DecimalFormat("#.###");


    public TextBlockScreen(TextBlockEntity textBlock) {
        super(Text.empty());
        this.textBlock = textBlock;
    }

    @Override
    protected void init() {
        super.init();

        this.client.keyboard.setRepeatEvents(true);

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        textWidget = this.addDrawableChild(new TextFieldWidget(this.textRenderer, this.x + 4, this.y + 14, 168, 20, Text.empty()));
        textWidget.setMaxLength(512);
        textWidget.setText(Text.Serializer.toJson(textBlock.getText()));

        billboardCheckbox = this.addDrawableChild(new CheckboxWidget(this.x + 3, this.y + 40, 20, 20, Text.translatable("gui.text_block.billboard"), textBlock.isBillboard()));
        distanceScaledCheckbox = this.addDrawableChild(new CheckboxWidget(this.x + 75, this.y + 40, 20, 20, Text.translatable("gui.text_block.distance_scale"), textBlock.isDistanceScaled()));

        Vec3d offset = new Vec3d(textBlock.getOffset());
        Vec3d rotation = new Vec3d(textBlock.getRotation());
        Vec3d scale = new Vec3d(textBlock.getScale());
        for (int i = 0; i < 3; i++) {
            offsetWidgets[i] = new NumberTextFieldWidget(this.textRenderer, this.x + i * 48 + 4, this.y + 73, 40, 20, Text.translatable(""));
            offsetWidgets[i].setText(decimalNumberFormat.format(offset.getComponentAlongAxis(Direction.Axis.VALUES[i])));

            rotationWidgets[i] = new NumberTextFieldWidget(this.textRenderer, this.x + i * 48 + 4, this.y + 107, 40, 20, Text.translatable(""));
            rotationWidgets[i].setText(decimalNumberFormat.format(rotation.getComponentAlongAxis(Direction.Axis.VALUES[i])));

            scaleWidgets[i] = new NumberTextFieldWidget(this.textRenderer, this.x + i * 48 + 4, this.y + 141, 40, 20, Text.translatable(""));
            scaleWidgets[i].setText(decimalNumberFormat.format(scale.getComponentAlongAxis(Direction.Axis.VALUES[i])));

            this.addDrawableChild(offsetWidgets[i]);
            this.addDrawableChild(rotationWidgets[i]);
            this.addDrawableChild(scaleWidgets[i]);
        }

        this.addDrawableChild(new ButtonWidget(this.x, this.y + 167, 176, 20, ScreenTexts.DONE,button -> {
            textBlock.markDirty();
            this.close();
        }));
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        PacketByteBuf data = PacketByteBufs.create();
        data.writeBlockPos(this.textBlock.getPos());

        Text text;
        try {
            text = Text.Serializer.fromJson(this.textWidget.getText());
        }
        catch (Exception e) {
            text = Text.literal(this.textWidget.getText());
        }
        data.writeText(text);

        data.writeBoolean(this.billboardCheckbox.isChecked());
        data.writeBoolean(this.distanceScaledCheckbox.isChecked());

        data.writeFloat(NumberUtils.createFloat(offsetWidgets[0].getText()));
        data.writeFloat(NumberUtils.createFloat(offsetWidgets[1].getText()));
        data.writeFloat(NumberUtils.createFloat(offsetWidgets[2].getText()));

        data.writeFloat(NumberUtils.createFloat(rotationWidgets[0].getText()));
        data.writeFloat(NumberUtils.createFloat(rotationWidgets[1].getText()));
        data.writeFloat(NumberUtils.createFloat(rotationWidgets[2].getText()));

        data.writeFloat(NumberUtils.createFloat(scaleWidgets[0].getText()));
        data.writeFloat(NumberUtils.createFloat(scaleWidgets[1].getText()));
        data.writeFloat(NumberUtils.createFloat(scaleWidgets[2].getText()));

        ClientPlayNetworking.send(TextBlock.TEXT_BLOCK_UPDATE_PACKET, data);
        super.removed();
    }

    @Override
    public void renderBackground(MatrixStack matrices, int vOffset) {
        super.renderBackground(matrices, vOffset);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.textRenderer.draw(matrices, Text.translatable("gui.text_block.text"), this.x + 3, this.y + 5, 0x404040);

        this.textRenderer.draw(matrices, Text.translatable("gui.text_block.offset"), this.x + 3, this.y + 64, 0x404040);
        this.textRenderer.draw(matrices, Text.translatable("gui.text_block.rotation"), this.x + 3, this.y + 98, 0x404040);
        this.textRenderer.draw(matrices, Text.translatable("gui.text_block.scale"), this.x + 3, this.y + 132, 0x404040);

        super.render(matrices, mouseX, mouseY, delta);
    }
}
