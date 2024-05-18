package org.MonsterBattler;

import java.util.ArrayList;

public class TurnDisplayList {
    public ArrayList<TurnDisplayElement> displayList;

    public TurnDisplayList() {
        this.displayList = new ArrayList<>();
    }

    public void addMSGToList(TurnDisplayElement msgElement) {
        this.displayList.add(msgElement);
    }

    public ArrayList<TurnDisplayElement> getDisplayList() {
        return this.displayList;
    }

    public void setDisplayList(ArrayList<TurnDisplayElement> displayList) {
        this.displayList = displayList;
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Append the queue elements manually
        jsonBuilder.append("\"displayQ\":[");

        for (TurnDisplayElement element : this.displayList) {
            jsonBuilder.append(element.toJson()).append(",");
        }

        // Remove the trailing comma if there are elements in the queue
        if (!displayList.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("]");

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    @Override
    public String toString() {
        return "TurnDisplayList{" +
                "displayList=" + displayList +
                '}';
    }
}

