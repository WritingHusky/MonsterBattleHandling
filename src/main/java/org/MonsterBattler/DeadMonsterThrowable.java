package org.MonsterBattler;

/**
 * This is the throwable that will be thrown so that the major system can ask
 * for the player to switch monster
 */
public class DeadMonsterThrowable extends Throwable {
    public String deadMonsterSlot;
    public DeadMonsterThrowable(String deadMonsterSlot) {
        this.deadMonsterSlot = deadMonsterSlot;
    }
}
