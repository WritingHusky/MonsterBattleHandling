package org.MonsterBattler;

public class Effect {

    /**
     * The string representation of when the effect should occur
     */
    public String trigger;
    public String failedTriggerMSG;
    /**
     * The string that represents the outcome of the effect
     */
    public String resultCode;
    /**
     * The string representation of the value associated with the value accosted with
     * an effect
     */
    public String effectValue;
    /**
     * The type of effect (Damage, Alter, State, or None)
     */
    public String attackType;
    public String moveType;

    public Effect() {
        this.trigger = "True";
        this.failedTriggerMSG = "Default constructor msg";
        this.resultCode = "None";
        this.effectValue = "N/a";
        this.attackType = "None";
    }

    public Effect(String attackType, String result, String effectValue, String trigger, String failedTriggerMSG, String moveType) {
        this.resultCode = result.toUpperCase();
        this.effectValue = effectValue;
        this.trigger = trigger;
        this.failedTriggerMSG = failedTriggerMSG;
        this.attackType = attackType.toUpperCase();
        this.moveType = moveType;
    }

    public String getFailedTriggerMSG() {
        return failedTriggerMSG;
    }

    public void setFailedTriggerMSG(String failedTriggerMSG) {
        this.failedTriggerMSG = failedTriggerMSG;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getEffectValue() {
        return this.effectValue;
    }

    public void setEffectValue(String effectCode) {
        this.effectValue = effectCode;
    }

    /**
     * depreciated by the effect resolver
     * 
     * @param turnInfoPackage All the turnInfo
     * @return {boolean} If the given effect triggers based on the current state
     */
    public boolean triggers(TurnInfoPackage turnInfoPackage) {

        return switch (this.trigger) {
            case "Always", "True" -> true;
            default -> false;
        };
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getAttackType() {
        return attackType;
    }

    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }

    public String getMoveType() {
        return moveType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    @Override
    public String toString() {
        return "Effect [trigger=" + trigger + ", failedTriggerMSG=" + failedTriggerMSG + ", resultCode=" + resultCode
                + ", effectValue=" + effectValue + ", attackType=" + attackType + ", moveType=" + moveType + "]";
    }
    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        jsonBuilder.append("\"trigger\":\"").append(trigger).append("\",");
        jsonBuilder.append("\"failedTriggerMSG\":\"").append(failedTriggerMSG).append("\",");
        jsonBuilder.append("\"resultCode\":\"").append(resultCode).append("\",");
        jsonBuilder.append("\"effectValue\":\"").append(effectValue).append("\",");
        jsonBuilder.append("\"attackType\":\"").append(attackType).append("\",");
        jsonBuilder.append("\"moveType\":\"").append(moveType).append("\"");

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

}
