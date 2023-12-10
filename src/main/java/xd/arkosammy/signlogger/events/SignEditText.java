package xd.arkosammy.signlogger.events;

import net.minecraft.block.entity.SignText;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;


public class SignEditText {

    private final String[] textLines = new String[]{"", "", "", ""};

    public SignEditText(SignText signText){
        Text[] messages = signText.getMessages(false);
        for(int i = 0; i < 4; i++){
            textLines[i] = messages[i].getString();
        }
    }

    public SignEditText(List<FilteredMessage> messageList){
        FilteredMessage[] filteredMessages = messageList.toArray(new FilteredMessage[0]);
        for(int i = 0; i < 4; i++){
            textLines[i] = filteredMessages[i].getString();
        }
    }

    public SignEditText(String[] messages){
        System.arraycopy(messages, 0, textLines, 0, 4);
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
