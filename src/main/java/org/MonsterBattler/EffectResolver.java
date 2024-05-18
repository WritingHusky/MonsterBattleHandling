package org.MonsterBattler;

import java.util.*;

public class EffectResolver {

    /**
     * This is the function that simulator will call
     */
    public static void doEffect(Effect effect, TurnInfoPackage turnInfoPackage, String attacker, String defender) {
        TurnDisplayElement msgElement;
        // Check if the effect happens
        if (!doesEffectTrigger(effect, turnInfoPackage)) {
            // If the effect does not trigger then set the failed the msg

            msgElement = TurnDisplayElementFactory.create("Move Header", effect.getFailedTriggerMSG());
//            System.out.println("Missed: " + msgElement); // Debugging
            return;
        }
        // Do the actual effect
        msgElement = handleEffect(effect, turnInfoPackage, attacker, defender);
//        System.out.println("Hit: " + msgElement); // Debugging
        // Add the msgElement to the display list
        turnInfoPackage.getTurnDisplayList().addMSGToList(msgElement);
    }

    /*
     * Notes about how things will be resolved
     * - Accuracy check is an operate effect that will be given at the beginning of
     * the Queue
     * - Effects are split into a trigger and a result
     * - most move effects with have a trigger of "Always" or "True" to always occur
     */

    /**
     * This is the main logic of doing the actual effect
     * @param effect{Effect}                   The effect to execute
     * @param turnInfoPackage{TurnInfoPackage}
     */
    private static TurnDisplayElement handleEffect(Effect effect, TurnInfoPackage turnInfoPackage, String attacker, String defender) {
        TurnDisplayElement msgElement;

        String attackType = effect.getAttackType();
        attackType = attackType.toUpperCase();

        switch (attackType) {
            case "NONE":
                if (effect.getResultCode().equals("SWAP") ||effect.getResultCode().equals("Swap")) {
                    // swap the monsters in the turnInfoPackage
                    int attackerIndex = turnInfoPackage.convertSlotToIndex(attacker);
                    int defenderIndex = turnInfoPackage.convertSlotToIndex(defender);

                    // Do the swaps
                    // Copy the array
                    List<Monster> list =new ArrayList<>(Arrays.asList(turnInfoPackage.getMonsters()));
                    // Swap the slots
                    list.get(attackerIndex).setSlot(defender);
                    list.get(defenderIndex).setSlot(attacker);
                    // Swap the monsters
                    Collections.swap(list, attackerIndex, defenderIndex);
                    // Set the new monsters
                    turnInfoPackage.setMonsters(list.toArray(new Monster[0]));
                    // Create the message
                    String message = list.get(attackerIndex).getName() + ":"+ list.get(attackerIndex).getMonsterCode()
                            + " swapped out and " +  list.get(defenderIndex).getName() + ":" + list.get(defenderIndex).getMonsterCode() + " swapped in";
                    msgElement = TurnDisplayElementFactory.create("Message Header", message);
                    break;
                }
                msgElement = TurnDisplayElementFactory.createErrorEffectResolver(effect);
                // Use default nothing message
                break;
            case "DAMAGE":
                msgElement = doDamageAttack(effect, turnInfoPackage, attacker, defender);
                break;
            case "ALTER":
                msgElement = doAlterAttack(effect, turnInfoPackage, attacker, defender);
                break;
            case "STATE":
                msgElement = doStateAttack(effect, turnInfoPackage, attacker, defender);
                break;
            default:
//                turnInfoPackage.logger.info("Effect attackType not handled: " + effect.getAttackType()
//                        + "\nFull effect: " + effect);
                msgElement = TurnDisplayElementFactory.createErrorEffectResolver(effect);
                break;
        }
        return msgElement;
    }

