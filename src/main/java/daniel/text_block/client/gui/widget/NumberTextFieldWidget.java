package daniel.text_block.client.gui.widget;

import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.apache.commons.lang3.math.NumberUtils;

public class NumberTextFieldWidget extends TextFieldWidget {
    public NumberTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        String toBeString = this.getText() + chr;
        if (!NumberUtils.isCreatable(toBeString)) {
            return false;
        }
        return super.charTyped(chr, modifiers);
    }
}
