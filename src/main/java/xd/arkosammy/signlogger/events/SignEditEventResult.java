package xd.arkosammy.signlogger.events;

import java.time.LocalDateTime;

public record SignEditEventResult(String author, String blockPos, String worldRegistryKey, SignEditText originalText, SignEditText newText, LocalDateTime timestamp, boolean isFrontSide) {

    @Override
    public String toString(){
        return String.format("[%s] %s edited the %s-side text of a sign at %s in %s, from %s, to %s",
                this.timestamp,
                this.author,
                this.isFrontSide ? "front" : "back",
                this.blockPos,
                SignEditEvent.getWorldRegistryKeyAsAltString(this.worldRegistryKey),
                this.originalText.toString(),
                this.newText.toString());
    }

    public static class Builder {

        private String author = "NULL";
        private String blockPos = "NULL";
        private String worldRegistryKey = "NULL";
        private SignEditText originalText = new SignEditText(new String[]{"", "", "", ""});
        private SignEditText newText = new SignEditText(new String[]{"", "", "", ""});
        private final LocalDateTime timestamp;
        private final boolean isFrontSide;

        public Builder(LocalDateTime timestamp, boolean isFrontSide){
            this.timestamp = timestamp;
            this.isFrontSide = isFrontSide;
        }

        public Builder withAuthor(String author){
            this.author = author;
            return this;
        }

        public Builder withBlockPos(String pos){
            this.blockPos = pos;
            return this;
        }

        public Builder withWorldRegistryKey(String worldRegistryKey){
            this.worldRegistryKey = worldRegistryKey;
            return this;
        }

        public Builder withOriginalText(String[] messages){
            this.originalText = new SignEditText(messages);
            return this;
        }

        public Builder withNewText(String[] messages){
            this.newText = new SignEditText(messages);
            return this;
        }

        public SignEditEventResult build(){
            return new SignEditEventResult(this.author, this.blockPos, this.worldRegistryKey, this.originalText, this.newText, this.timestamp, this.isFrontSide);
        }


    }

}