    private static TurnDisplayElement doDamageAttack(Effect effect, TurnInfoPackage turnInfoPackage, String attacker, String defender) {
        TurnDisplayElement msgElement = TurnDisplayElementFactory.createErrorImplementation(effect);

        // Verify the effect code
        String effectResult = effect.getResultCode();
        if (effectResult == null|| effectResult.isEmpty()) {
            // Handle case when effectResult is null / empty
            return msgElement;
        }
        effectResult = effectResult.toUpperCase();

        // Get the monsters for the attack
        Monster attackerMon = turnInfoPackage.getMonsterBySlot(attacker);
        Monster defenderMon = turnInfoPackage.getMonsterBySlot(defender);

        // define some variables
        int type;
        int damage;

        // Handle the different cases
        switch (effectResult) {
            case "NONE":
                // Use the default Nothing msg
                break;
            case "PHYSICAL-DAMAGE":
                type = typeChart[convertTypeToIndex(attackerMon.getType())][convertTypeToIndex(
                        defenderMon.getType())];

                damage = defenderMon.getDamageByCalculation(turnInfoPackage, effect,
                        attacker, true, false, (Math.random() * 15 + 85) / 100, type);

                String message = defenderMon.getName() + ":" + defenderMon.getMonsterCode() + " took " + damage + " damage";
                msgElement = TurnDisplayElementFactory.create("Effect", message);

                defenderMon.doDamage(damage, turnInfoPackage);
                break;
            case "SPECIAL-DAMAGE":
                type = typeChart[convertTypeToIndex(attackerMon.getType())][convertTypeToIndex(defenderMon.getType())];

                damage = defenderMon.getDamageByCalculation(turnInfoPackage, effect,
                        attacker, false,
                        false, (Math.random() * 15 + 85) / 100, type);

                message = defenderMon.getName() + ":" + defenderMon.getMonsterCode() + " took " + damage + " damage";
                msgElement = TurnDisplayElementFactory.create("Effect", message);

                defenderMon.doDamage(damage, turnInfoPackage);
                break;
            case "NTH-DAMAGE":
            case "HEAL": //TODO Implement.
                // Begin Alt damage calc effects (currently Empty)
            default:
                msgElement = TurnDisplayElementFactory.createErrorImplementation(effect);
                break;
        }
        return msgElement;
    }

    private static TurnDisplayElement doAlterAttack(Effect effect, TurnInfoPackage turnInfoPackage, String attacker, String defender) {
        TurnDisplayElement msgElement = TurnDisplayElementFactory.createErrorImplementation(effect);

        // Verify the effect code
        String effectResult = effect.getResultCode();
        if (effectResult == null|| effectResult.isEmpty()) {
            // Handle case when effectResult is null / empty
            return msgElement;
        }
        effectResult = effectResult.toUpperCase();

        // Handle the different cases
        switch (effectResult) {
            case "NONE":
                // Use the default Nothing msg
                break;
            // Begin Weather effects
            case "CLEAR-WEATHER":
                clearWeather();
                /*
                 * Set weather in TIP
                 * Reset GMEs and EOT
                 */
                break;
            case "HARSH-SUNLIGHT":
                clearWeather();
                /*
                 * Reset GMEs and EOT
                 * GME: fire move +50% power
                 * GME: water move -50% power
                 * Set weather in TIP
                 */
                break;
            case "RAIN":
                clearWeather();
                /*
                 * Reset GMEs and EOT
                 * GME: fire move -50% power
                 * GME: water move +50% power
                 * Set weather in TIP
                 */
                break;
            case "SANDSTORM":
                clearWeather();
                /*
                 * Reset GMEs and EOT
                 * GME: rock mon +50% SP.D
                 * EOT: 1/16 dmg if not rock, ground, steel, or other reason ("check Sandstorm")
                 * Set weather in TIP
                 */
                break;
            case "SNOW":
                clearWeather();
                /*
                 * Reset GMEs and EOT
                 * GME: ice mon +50% DEF
                 * Set weather in TIP
                 */
                break;
            case "FOG":
                clearWeather();
                /*
                 * Reset GMEs and EOT
                 * GME: all move -40% acc
                 * Set weather in TIP
                 */
                break;
            // Begin Terrain effects
            case "CLEAR-TERRAIN":
                clearTerrain();
                /*
                 * Reset Terrains(GME and EOT) and battle timers
                 */
                break;
            case "ELECTRIC-TERRAIN":
                clearTerrain();
                /*
                 * Reset Terrains(GME and EOT) and battle timers
                 * GME: Elec move +30% pow
                 * Set terrain in TIP and set terrain timer
                 */
                break;
            case "GRASSY-TERRAIN":
                clearTerrain();
                /*
                 * Reset Terrains(GME and EOT) and battle timers
                 * GME: Grass move +30% pow
                 * EOT: 1/6 hp heal to all on ground
                 * GME: move -50% pow if move is "earth shaking" (Bulldoze, Earthquake,
                 * Magnitude, etc.)
                 * Set terrain in TIP and set terrain timer
                 */
                break;
            case "MISTY-TERRAIN":
                clearTerrain();
                /*
                 * Reset Terrains(GME and EOT) and battle timers
                 * GME: Drag move -50% pow if move has dmg
                 * Set terrain in TIP and set terrain timer
                 */
                break;
            case "PSYCHIC-TERRAIN":
                clearTerrain();
                /*
                 * Reset Terrains(GME and EOT) and battle timers
                 * GME: Psy move +30% pow
                 * GME: move miss if prio is above default(>0)
                 * Set terrain in TIP and set terrain timer
                 */
                break;
            default:
                msgElement = TurnDisplayElementFactory.createErrorImplementation(effect);
                break;
        }
        return msgElement;
    }

