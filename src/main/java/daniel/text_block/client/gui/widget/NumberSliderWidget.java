package daniel.text_block.client.gui.widget;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class NumberSliderWidget extends SliderWidget {
    private final ValueChange messageUpdateCallback;
    private final ValueChange valueApplyCallback;

    public NumberSliderWidget(int x, int y, int width, int height, Text text, double value, ValueChange messageUpdate, ValueChange valueApply) {
        super(x, y, width, height, text, value);
        this.messageUpdateCallback = messageUpdate;
        this.valueApplyCallback = valueApply;
    }

    @Override
    protected void updateMessage() {
        messageUpdateCallback.onValueChanged(this);
    }

    @Override
    protected void applyValue() {
        valueApplyCallback.onValueChanged(this);
    }

    public interface ValueChange {
        void onValueChanged(NumberSliderWidget slider);
    }
}
