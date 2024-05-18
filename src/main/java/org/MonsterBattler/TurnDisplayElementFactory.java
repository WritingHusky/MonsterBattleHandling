package org.MonsterBattler;

public class TurnDisplayElementFactory {
    public TurnDisplayElementFactory() {
    }

    public static TurnDisplayElement create(String messageType, String message) {
        return new TurnDisplayElement(messageType, message);
    }

    public static TurnDisplayElement createErrorEffectResolver(Effect effect){
        return TurnDisplayElementFactory.create("Error", "Nothing Happened (Effect resolver failed for effect: " + effect + ")");
    }

    public static TurnDisplayElement createErrorImplementation(Effect effect){
        return TurnDisplayElementFactory.create("Error", "Nothing Happened (Effect Not implemented for effect: " + effect + ")");
    }
}
