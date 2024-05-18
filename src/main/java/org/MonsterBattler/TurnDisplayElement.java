package org.MonsterBattler;

public class TurnDisplayElement {
    /**
     *
     */
    public String messageType;
    public String message;

    public TurnDisplayElement() {
        this.messageType = "None";
        this.message = "None";
    }

    public TurnDisplayElement(String messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TurnDisplayElement{" +
                "messageType='" + messageType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String toJson() {

        return "{" +
                // Append fields manually
                "\"messageType\":\"" + messageType + "\"," +
                "\"message\":\"" + message + "\"" +
                "}";
    }
}
