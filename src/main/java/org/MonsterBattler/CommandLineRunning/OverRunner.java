package org.MonsterBattler.CommandLineRunning;

import org.MonsterBattler.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;

/**
 * This is main file that handles the
 */
public class OverRunner {
    static Scanner scanner;

    public static void main(String[] args) {

        // Get the rule set
        /* Using default currently (6vs6) */
        final int teamCount = 2;
        final int monCountTotal = 4;
        final int activeMon = 1;
        Logger logger = Logger.getLogger("BattlerLogger");
        try {
            FileHandler fileHandler = new FileHandler("battlerLogger.log");
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
            // Register a shutdown hook to close the FileHandler

        } catch (SecurityException | IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        // Load everything ready to begin battle
        TurnInfoPackage turnInfoPackage = new TurnInfoPackage(monCountTotal, teamCount, activeMon);
        final int monInTeam = monCountTotal / teamCount;
        DataBaseFrontend dataBaseFrontend = new DataBaseFrontend();
        scanner = new Scanner(System.in);

        MoveQueueBuilder moveQueueBuilder = new MoveQueueBuilder();

        // Load up all the monsters
        // Copy the monster array for sizing
        Monster[] monsters = new Monster[monCountTotal];

        for (int j = 0; j < teamCount; j++) {
            // Load up / get team data
            int[] teamDexIdArr = getTeamDexId(monInTeam, j);

            // Build the teams
            for (int i = 0; i < monInTeam; i++) {
                // Get the database information to work with
                monsters[(j * monInTeam) + i] = dataBaseFrontend.pullMonster(teamDexIdArr[i], (j * monInTeam) + i);
                // Set the monster slots
            }
        }
        // Set the Monster Array and assign slots
        turnInfoPackage.setMonsters(monsters);

        // Begin battle loop
//        turnInfoPackage.logger.fine("Battle Beginning");
        while (true) {
            if (runBattle(turnInfoPackage, moveQueueBuilder))
                break;
        } // Turn Loop point

        SOP("~~Battle has ended~~");
        scanner.close();
//        for (Handler handler : turnInfoPackage.logger.getHandlers()) {
//            handler.close();
//        }
    }

    private static boolean runBattle(TurnInfoPackage turnInfoPackage, MoveQueueBuilder moveQueueBuilder) {
//        turnInfoPackage.logger.finest("Turn:" + turnInfoPackage.getTurnCount());
        SOP("~~~~Turn:" + turnInfoPackage.getTurnCount() + "~~~~");

        SOP("Battlefield as it stands");
        for (Monster mon : turnInfoPackage.getMonsters()) {
            displayMonsterData(mon);
            SOP("Slot:" + mon.getSlot());
        }
        SOP("\n");

        int activeMon = turnInfoPackage.getActiveMon();
        // int monCountTotal = turnInfoPackage.getMonCountTotal();
        int teamCount = turnInfoPackage.getTeamCount();
        int monInTeam = turnInfoPackage.getMonInTeam();
        // Get move choices of all teams
        MoveEffect[] moves = new MoveEffect[teamCount * activeMon];
//        turnInfoPackage.logger.fine("Building Move Array");
        for (int i = 0; i < teamCount; i++) {
            for (int j = 0; j < activeMon; j++) {
                int index2 = i * activeMon + j;
                moves[index2] = getMoveChoice(turnInfoPackage.getMonsters()[i * monInTeam + j], turnInfoPackage);
//                turnInfoPackage.logger.finest("Move: " + moves[index2]);
            }
        }
        // Fill MoveQueue with the moves
//        turnInfoPackage.logger.fine("Building MoveQ with move array" + Arrays.toString(moves));
        for (MoveEffect move : moves) {
//            turnInfoPackage.logger.finest("Adding move to Q: " + move.toString());
            moveQueueBuilder.pushMove(move, turnInfoPackage);
        }
        turnInfoPackage.setMoveQueue(moveQueueBuilder.getMoveQueue());
        moveQueueBuilder.setMoveQueue(new LinkedList<>());
        SOP("\n\n~~~~\nRunning Turn\n~~~\n\n");
//        turnInfoPackage.logger.fine("Running with move Q:" + turnInfoPackage.getMoveQueue().toString());
        // Run a turn of battle
        if (runTurn(turnInfoPackage, moveQueueBuilder)) {
            // If the battle ends
            displayTurn(turnInfoPackage.getTurnDisplayList());
            return true;
        }

        // Handle the turn display list
        displayTurn(turnInfoPackage.getTurnDisplayList());
        turnInfoPackage.setTurnDisplayList(new TurnDisplayList());
        return false;
    }

    /**
     * Run battle is separate method to allow for recursion for monster death
     */
    private static boolean runTurn(TurnInfoPackage turnInfoPackage, MoveQueueBuilder moveQueueBuilder) {
        try {
            if (checkBattle(turnInfoPackage))
                return true;
            BattleSimulator.executeTurn(turnInfoPackage);
            return checkBattle(turnInfoPackage);
        } catch (DeadMonsterThrowable deadMon) {

            // Build up a new move Queue as to continue the turn after swaps
            Queue<MoveEffect> oldQueue = turnInfoPackage.getMoveQueue();
            for (MoveEffect oldEffect : oldQueue) {
                moveQueueBuilder.pushMove(oldEffect, turnInfoPackage);
            }

            if (checkBattle(turnInfoPackage))
                return true;

            // Display the turn info before getting the monster
            displayTurn(turnInfoPackage.getTurnDisplayList());

            // iterate over all the active monsters
            for (int i = 0; i < turnInfoPackage.getTeamCount(); i++) {
                for (int j = 0; j < turnInfoPackage.getActiveMon(); j++) {
                    // Check if the active monster is dead
                    int index = i * turnInfoPackage.getTeamCount() + j;
                    Monster currentMon = turnInfoPackage.getMonsters()[index];
                    if (currentMon.isDead()) {
                        // If the monster is dead get the swapped move
                        MoveEffect swap;
                        // SOP(currentMon.getName() + " is dead");
                        do {
                            swap = getMonsterSwapChoice(currentMon.getSlot(), turnInfoPackage);
                        } while (Objects.equals(swap.getTarget(), currentMon.getSlot()));
                        moveQueueBuilder.pushMove(swap, turnInfoPackage);
                    }
                }
            }
            // Set the move Queue
            turnInfoPackage.setMoveQueue(moveQueueBuilder.getMoveQueue());
            moveQueueBuilder.setMoveQueue(new LinkedList<>());
            // Run the turn
            runTurn(turnInfoPackage, moveQueueBuilder);
            return false;
        }
    }

    private static boolean checkBattle(TurnInfoPackage turnInfoPackage) {
        // Check for if the game is over
        for (int i = 0; i < turnInfoPackage.getTeamCount(); i++) {
            int index = i * turnInfoPackage.getMonInTeam();
            boolean teamDead = turnInfoPackage.isTeamDead(turnInfoPackage.convertIntToSlot(index));
            if (teamDead) {
                return true;
            }
        }
        return false;
    }

    private static void displayTurn(TurnDisplayList turnDisplayList) {
        SOP("Displaying turn info");
        int count = 0;
        for (TurnDisplayElement element : turnDisplayList.getDisplayQ()) {
            SOP("display element: " + count);
            SOP(element.getActivationMsg());
            SOP(element.getResultMsg());
            count++;

        }
    }

    private static MoveEffect getMoveChoice(Monster monster, TurnInfoPackage turnInfoPackage) {
        SOP("Please choose a move for " + monster.getName() + " to use.");
        displayMonsterData(monster);

        scanner.reset();
        int result;

        while (true) {
            // Display the available moves (swap included)
            for (int i = 0; i < monster.getMoves().length; i++) {
                MoveEffect move = monster.getMoves()[i];
                SOP(move.getMoveName() + ":\n\tAccuracy: " + move.getAccuracy() + "\n\tPower: " + move.getPower());
                SOP("Choice: " + i);
                SOP("");
            }

            // Check if the next input is an integer
            if (scanner.hasNextInt()) {
                result = scanner.nextInt();
                if (result >= 0 && result < 5) {
                    break;
                } else {
                    SOP("!!!!Invalid choice, please choose again!!!!\n");
                }
            } else {
                // Consume the non-integer input to avoid an infinite loop
                scanner.next();
                SOP("!!!!Invalid choice, please enter a number!!!!\n");
            }
        }

        SOP("Chosen Move " + result);

        MoveEffect move = monster.getMoves()[result];

        // If Swap is chosen, get the swap choice
        if (move.getMoveName().equals("Swap")) {
            move = getMonsterSwapChoice(monster.getSlot(), turnInfoPackage);
            return move;
        }

        // Else get the target of the attack
        move.setSource(monster.getSlot());

        int slotResult = -1;
        boolean repeat;

        // Clear the scanner because it might have leftover characters
        scanner.nextLine();
        int currentTeam = turnInfoPackage.convertSlotToIndex(monster.getSlot()) / turnInfoPackage.getMonInTeam();
        // get the target
        do {
            for (int team = 0; team < turnInfoPackage.getTeamCount(); team++) {
                // SOP(team+"");
                if (team == currentTeam) {
                    continue;
                }
                for (int i = 0; i < turnInfoPackage.getActiveMon(); i++) {
                    int index = i + team * turnInfoPackage.getMonInTeam();
                    Monster targetMonster = turnInfoPackage.getMonsters()[index];
                    displayMonsterData(targetMonster);
                    SOP("Slot: " + index);
                }
            }

            // Check if the next input is an integer
            if (scanner.hasNextInt()) {
                slotResult = scanner.nextInt();

                if (slotResult == -1) {
                    repeat = true;
                } else if (slotResult >= turnInfoPackage.getMonCountTotal()) {
                    repeat = true;
                } else {
                    repeat = false;
                }
            } else {
                // Consume the non-integer input to avoid an infinite loop
                scanner.next();
                repeat = true;
            }
        } while (repeat);
        SOP("Target: " + slotResult + "\n");
        move.setTarget(turnInfoPackage.convertIntToSlot(slotResult));

        return move;
    }

    private static MoveEffect getMonsterSwapChoice(String sourceSlot, TurnInfoPackage turnInfoPackage) {
        // Display info on all the monsters
        Monster[] team = turnInfoPackage.getTeamBySlot(sourceSlot);
        // Get Choice
        int result = -1;
        if (scanner == null)
            scanner = new Scanner(System.in);

        do {
            SOP("Please choice a monster to switch in");
            for (int i = 0; i < team.length; i++) {
                displayMonsterData(team[i]);
                SOP("Choice: " + i);
            }
            result = scanner.nextInt();
            // Validate choice (owned monster, alive)
            if (result >= 0 && result < team.length) {
                // SOP(result + " is in range");
                if (!team[result].isDead()) {
                    // SOP(" and is not dead");
                    break;
                }
            }
        } while (true);

        // Add the swap move to the moveQ (High priority)
        // SOP("Building swap move");

        return buildSwapEffect(sourceSlot, team[result]);
    }

    private static MoveEffect buildSwapEffect(String sourceSlot, Monster team) {
        MoveEffect swap = new MoveEffect();
        swap.setAccuracy(100);
        swap.setMoveName("Swap");
        swap.setPower(0);
        swap.setPriority(6);
        swap.setSource(sourceSlot);
        swap.setTarget(team.getSlot());
        Effect swapEffect = new Effect("None", "Swap", "", "Always", "Error in Swap","Normal");
        Queue<Effect> swapEffects = new LinkedList<>();
        swapEffects.add(swapEffect);
        swap.setMoveEffects(swapEffects);
        return swap;
    }

    private static void displayMonsterData(Monster monster) {
        SOP("Name: " + monster.getName());
        SOP("HP: " + monster.getCurrentHp() + "/" + monster.getMaxHp());
        SOP("Status: " + monster.getStatus());
    }

    private static int[] getTeamDexId(int monInTeam, int team) {
        return new int[monInTeam];
    }

    private static void SOP(String s) {
        System.out.println(s);
    }
}
