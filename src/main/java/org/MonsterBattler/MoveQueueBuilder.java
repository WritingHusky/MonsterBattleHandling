package org.MonsterBattler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * This is the class that will intake the chosen moves and create the moveQueue.
 * <br>
 * </br>
 * This class will also set up any pre/end move effects that may result from move
 * choice?
 * <br>
 * </br>
 * This class could be merged into the Battler simulator
 */
public class MoveQueueBuilder {
    /**
     * For modularity this queue will build the order of moves each time a move is
     * pushed
     */
    public Queue<MoveEffect> moveQueue;
    // private int count = 0;
    Random rand = new Random();

    public MoveQueueBuilder() {
        this.moveQueue = new LinkedList<>();
    }

    /**
     * Need to check that the queue is built correctly (will be obvious in testing)
     * 
     * @param move The Move to push to Queue
     * @param turnInfoPackage The Info About the turn (used for speed)
     */
    public void pushMove(MoveEffect move, TurnInfoPackage turnInfoPackage) {

//        turnInfoPackage.logger.finest("Adding Move: " + move.toString() + "\nTo queue: " + this.moveQueue.toString());
        // Array sorted from highest to lowest
        LinkedList<MoveEffect> ll = new LinkedList<>(this.moveQueue);

        int index = 0; // might have to change to 0 for default (maybe ll.size() to default to the end of the ll )
        // Find where to put the move
        for (int j = 0; j < this.moveQueue.size(); j++) {
            MoveEffect currentMove = ll.get(j);

            // Pass over when slower
            if (currentMove.getPriority() <= move.getPriority()) {
                if (currentMove.getPriority() < move.getPriority()) {
                    // Set index when faster
                    index = j;
                    break;
                } else if (currentMove.getPriority() == move.getPriority()) {
                    // If tie check monster speed stats
                    Monster sourceMon = turnInfoPackage.getMonsterBySlot(move.getSource());
                    Monster targetMon = turnInfoPackage.getMonsterBySlot(currentMove.getSource());

                    if (sourceMon.getStats()[5] > targetMon.getStats()[5]) {
                        index = j;
                        break;
                    } else if (sourceMon.getStats()[5] < targetMon.getStats()[5]) {
                        index = j + 1;
                        break;
                    } else {
                        // If Tie make random choice
                        index = j;
                        if (rand.nextInt(1, 3) != 1) {
                            // If lose
                            index += 1;
                            break;
                        } // If Win nothing happens
                    }
                }
            }
        }
        try {
            ll.add(index, move);
        } catch (Exception e) {
//            turnInfoPackage.logger
//                    .finest("Error in building the moveQ: " + ll + "\nException: " + e.getMessage());
        }

        // Rebuild the queue
        this.moveQueue = ll;
//        turnInfoPackage.logger.finest("Finished Building Q: " + this.moveQueue);
    }

    public Queue<MoveEffect> getMoveQueue() {
        return moveQueue;
    }

    public void setMoveQueue(Queue<MoveEffect> moveQueue) {
        this.moveQueue = moveQueue;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

}
