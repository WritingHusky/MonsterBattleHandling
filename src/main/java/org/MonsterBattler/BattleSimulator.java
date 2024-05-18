package org.MonsterBattler;
/*
 * This is where the execution of the turn occurs
 */

import java.util.Queue;
import java.util.Random;
public class BattleSimulator {

    /**
     * This is the main logic of the execution of the turn.<br>
     * </br>
     * This runs after moves have been chosen and ordered.
     * 
     * @throws DeadMonsterThrowable When a monster dies. Thrown to stop the turn to
     *                              ask for the player for a monster or end the
     *                              battle.
     *                              The moveQueue may not be empty so this method
     *                              can be called again to resume the turn. Then the
     *                              turnInfoPackage will be cleaned
     *                              and the turn is over.
     *                              <br>
     *                              </br>
     *                              The turnDisplay info then can be read to the
     *                              players before the choice, so they know what is
     *                              happening.
     *                              Thus Clearing it for the rest of the turn
     */
    public static TurnInfoPackage executeTurn(TurnInfoPackage turnInfoPackage) throws DeadMonsterThrowable {

        // Might have to check for swaps here

        while (!turnInfoPackage.getMoveQueue().isEmpty()) {
            // Pop element of turn Q as MoveEffect
            MoveEffect currentMove = turnInfoPackage.getMoveQueue().poll();
            // System.out.println("Simulating Move: " + currentMove.toString());

            // Get the needed info to do the attack
            assert currentMove != null;
            String source = currentMove.getSource();
            String target = currentMove.getTarget();

            // This section of code might move / split for the sake of pre-move effects // For now this should work
            // If the source is dead and the move is not swap then skip the move
            if (turnInfoPackage.getMonsterBySlot(source).isDead() && !currentMove.getMoveName().equals("Swap")) {
                continue;
            }
            // If the target is dead then skip the move and go to the next one
            if (turnInfoPackage.getMonsterBySlot(target).isDead()) {
                continue;
            }


            // Go through any pre-move effects stored in the TurnInfoPackage
            MonsterTurnInfo sourceTurnInfo = turnInfoPackage.getMonsterTurnInfoBySlot(source);
            Queue<MoveEffect> sourcePremoveEffects = sourceTurnInfo.getPreMoveEffects();
            for (MoveEffect move : sourcePremoveEffects) {
                for (Effect moveEffect : move.getMoveEffects()) {
                    doEffect(moveEffect, turnInfoPackage, source, target);
                }
            }

            MonsterTurnInfo targetTurnInfo = turnInfoPackage.getMonsterTurnInfoBySlot(target);
            Queue<MoveEffect> targetPremoveEffects = targetTurnInfo.getPreMoveEffects();
            for (MoveEffect move : targetPremoveEffects) {
                for (Effect moveEffect : move.getMoveEffects()) {
                    doEffect(moveEffect, turnInfoPackage, source, target);
                }
            }
            // ^ Might Become GME

            // Accuracy Check before the main Effects
            Random rand = new Random();
            // If it fails the move ends
            if (currentMove.getAccuracy() < rand.nextInt(0, 100)) {
                // Add the miss to the display
                Monster sourceMonster = turnInfoPackage.getMonsterBySlot(source);

                // Build the message
                String message = sourceMonster.getName() + ":" + sourceMonster.getMonsterCode() + " used " + currentMove.getMoveName();
                turnInfoPackage.getTurnDisplayList()
                        .addMSGToList(TurnDisplayElementFactory.create("Message Header", message));
                break;
            } else { // Technically this else is not needed, but it's here for clarity
                // Add the Hit to the display

                // Handle the case where the move is swap (when swap don't add hit to display
                if(!currentMove.getMoveName().equals("Swap")){
                    Monster sourceMonster = turnInfoPackage.getMonsterBySlot(source);
                    Monster targetMonster = turnInfoPackage.getMonsterBySlot(target);

                    // Build the message
                    String message = sourceMonster.getName() + ":" + sourceMonster.getMonsterCode() + " used " + currentMove.getMoveName() +
                            " on " + targetMonster.getName() + ":" + targetMonster.getMonsterCode();
                    turnInfoPackage.getTurnDisplayList()
                            .addMSGToList(TurnDisplayElementFactory.create("Message Header", message));
                }
            }

            // Get the effects of the attack
            Queue<Effect> moveIT = currentMove.getMoveEffects();
            // Go through the effects
            for (Effect effect : moveIT) {
                // For each effect in effects
                // Do the effect
                doEffect(effect, turnInfoPackage, source, target);

                // Allow the defender to respond
                Monster monTarget = turnInfoPackage.getMonsterBySlot(target);
                if (monTarget.doesAbilityTrigger(effect.getResultCode())) {
                    for (Effect abilityEffect : monTarget.getAbility().getMoveEffects()) {
                        doEffect(abilityEffect, turnInfoPackage, source, target);
                    }
                }
                // The Source does not get to respond to the response

                // Check if the responses caused this move to end
                if (turnInfoPackage.isMoveOverriddenBySlot(target) > 0) {
                    // Add override msg to displayMsg
                    break;
                }
            } // Go on to the next effect

            // End of move effects stored in the turnInfoPackage
            // End of move effects for the source
            sourceTurnInfo = turnInfoPackage.getMonsterTurnInfoBySlot(source);
            Queue<MoveEffect> sourceEndmoveEffects = sourceTurnInfo.getEndMoveEffects();
            for (MoveEffect move : sourceEndmoveEffects) {
                for (Effect moveEffect : move.getMoveEffects()) {
                    doEffect(moveEffect, turnInfoPackage, source, target);
                }

            }

            // End of move effects for the target
            targetTurnInfo = turnInfoPackage.getMonsterTurnInfoBySlot(target);
            Queue<MoveEffect> targetEndmoveEffects = targetTurnInfo.getEndMoveEffects();
            for (MoveEffect move : targetEndmoveEffects) {
                for (Effect moveEffect : move.getMoveEffects()) {
                    doEffect(moveEffect, turnInfoPackage, source, target);
                }

            }
            // ^ Might Become EOT effects

            // End Of attack clean Up
            /*
             * List of things to clean up
             * - Tick over timers
             * - Clear empty timers
             */
            // Tick the status of monsters
            turnInfoPackage.getMonsterBySlot(source).tickStatusActive(turnInfoPackage);
            turnInfoPackage.getMonsterBySlot(target).tickStatusPassive(turnInfoPackage);

            // Tick timers of the source and do all the effects
            for (BattleTimer timer : turnInfoPackage.getTimers(source)) {
                timer.tick();
                doEffect(timer.getEffect(), turnInfoPackage, source, target);
            }
            turnInfoPackage.purgeTimers(source);

            boolean deathFlag = turnInfoPackage.getMonsterBySlot(target).isDead();

            // If the move was swap then skip the death check for the target as they will be dead if they swapped out
            if(currentMove.getMoveName().equals("Swap")){
                continue;
            }
            // If a monster is dead at this point then they were killed by a move
            // It is the responsibility of the builder to check if the game is over now // This could be changed to be here
                // but that would make the code here more complex

            // If the target is dead try to switch out
            if (turnInfoPackage.getMonsterBySlot(target).isDead()) {

                Monster targetMonster = turnInfoPackage.getMonsterBySlot(target);

                // Build the message
                String message = targetMonster.getName() + ":" + targetMonster.getMonsterCode() + " has Died";
                turnInfoPackage.getTurnDisplayList().getDisplayList().add(TurnDisplayElementFactory.create("Message Header", message));
//                System.out.println("Monster Died: "+ turnInfoPackage.getMonsterBySlot(target).getMonsterCode() + " (target-post-timer)");

                // Clean Up the display list
                turnInfoPackage.cleanTurnDisplayList();

                // Throw an error to stop the turn midway
                throw new DeadMonsterThrowable(turnInfoPackage.getMonsterBySlot(target).getSlot()); // Uses the slot code
            }
            if (deathFlag && turnInfoPackage.getMonsterBySlot(source).isDead()){

                Monster sourceMonster = turnInfoPackage.getMonsterBySlot(source);

                // Build the message
                String message = sourceMonster.getName() + ":" + sourceMonster.getMonsterCode() + " has Died";
                turnInfoPackage.getTurnDisplayList().getDisplayList().add(TurnDisplayElementFactory.create("Message Header", message));
//                System.out.println("Monster Died: "+ turnInfoPackage.getMonsterBySlot(source).getMonsterCode() + " (source-post-timer)");

                // Clean Up the display list
                turnInfoPackage.cleanTurnDisplayList();

                // Throw an error to stop the turn midway
                throw new DeadMonsterThrowable(turnInfoPackage.getMonsterBySlot(source).getSlot()); // Uses the slot code
            }

        } // Move Queue loop point

        // End of Turn
        /*
         * Clean Up the TurnInfoPackage
         * Recalculate all relevant stats (will so be done at time of activation)
         * Clean up turnDisplayList? remove nones
         */
        // Clean up the turnDisplayList
        turnInfoPackage.cleanUpPackage();

        // Return the TurnInfoPackage
        return turnInfoPackage;
    }

    /**
     * The method that will call another
     * 
     * @param effect The effect to be handled
     * @param turnInfoPackage All the info to use
     */
    public static void doEffect(Effect effect, TurnInfoPackage turnInfoPackage, String sourceCode, String targetCode) {
        EffectResolver.doEffect(effect, turnInfoPackage, sourceCode, targetCode);
    }

}