    public static void clearTerrain() {

    }

    private static void clearWeather() {

    }

    private static TurnDisplayElement doStateAttack(Effect effect, TurnInfoPackage turnInfoPackage, String attacker, String defender) {
        TurnDisplayElement msgElement = TurnDisplayElementFactory.createErrorImplementation(effect);

        // Verify the effect code
        String effectResult = effect.getResultCode();
        if (effectResult == null|| effectResult.isEmpty()) {
            // Handle case when effectResult is null / empty
            return msgElement;
        }
        effectResult = effectResult.toUpperCase();

        // Define some variables
        String message;
        int statValue;
        boolean alterStat = false;
        int statIndex = 0;
        Monster attackerMon = turnInfoPackage.getMonsterBySlot(attacker);
        Monster defenderMon = turnInfoPackage.getMonsterBySlot(defender);

        // Handle the different cases
        switch (effectResult) {
            case "NONE":
                // Use the default Nothing msg
                msgElement = TurnDisplayElementFactory.create("Error","Nothing Happened (State, None)");
                break;
            // Begin Status effects
            /*
             * In order deal with the exclusivity of status effects
             * will have to run a clear status effect before handling the
             */
            case "CLEAR-STATUS":
                // Clear all status effects
                clearStatusEffects(turnInfoPackage, defender);

                // Build the message
                message = "Cleared Status of " + defenderMon.getName() + ":" + defenderMon.getMonsterCode();
                msgElement = TurnDisplayElementFactory.create("Effect", message);

                // Set the status to clear
                defenderMon.setStatus("Clear Status");
                break;
            case "BURN":
                // Clear all status effects
                clearStatusEffects(turnInfoPackage, defender);

                // Handle the cases where the status fizzles
                if (Objects.equals(turnInfoPackage.getTerrain(), "Misty Terrain")) {
                    message = "Tried to Burn " + defenderMon.getName() + " but," +
                            "it fizzled due to misty terrain";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                }
                // Set the status to Burn
                defenderMon.setStatus("Burn");
                // Results Handled in active tick of Monster
                break;
            case "FREEZE":
                // Clear all status effects
                clearStatusEffects(turnInfoPackage, defender);

                // Handle the cases where the status fizzles
                if (Objects.equals(turnInfoPackage.getWeather(), "Harsh Sunlight")) {
                    message = "Tried to Freeze " + defenderMon.getName() + " but," +
                            "was thawed out by the sun";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                } else if (Objects.equals(turnInfoPackage.getTerrain(), "Misty Terrain")) {
                    message = "Tried to Freeze " + defenderMon.getName() + " but," +
                            "it fizzled due to misty terrain";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                }

                // Set the status to Freeze
                defenderMon.setStatus("Freeze");

                // Build the message
                message = "Froze " + defenderMon.getName() + ":" + defenderMon.getMonsterCode();
                msgElement = TurnDisplayElementFactory.create("Effect", message);

                break;
            case "PARALYSIS":
                // Clear all status effects
                clearStatusEffects(turnInfoPackage, defender);

                // Handle the cases where the status fizzles
                if (Objects.equals(turnInfoPackage.getTerrain(), "Misty Terrain")) {
                    message = "Tried to Paralyze " + defenderMon.getName() + " but," +
                            "it fizzled due to misty terrain";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                }
                // Set the status to Paralysis
                defenderMon.setStatus("Paralysis");

                // Build the message
                message = "Paralyzed " + defenderMon.getName() + ":" + defenderMon.getMonsterCode();
                msgElement = TurnDisplayElementFactory.create("Effect", message);

                //TODO Add Global Move Effect to randomly null attacking move of defender using
                break;
            case "POISON":
                // Clear all status effects
                clearStatusEffects(turnInfoPackage, defender);

                // Handle the cases where the status fizzles
                if (Objects.equals(turnInfoPackage.getTerrain(), "Misty Terrain")) {
                    message = "Tried to Poison " + defenderMon.getName() + " but," +
                            "it fizzled due to misty terrain";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                }
                // Set the status to Poison
                defenderMon.setStatus("Poison");

                // Build the message
                message = "Poisoned " + defenderMon.getName() + ":" + defenderMon.getMonsterCode();
                msgElement = TurnDisplayElementFactory.create("Effect", message);
                // Results Handled in active tick of Monster
                break;
            case "BADLY-POISON":
                // Clear all status effects
                clearStatusEffects(turnInfoPackage, defender);

                // Handle the cases where the status fizzles
                if (Objects.equals(turnInfoPackage.getTerrain(), "Misty Terrain")) {
                    message = "Tried to Badly Poison " + defenderMon.getName() + " but," +
                            "it fizzled due to misty terrain";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                }

                // Set the status to Badly Poison
                defenderMon.setStatus("Badly Poison");

                // Build the message
                message = "Badly Poisoned " + defenderMon.getName() + ":" + defenderMon.getMonsterCode();
                msgElement = TurnDisplayElementFactory.create("Effect", message);
                // Results Handled in active tick of Monster
                break;
            case "SLEEP":
                // Clear all status effects
                clearStatusEffects(turnInfoPackage, defender);

                // Handle the cases where the status fizzles
                if (Objects.equals(turnInfoPackage.getTerrain(), "Electric")) {
                    message = "Tried to make " + defenderMon.getName() + " fall asleep but," +
                            "it fizzled due to electric terrain";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                } else if (Objects.equals(turnInfoPackage.getTerrain(), "Misty Terrain")) {
                    message = "Tried to make " + defenderMon.getName() + " fall asleep but," +
                            "it fizzled due to misty terrain";
                    msgElement = TurnDisplayElementFactory.create("Effect", message);
                    break;
                }
                // Add Global Move Effect to sleep with a timer using monster code
                break;
            // Begin stat effects
            // Code is the same for all stat effects, so we will handle them all-in-one at the end
            case "ALTER-STAT-HP":
                alterStat = true;
//                statIndex = 0;
                break;
            case "ALTER-STAT-ATK":
                alterStat = true;
                statIndex = 1;
                break;
            case "ALTER-STAT-DEF":
                alterStat = true;
                statIndex = 2;
                break;
            case "ALTER-STAT-SP.A":
                alterStat = true;
                statIndex = 3;
                break;
            case "ALTER-STAT-SP.D":
                alterStat = true;
                statIndex = 4;
                break;
            case "ALTER-STAT-SPEED":
                alterStat = true;
                statIndex = 5;
                break;
            default:
                msgElement = TurnDisplayElementFactory.createErrorImplementation(effect);
                break;
        }
        if(alterStat){
            statValue = Integer.parseInt(effect.getEffectValue());
            msgElement = setMsg_StatChange(turnInfoPackage, statValue, statIndex, defender, attacker);
            defenderMon.effectStat(statIndex, statValue);
        }

        return msgElement;
    }

