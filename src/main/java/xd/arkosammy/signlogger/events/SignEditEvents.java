package xd.arkosammy.signlogger.events;

public enum SignEditEvents {
    CHANGED_TEXT("changed_text"),
    WAXED_SIGN("waxed_sign"),
    DYED_SIGN("dyed_sign"),
    GLOWED_SIGN("glowed_sign"),
    UNGLOWED_SIGN("unglowed_sign");

    private final String eventTypeString;


    SignEditEvents(String eventTypeString){
        this.eventTypeString = eventTypeString;
    }

    public String getEventTypeString(){
        return this.eventTypeString;
    }

    public static SignEditEvents getFromEventTypeString(String eventTypeString){
        for(SignEditEvents signEditEvent : SignEditEvents.values()){
            if(signEditEvent.eventTypeString.equals(eventTypeString)){
                return signEditEvent;
            }
        }
        return CHANGED_TEXT;
    }


}
