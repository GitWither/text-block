package daniel.text_block;

import daniel.text_block.block.TextBlockBlock;
import daniel.text_block.block.entity.TextBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class TextBlock implements ModInitializer {

    public static final String MOD_ID = "text_block";

    public static final Block TEXT_BLOCK = Registry.register(
            Registry.BLOCK,
            new Identifier(MOD_ID, "text_block"),
            new TextBlockBlock()
    );

    public static final Item TEXT_BLOCK_ITEM = Registry.register(
            Registry.ITEM,
            new Identifier(MOD_ID, "text_block"),
            new BlockItem(TEXT_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS))
    );

    public static final BlockEntityType<TextBlockEntity> TEXT_BLOCK_ENTITY =
            Registry.register(
                    Registry.BLOCK_ENTITY_TYPE,
                    new Identifier(MOD_ID, "text_block"),
                    FabricBlockEntityTypeBuilder.create(TextBlockEntity::new, TEXT_BLOCK).build()
            );

    public static final Identifier TEXT_BLOCK_UPDATE_PACKET = new Identifier(MOD_ID, "text_block_update");

    public static final Identifier OPEN_TEXT_BLOCK_SCREEN_PACKET_ID = new Identifier(MOD_ID, "open_text_block_screen");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(TEXT_BLOCK_UPDATE_PACKET, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            Text text = buf.readText();
            boolean isBillboard = buf.readBoolean();
            boolean isDistanceScaled = buf.readBoolean();
            float offsetX = buf.readFloat();
            float offsetY = buf.readFloat();
            float offsetZ = buf.readFloat();
            float rotationX = buf.readFloat();
            float rotationY = buf.readFloat();
            float rotationZ = buf.readFloat();
            float scaleX = buf.readFloat();
            float scaleY = buf.readFloat();
            float scaleZ = buf.readFloat();
            server.execute(() -> {
                ServerWorld world = player.getWorld();
                BlockEntity textBlock = world.getBlockEntity(pos);
                BlockState state = world.getBlockState(pos);
                if (!(textBlock instanceof TextBlockEntity textBlockEntity)) return;

                textBlockEntity.setText(text);
                textBlockEntity.setBillboard(isBillboard);
                textBlockEntity.setDistanceScaled(isDistanceScaled);
                textBlockEntity.setOffset(offsetX, offsetY, offsetZ);
                textBlockEntity.setRotation(rotationX, rotationY, rotationZ);
                textBlockEntity.setScale(scaleX, scaleY, scaleZ);

                textBlockEntity.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            });
        });
    }
}