    private static void clearStatusEffects(TurnInfoPackage turnInfoPackage, String defender) {

    }

    private static TurnDisplayElement setMsg_StatChange(TurnInfoPackage turnInfoPackage, int statValue, int index, String defender, String attacker) {
        TurnDisplayElement msgElement;
        String amount ="";
        if (statValue == 0) {
            // if the change is nothing
            return TurnDisplayElementFactory.create("Error","Stat was not changed as the value was 0");
        } else if (statValue >1){
            amount =  "Sharply";
        }
        String stat = "";
        switch (index) {
            case 0:
                stat = "Hp";
                break;
            case 1:
                stat = "Atk";
                break;
            case 2:
                stat = "Def";
                break;
            case 3:
                stat = "Sp.A";
                break;
            case 4:
                stat = "Sp.D";
                break;
            case 5:
                stat = "Speed";
                break;
            default:
                break;
        }

        boolean increase = statValue > 0;
        String message;
        Monster defenderMon = turnInfoPackage.getMonsterBySlot(defender);
        if (increase) { // The case of the stat not changing is handled at the beginning
            message = defenderMon.getName() + ":" + defenderMon.getMonsterCode() + " has its " + stat + "raised " + amount;
            msgElement = TurnDisplayElementFactory.create("Effect", message);
        } else {
            message = defenderMon.getName() + ":" + defenderMon.getMonsterCode() + " has its " + stat + "lowered " + amount;
            msgElement = TurnDisplayElementFactory.create("Effect", message);
        }

        // TO DO add the case of the stat not changing
        return msgElement;

    }

