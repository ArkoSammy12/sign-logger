package xd.arkosammy.signlogger.events;

import net.minecraft.block.entity.SignText;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public record SignEditText(String[] textLines) {

    public SignEditText(SignText signText) {
        this(Arrays.stream(signText.getMessages(false)).map(Text::getString).toArray(String[]::new));
    }

    public SignEditText(List<FilteredMessage> messageList) {
        this(Arrays.stream(messageList.toArray(FilteredMessage[]::new)).map(FilteredMessage::getString).toArray(String[]::new));
    }

    public SignEditText(String[] textLines) {
        this.textLines = new String[]{"", "", "", ""};
        System.arraycopy(textLines, 0, this.textLines, 0, Math.min(textLines.length, 4));
    }

    public String[] getTextLines(){
        return this.textLines;
    }

    @Override
    public String toString(){
        return String.format("{\"%s\", \"%s\", \"%s\", \"%s\"}", this.textLines[0], this.textLines[1], this.textLines[2], this.textLines[3]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignEditText that)) return false;
        return Arrays.equals(getTextLines(), that.getTextLines());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getTextLines());
    }
}
