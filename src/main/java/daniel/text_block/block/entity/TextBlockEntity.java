package daniel.text_block.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import daniel.text_block.TextBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TextBlockEntity extends BlockEntity {
    private boolean billboard;
    private boolean distanceScaled;
    private Text text = Text.translatable("block.text_block.text_block");
    private final Vec3f rotation = new Vec3f(0, 0, 0);
    private final Vec3f offset = new Vec3f(0, 0, 0);
    private final Vec3f scale = new Vec3f(0.1f, 0.1f,0.1f);

    public TextBlockEntity(BlockPos pos, BlockState state) {
        super(TextBlock.TEXT_BLOCK_ENTITY, pos, state);
    }

    public boolean isDistanceScaled() {
        return this.distanceScaled;
    }

    public void setDistanceScaled(boolean value) {
        this.distanceScaled = value;
    }

    public void setOffset(float x, float y, float z) {
        this.offset.set(x, y, z);
    }

    public Vec3f getOffset() {
        return this.offset;
    }

    public Vec3f getScale() {
        return this.scale;
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
    }

    public Vec3f getRotation() {
        return this.rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
    }

    public boolean isBillboard() {
        return billboard;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
        this.text = text == null ? Text.empty() : text;
    }



    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    private Text parseTextFromJson(String json) {
        Text text = this.unparsedTextFromJson(json);
        if (this.world instanceof ServerWorld) {
            try {
                return Texts.parse(this.getCommandSource(), text, null, 0);
            }
            catch (CommandSyntaxException commandSyntaxException) {
            }
        }
        return text;
    }

    private Text unparsedTextFromJson(String json) {
        try {
            MutableText text = Text.Serializer.fromJson(json);
            if (text != null) {
                return text;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return ScreenTexts.EMPTY;
    }

    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(this.pos), Vec2f.ZERO, (ServerWorld)this.world, 2, "Text Block", text, this.world.getServer(), null);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("Text", Text.Serializer.toJson(this.text));
        nbt.putBoolean("Billboard", billboard);
        nbt.putBoolean("DistanceScaled", distanceScaled);
        nbt.putFloat("ScaleX", scale.getX());
        nbt.putFloat("ScaleY", scale.getY());
        nbt.putFloat("ScaleZ", scale.getZ());
        nbt.putFloat("OffsetX", offset.getX());
        nbt.putFloat("OffsetY", offset.getY());
        nbt.putFloat("OffsetZ", offset.getZ());
        nbt.putFloat("RotationX", rotation.getX());
        nbt.putFloat("RotationY", rotation.getY());
        nbt.putFloat("RotationZ", rotation.getZ());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.text = this.parseTextFromJson(nbt.getString("Text"));
        this.billboard = nbt.getBoolean("Billboard");
        this.distanceScaled = nbt.getBoolean("DistanceScaled");

        this.offset.set(nbt.getFloat("OffsetX"), nbt.getFloat("OffsetY"), nbt.getFloat("OffsetZ"));
        this.scale.set(nbt.getFloat("ScaleX"), nbt.getFloat("ScaleY"), nbt.getFloat("ScaleZ"));
        this.rotation.set(nbt.getFloat("RotationX"), nbt.getFloat("RotationY"), nbt.getFloat("RotationZ"));
    }
}
