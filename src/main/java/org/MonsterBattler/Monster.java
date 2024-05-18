package org.MonsterBattler;

import java.util.Arrays;
import java.util.Objects;

public class Monster {

    public int currentHp;
    public int maxHp;
    public String name;
    public int dexID;
    public String monsterCode;
    public String status = "None";
    public int statusTimer;
    public Boolean isDead = false;
    public int team;
    public String position = "Ground";

    public MoveEffect abilityEffect;
    public String abilityTrigger;
    /**
     * Stat order:<br>
     * </br>
     * 0: HP <br>
     * </br>
     * 1: Attack <br>
     * </br>
     * 2: Defence<br>
     * </br>
     * 3: Sp. Attack<br>
     * </br>
     * 4: Sp. Defence<br>
     * </br>
     * 5: Speed
     */
    public int[] stats;
    public int level;
    public String type;
    public String slot;
    public MoveEffect[] moves;

    public int[] evValues = new int[6];
    public int[] ivValues = new int[6];
    public int[] generatedStats = new int[6];
    public int[] statusEffectedStats = new int[6];
    public int[] beyondEffectedStats = new int[6];

    public Monster() {
    }

    public Monster(String name, int dexID, int[] stats, int level, String type, MoveEffect[] moves) {
        this.name = name;
        this.dexID = dexID;
        this.stats = stats;
        this.level = level;
        generateStats();
        this.type = type;
        this.moves = moves;
    }

    /**
     * 
     * @return int[] of the stats to use for calc etc
     */
    public int[] getStats() {
        int[] resultStats = new int[6];
        for (int i = 0; i < 6; i++) {
            double statMod = 0;
            double value = this.statusEffectedStats[i] + this.beyondEffectedStats[i];
            if (value > 0) {
                statMod = value / 3;
            } else if (value < 0) {
                statMod = 3 / value;
            }
            int result = (int) Math.round(this.generatedStats[i] + this.generatedStats[i] * statMod);
            if (result <= 0)
                result = 1;
            resultStats[i] = result;
        }
        return resultStats;
    }

    public void effectStat(int index, int value) {
        this.statusEffectedStats[index] += value;
        if (this.statusEffectedStats[index] > 6)
            this.statusEffectedStats[index] = 6;
        else if (this.statusEffectedStats[index] < -6)
            this.statusEffectedStats[index] = -6;
    }

    public void effectStatBeyond(int index, int value) {
        this.beyondEffectedStats[index] += value;
    }

    public void setStats(int[] stats) {
        this.stats = stats;
    }

    public MoveEffect getAbility() {
        return this.abilityEffect;
    }

    public void setAbility(MoveEffect abilityEffect) {
        this.abilityEffect = abilityEffect;
    }

    public boolean doesAbilityTrigger(String effectCode) {
        return Objects.equals(effectCode, this.abilityTrigger);
    }

