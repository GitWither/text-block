package daniel.text_block.client.render.block;

import daniel.text_block.block.entity.TextBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.*;

public class TextBlockRenderer implements BlockEntityRenderer<TextBlockEntity> {

    private final TextRenderer textRenderer;

    public TextBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(TextBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

        matrices.push();

        matrices.translate(0.5f, 1, 0.5f);

        Vector3f scale = entity.getScale();
        Vector3f offset = entity.getOffset();
        Vector3f rotation = entity.getRotation();

        if (entity.isBillboard()) {
            matrices.multiply(dispatcher.getRotation());
            matrices.scale(-scale.x(), -scale.y(), scale.z());
        }
        else {
            matrices.translate(offset.x(), offset.y(), offset.z());
            matrices.multiply(new Quaternionf().rotationXYZ(rotation.x(), rotation.y(), rotation.z()));
            matrices.scale(scale.x(), -scale.y(), scale.z());
        }

        if (entity.isDistanceScaled()) {
            float distance = MathHelper.sqrt((float) MinecraftClient.getInstance().cameraEntity.squaredDistanceTo(entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ()));
            distance *= 0.2f;
            matrices.scale(distance, distance, distance);
        }

        float opacityOption = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int opacity = (int)(opacityOption * 255.0f) << 24;

        Matrix4f pos = matrices.peek().getPositionMatrix();


        float x = -textRenderer.getWidth(entity.getText()) / 2f;
        textRenderer.draw(entity.getText(), x, 0, 0x20FFFFFF, false, pos, vertexConsumers, TextRenderer.TextLayerType.NORMAL, opacity, light);

        matrices.pop();
    }
}
