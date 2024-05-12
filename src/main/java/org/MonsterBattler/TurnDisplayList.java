package org.MonsterBattler;

import java.util.LinkedList;
import java.util.Queue;

public class TurnDisplayList {
    public Queue<TurnDisplayElement> displayQ;

    public TurnDisplayList() {
        this.displayQ = new LinkedList<>();
    }

    public void addMSGToList(TurnDisplayElement msgElement) {
        this.displayQ.add(msgElement);
    }

    public Queue<TurnDisplayElement> getDisplayQ() {
        return this.displayQ;
    }

    public void setDisplayQ(Queue<TurnDisplayElement> displayQ) {
        this.displayQ = displayQ;
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Append the queue elements manually
        jsonBuilder.append("\"displayQ\":[");

        for (TurnDisplayElement element : this.displayQ) {
            jsonBuilder.append(element.toJson()).append(",");
        }

        // Remove the trailing comma if there are elements in the queue
        if (!displayQ.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("]");

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

}

