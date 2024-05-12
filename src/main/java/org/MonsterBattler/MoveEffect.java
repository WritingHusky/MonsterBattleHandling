package org.MonsterBattler;

import java.util.LinkedList;
import java.util.Queue;

public class MoveEffect {
    public Queue<Effect> moveEffects;
    public String moveName;
    public int priority;

    public int accuracy;
    public int power;
    public String typing;

    public String source;
    public String target;

    public MoveEffect() {
        this.moveEffects = new LinkedList<>();
        this.moveName = "Default";
        this.priority = 0;
        this.accuracy = 100;
        this.power = 0;
        this.source = "";
        this.target = "";
        this.typing = "";
    }

    public MoveEffect(String moveName, int priority, int accuracy, int power, String typing, Queue<Effect> moveEffects) {
        this.moveName = moveName;
        this.priority = priority;
        this.accuracy = accuracy;
        this.power = power;
        this.typing = typing;
        this.moveEffects = moveEffects;

        this.source = "";
        this.target = "";
    }

    /**
     * @return {String} The slot code of the source of the attack
     */
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return {String} The slot code of the target of the attack
     */
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Queue<Effect> getMoveEffects() {
        return moveEffects;
    }

    public void setMoveEffects(Queue<Effect> moveEffects) {
        this.moveEffects = moveEffects;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    @Override
    public String toString() {
        return String.format("MoveEffect[moveName=%s, priority=%d, accuracy=%d, power=%d, typing=%s, source=%s, target=%s, moveEffects=%s]",
                moveName, priority, accuracy, power, typing, source, target, moveEffects);
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Append fields manually
        jsonBuilder.append("\"moveEffects\":").append("[");
        for (Effect effect : moveEffects) {
            jsonBuilder.append(effect.toJson()).append(",");
        }
        if (!moveEffects.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove the trailing comma
        }
        jsonBuilder.append("],");

        jsonBuilder.append("\"moveName\":\"").append(moveName).append("\",");
        jsonBuilder.append("\"priority\":").append(priority).append(",");
        jsonBuilder.append("\"accuracy\":").append(accuracy).append(",");
        jsonBuilder.append("\"power\":").append(power).append(",");
        jsonBuilder.append("\"typing\":\"").append(typing).append("\",");
        jsonBuilder.append("\"source\":\"").append(source).append("\",");
        jsonBuilder.append("\"target\":\"").append(target).append("\"");

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

}
