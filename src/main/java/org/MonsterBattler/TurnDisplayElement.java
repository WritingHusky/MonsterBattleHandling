package org.MonsterBattler;

public class TurnDisplayElement {
    /**
     * The message to send before animation
     */
    public String activationMsg;
    public String activationSlot;
    /**
     * The message to send after animation
     */
    public String resultMsg;
    public String resultSlot;

    /**
     * See notes about what codes mean what
     */
    public int msgCode;

    public TurnDisplayElement() {
        this.activationMsg = "Default";
        this.activationSlot = "";
        this.msgCode = 2;
        this.resultMsg = "Constructor";
        this.resultSlot = "";
    }

    public TurnDisplayElement(String activationMsg, String activationSlot, int msgCode, String resultMsg, String resultSlot) {
        this.activationMsg = activationMsg;
        this.activationSlot = activationSlot;
        this.msgCode = msgCode;
        this.resultMsg = resultMsg;
        this.resultSlot = resultSlot;
    }

    public void setElement(String activationMsg, String slot, int msgCode, String resultMsg, String resultSlot) {
        this.activationMsg = activationMsg;
        this.activationSlot = slot;
        this.msgCode = msgCode;
        this.resultMsg = resultMsg;
        this.resultSlot = resultSlot;
    }

    public String getActivationMsg() {
        return activationMsg;
    }

    public void setActivationMsg(String msg) {
        this.activationMsg = msg;
    }

    public String getActivationSlot() {
        return activationSlot;
    }

    public void setActivationSlot(String activationSlot) {
        this.activationSlot = activationSlot;
    }

    public int getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(int msgCode) {
        this.msgCode = msgCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String move) {
        this.resultMsg = move;
    }

    public String getResultSlot() {
        return resultSlot;
    }

    public void setResultSlot(String resultSlot) {
        this.resultSlot = resultSlot;
    }

    @Override
    public String toString() {
        return "TurnDisplayElement{" +
                "activationMsg='" + activationMsg + '\'' +
                ", activationSlot='" + activationSlot + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", resultSlot='" + resultSlot + '\'' +
                ", msgCode=" + msgCode +
                '}';
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Append fields manually
        jsonBuilder.append("\"activationMsg\":\"").append(activationMsg).append("\",");
        jsonBuilder.append("\"activationSlot\":\"").append(activationSlot).append("\",");
        jsonBuilder.append("\"resultMsg\":\"").append(resultMsg).append("\",");
        jsonBuilder.append("\"resultSlot\":\"").append(activationSlot).append("\",");
        jsonBuilder.append("\"msgCode\":").append(msgCode);

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
