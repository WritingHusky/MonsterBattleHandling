package org.MonsterBattler;

public class TurnDisplayElementFactory {
    public TurnDisplayElementFactory() {
    }

    public static TurnDisplayElement create(String activationMsg, String slot, int msgCode, String resultMsg, String resultSlot) {
        return new TurnDisplayElement(activationMsg, slot, msgCode, resultMsg, resultSlot);
    }
}