    public void setStatus(String status) {
        this.statusTimer = 0;
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    /**
     * Active Status: <br>
     * </br>
     * <br>
     * Burn</br>
     * <br>
     * Poison</br>
     * <br>
     * Badly Poison</br>
     */
    public void tickStatusActive(TurnInfoPackage turnInfoPackage) {

        TurnDisplayElement msgElement = new TurnDisplayElement();
        int damage;
        String message;
        switch (this.getStatus()) {
            case "Burn":
                // Burn does 1/16 of the max HP
                damage = this.getMaxHp() / 16;
                // It does min 1 damage
                if (damage <= 0)
                    damage = 1;
                // Do the damage
                this.doDamage(damage, turnInfoPackage);
                // Create the message
                message = this.getName() + ":" + this.getMonsterCode() + " was hurt by it's Burn and took " + damage + " damage";
                msgElement = TurnDisplayElementFactory.create("Post Effect", message);
                // Increase the status timer
                this.statusTimer++;
                break;
            case "Poison":
                // Poison does 1/8 of the max HP
                damage = this.getMaxHp() / 8;
                // It does min 1 damage
                if (damage <= 0)
                    damage = 1;
                // Do the damage
                this.doDamage(damage, turnInfoPackage);
                // Create the message
                message = this.getName() + ":" + this.getMonsterCode() + " was hurt by Poison and took " + damage + " damage";
                msgElement = TurnDisplayElementFactory.create("Post Effect", message);
                // Increase the status timer
                this.statusTimer++;
                break;
            case "Badly Poison":
                // Badly Poison does 1/16 of the max HP + 1/16 of the max HP for each turn
                damage = (this.getMaxHp() + this.getMaxHp() * this.statusTimer) / 16;
                // It does min 1 damage
                if (damage <= 0)
                    damage = 1;
                // Do the damage
                this.doDamage(damage, turnInfoPackage);
                // Create the message
                message = this.getName() + ":" + this.getMonsterCode() + " was hurt by Badly Poisoned and took " + damage + " damage";
                msgElement = TurnDisplayElementFactory.create("Post Effect", message);
                // Increase the status timer
                this.statusTimer++;
                break;
            default:
                // If the status does not proc then nothing happens
                break;
        }

        turnInfoPackage.getTurnDisplayList().addMSGToList(msgElement);
    }

    public void tickStatusPassive(TurnInfoPackage turnInfoPackage) {

    }

    public void doDamage(int damage, TurnInfoPackage turnInfoPackage) {
        try {
            this.currentHp = Math.subtractExact(this.currentHp, damage);
        } catch (ArithmeticException e) {
            this.currentHp = this.maxHp;
        }
        if (this.currentHp <= 0) {
            this.currentHp = 0;
            this.setDeath(true);
        }

        if (this.currentHp > this.maxHp){
            this.currentHp = this.maxHp;
        }

    }

    /**
     * Calculates the damage dealt by a move
     * @param turnInfoPackage the turnInfoPackage
     * @param effect the effect that does the damage
     * @param attacker the Attacker of the move
     * @param isPhysicalAttack is the move physical
     * @param isCritical is the move a critical hit
     * @param randomValue the random value for the damage between (85 - 100) / 100
     * @param typeVal the type advantage value (2x the effectiveness)
     * @return the damage dealt
     */
    public int getDamageByCalculation(TurnInfoPackage turnInfoPackage, Effect effect, String attacker,
                                      boolean isPhysicalAttack, boolean isCritical, double randomValue, int typeVal) {

        String moveType = effect.getMoveType();
        int attackPower = Integer.parseInt(effect.getEffectValue());

        int attackStat;
        int defenceStat;
        if (isPhysicalAttack) {
            defenceStat = this.getStats()[2];
            attackStat = turnInfoPackage.getMonsterBySlot(attacker).getStats()[1];
        } else {
            defenceStat = this.getStats()[4];
            attackStat = turnInfoPackage.getMonsterBySlot(attacker).getStats()[2];
        }

        double criticalValue;
        if (isCritical)
            criticalValue = 2;
        else
            criticalValue = 1;

        double stab = 1;
        if (Objects.equals(this.getType(), moveType)) {
            stab = 1.5;
        }

        double totalDMG = (double) (2 * this.level) / 5 + 2;
        totalDMG *= (double) attackPower * ((double) attackStat / defenceStat);
        totalDMG = totalDMG / 50 + 2;
        double coefficient =  criticalValue * stab * ((double) typeVal / 2) * randomValue;
        totalDMG = totalDMG * coefficient ;

        int damageDealt = (int) Math.ceil(totalDMG);

        if (damageDealt == 0 && typeVal != 0)
            damageDealt = 1;

        return damageDealt;
    }

    public Boolean isDead() {
        return isDead;
    }

    public void setDeath(Boolean isDead) {
        this.isDead = isDead;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDexID() {
        return dexID;
    }

    public void setDexID(int dexID) {
        this.dexID = dexID;
    }

    public String getMonsterCode() {
        return monsterCode;
    }

    public void setMonsterCode(String monsterCode) {
        this.monsterCode = monsterCode;
    }

    public Boolean getIsDead() {
        return isDead;
    }

    public void setIsDead(Boolean isDead) {
        this.isDead = isDead;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatusTimer() {
        return statusTimer;
    }

    public void setStatusTimer(int statusTimer) {
        this.statusTimer = statusTimer;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public MoveEffect[] getMoves() {
        return moves;
    }

    public void setMoves(MoveEffect[] moves) {
        this.moves = moves;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void generateHealth() {
        this.maxHp = this.generatedStats[0];
        this.currentHp = this.maxHp;
    }

    public void generateStats() {
        // Set the HP
        int stat;
        stat = (this.stats[0] * 2) + this.ivValues[0] + (this.evValues[0] / 4);
        stat = (int) (double) ((stat * this.level) / 100) + this.level + 10;

        this.generatedStats[0] = stat;

        for (int i = 1; i < this.stats.length; i++) {
            stat = (this.stats[i] * 2) + this.ivValues[i] + (this.evValues[i] / 4);
            stat = (int) (double) ((stat * this.level) / 100) + 5;

            this.generatedStats[i] = stat;
        }
        generateHealth();
    }

    public MoveEffect getAbilityEffect() {
        return abilityEffect;
    }

    public void setAbilityEffect(MoveEffect abilityEffect) {
        this.abilityEffect = abilityEffect;
    }

    public String getAbilityTrigger() {
        return abilityTrigger;
    }

    public void setAbilityTrigger(String abilityTrigger) {
        this.abilityTrigger = abilityTrigger;
    }

    public int[] getEvValues() {
        return evValues;
    }

    public void setEvValues(int[] evValues) {
        this.evValues = evValues;
    }

    public int[] getIvValues() {
        return ivValues;
    }

    public void setIvValues(int[] ivValues) {
        this.ivValues = ivValues;
    }

    public int[] getGeneratedStats() {
        return generatedStats;
    }

    public void setGeneratedStats(int[] generatedStats) {
        this.generatedStats = generatedStats;
    }

    public int[] getStatusEffectedStats() {
        return statusEffectedStats;
    }

    public void setStatusEffectedStats(int[] statusEffectedStats) {
        this.statusEffectedStats = statusEffectedStats;
    }

    public int[] getBeyondEffectedStats() {
        return beyondEffectedStats;
    }

    public void setBeyondEffectedStats(int[] beyondEffectedStats) {
        this.beyondEffectedStats = beyondEffectedStats;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Append fields manually
        jsonBuilder.append("\"name\":\"").append(name).append("\",");
        jsonBuilder.append("\"currentHp\":").append(currentHp).append(",");
        jsonBuilder.append("\"maxHp\":").append(maxHp).append(",");
        jsonBuilder.append("\"dexID\":").append(dexID).append(",");
        jsonBuilder.append("\"monsterCode\":\"").append(monsterCode).append("\",");
        jsonBuilder.append("\"status\":\"").append(status).append("\",");
        jsonBuilder.append("\"statusTimer\":").append(statusTimer).append(",");
        jsonBuilder.append("\"isDead\":").append(isDead).append(",");
        jsonBuilder.append("\"team\":").append(team).append(",");
        jsonBuilder.append("\"position\":\"").append(position).append("\",");
        jsonBuilder.append("\"stats\":").append(Arrays.toString(stats)).append(",");
        jsonBuilder.append("\"level\":").append(level).append(",");
        jsonBuilder.append("\"type\":\"").append(type).append("\",");
        jsonBuilder.append("\"slot\":\"").append(slot).append("\",");

        //Iterate over moves and append to json
        jsonBuilder.append("\"moves\":").append("[");
        for (int i = 0; i < moves.length; i++) {
            jsonBuilder.append(moves[i].toJson());
            if (i < moves.length - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("],");

        jsonBuilder.append("\"generatedStats\":").append(Arrays.toString(generatedStats)).append(",");
        jsonBuilder.append("\"statusEffectedStats\":").append(Arrays.toString(statusEffectedStats)).append(",");
        jsonBuilder.append("\"beyondEffectedStats\":").append(Arrays.toString(beyondEffectedStats));

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

}
