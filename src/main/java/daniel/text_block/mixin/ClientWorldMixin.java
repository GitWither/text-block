package daniel.text_block.mixin;

import daniel.text_block.TextBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {


    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "java/util/Set.of (Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;"))
    private static <E> Set<E> textBlock$setTextBlockMarker(E e1, E e2) {
        return (Set<E>) Set.of(e1, e2, TextBlock.TEXT_BLOCK_ITEM);
    }
}