    private static Boolean doesEffectTrigger(Effect effect, TurnInfoPackage turnInfoPackage) {
        String trigger = effect.getTrigger();
        if(trigger == null){
            // Handle case when trigger is null
            return false;
        }
        // Check other cases
        switch (trigger) {
            case "Always":
            case "True":
                return true;
            case "Never":
            default:
                return false;
        }
    }

    private static final int[][] typeChart = {
            // Normal Fire Water Electric Grass Ice Fighting Poison Ground Flying Psych Bug Rock Ghost Dragon Dark Steel Fairy
            { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 1, 2 }, // Normal
            { 2, 1, 1, 2, 4, 4, 2, 2, 2, 2, 2, 4, 1, 2, 1, 2, 4, 2 }, // Fire
            { 2, 4, 1, 2, 1, 2, 2, 2, 4, 2, 2, 2, 4, 2, 1, 2, 2, 2 }, // Water
            { 2, 2, 4, 1, 1, 2, 2, 2, 0, 4, 2, 2, 2, 2, 1, 2, 2, 2 }, // Electric
            { 2, 1, 4, 2, 1, 2, 2, 1, 4, 1, 2, 1, 4, 2, 1, 2, 1, 2 }, // Grass
            { 2, 1, 1, 2, 4, 1, 2, 2, 4, 4, 2, 2, 2, 2, 4, 2, 1, 2 }, // Ice
            { 4, 2, 2, 2, 2, 4, 2, 1, 2, 1, 1, 1, 4, 0, 2, 4, 4, 1 }, // Fighting
            { 2, 2, 2, 2, 4, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 2, 0, 4 }, // Poison
            { 2, 4, 2, 4, 1, 2, 2, 4, 2, 0, 2, 1, 4, 2, 2, 2, 4, 2 }, // Ground
            { 2, 2, 2, 1, 4, 2, 4, 2, 2, 2, 2, 4, 1, 2, 2, 2, 1, 2 }, // Flying
            { 2, 2, 2, 2, 2, 2, 4, 4, 2, 2, 1, 2, 2, 2, 2, 0, 1, 2 }, // Psychic
            { 2, 1, 2, 2, 4, 2, 1, 1, 2, 1, 4, 2, 2, 1, 2, 4, 1, 1 }, // Bug
            { 2, 4, 2, 2, 2, 4, 1, 2, 1, 4, 2, 4, 2, 2, 1, 2, 1, 2 }, // Rock
            { 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 2, 2, 4, 2, 1, 2, 2 }, // Ghost
            { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 2, 1, 0 }, // Dragon
            { 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 4, 2, 2, 4, 2, 1, 2, 1 }, // Dark
            { 2, 1, 1, 1, 2, 4, 2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 1, 4 }, // Steel
            { 2, 1, 2, 2, 2, 2, 4, 1, 2, 2, 2, 2, 2, 2, 4, 4, 1, 2 } // Fairy
    };

    private static int convertTypeToIndex(String typestr) {
        int i;
        typestr = typestr.toLowerCase();
        i = switch (typestr) {
            case "fire" -> 1;
            case "water" -> 2;
            case "electric" -> 3;
            case "grass" -> 4;
            case "ice" -> 5;
            case "fighting" -> 6;
            case "poison" -> 7;
            case "ground" -> 8;
            case "flying" -> 9;
            case "psychic" -> 10;
            case "bug" -> 11;
            case "rock" -> 12;
            case "ghost" -> 13;
            case "dragon" -> 14;
            case "dark" -> 15;
            case "steel" -> 16;
            case "fairy" -> 17;
            default -> 0;
        };
        return i;
    }

}
