package org.MonsterBattler;

import java.util.LinkedList;
import java.util.Queue;

public class MonsterTurnInfo {
    // Monster
    // private Monster monster;
    // private int turnCount;
    // PreMove effects
    /**
     * Examples:
     * Transformations
     * Weather
     * Terrain
     */
    public Queue<MoveEffect> preMoveEffects;

    // Move
    MoveEffect move;

    // EndMove effects
    /**
     * Examples are post-move transformations
     */
    public Queue<MoveEffect> endMoveEffects;

    public MonsterTurnInfo() {
        // this.turnCount = 0;
        this.preMoveEffects = new LinkedList<>();
        this.endMoveEffects = new LinkedList<>();
        this.move = new MoveEffect();
    }

    // public Monster getMonster() {
    // return monster;
    // }

    // public void setMonster(Monster monster) {
    // this.monster = monster;
    // }

    public Queue<MoveEffect> getPreMoveEffects() {
        return preMoveEffects;
    }

    public void setPreMoveEffects(Queue<MoveEffect> preMoveEffects) {
        this.preMoveEffects = preMoveEffects;
    }

    public MoveEffect getMove() {
        return move;
    }

    public void setMove(MoveEffect move) {
        this.move = move;
    }

    public Queue<MoveEffect> getEndMoveEffects() {
        return endMoveEffects;
    }

    public void setEndMoveEffects(Queue<MoveEffect> endMoveEffects) {
        this.endMoveEffects = endMoveEffects;
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Append fields manually
        jsonBuilder.append("\"preMoveEffects\":").append("[");
        for (MoveEffect effect : preMoveEffects) {
            jsonBuilder.append(effect.toJson()).append(",");
        }
        if (!preMoveEffects.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove the trailing comma
        }
        jsonBuilder.append("],");

        jsonBuilder.append("\"move\":").append(move.toJson()).append(",");

        jsonBuilder.append("\"endMoveEffects\":").append("[");
        for (MoveEffect effect : endMoveEffects) {
            jsonBuilder.append(effect.toJson()).append(",");
        }
        if (!endMoveEffects.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove the trailing comma
        }
        jsonBuilder.append("]");

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

}
