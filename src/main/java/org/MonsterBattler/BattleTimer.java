package org.MonsterBattler;

/**
 * I don't know if this will be used but could be useful
 */
public class BattleTimer {
    public String name;
    public int timer;
    public Effect effect;

    public BattleTimer() {
        this.name = "default";
        this.timer = 10;
        this.effect = null;
    }

    public BattleTimer(String name, int timer, Effect effect) {
        this.name = name;
        this.timer = timer;
        this.effect = effect;
    }

    /**
     * @return True if the timer is still ticking
     */
    public boolean tick() {
        this.timer--;
        return this.timer >= 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

}
