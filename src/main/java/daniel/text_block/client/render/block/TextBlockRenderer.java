package daniel.text_block.client.render.block;

import daniel.text_block.block.entity.TextBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class TextBlockRenderer implements BlockEntityRenderer<TextBlockEntity> {

    public TextBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(TextBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

        matrices.push();

        matrices.translate(0.5f, 1, 0.5f);
        Vec3f scale = entity.getScale();
        Vec3f offset = entity.getOffset();
        if (entity.isBillboard()) {
            matrices.multiply(dispatcher.getRotation());
            matrices.scale(-scale.getX(), -scale.getY(), scale.getZ());
        }
        else {
            matrices.translate(offset.getX(), offset.getY(), offset.getZ());
            matrices.multiply(Quaternion.fromEulerXyzDegrees(entity.getRotation()));
            matrices.scale(scale.getX(), -scale.getY(), scale.getZ());
        }

        if (entity.isDistanceScaled()) {
            float distance = MathHelper.sqrt((float) MinecraftClient.getInstance().cameraEntity.squaredDistanceTo(entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ()));
            distance *= 0.2f;
            matrices.scale(distance, distance, distance);
        }

        DrawableHelper.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, entity.getText(), 0, 0, 0xffffff);
        matrices.pop();
    }
}
