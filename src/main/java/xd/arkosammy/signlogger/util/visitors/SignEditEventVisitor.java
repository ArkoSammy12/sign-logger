package xd.arkosammy.signlogger.util.visitors;

import xd.arkosammy.signlogger.events.*;

public interface SignEditEventVisitor {

    void visit(ChangedTextSignEvent changedTextSignEvent);

    void visit(WaxedSignEvent waxedSignEvent);

    void visit(DyedSignEvent dyedSignEvent);

    void visit(GlowedSignEvent glowedSignEvent);

}
