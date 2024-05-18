package org.MonsterBattler;

import com.google.gson.Gson;

import java.util.*;

public class TurnInfoPackage {
    public int monCountTotal;
    public int teamCount;
    public int monInTeam;
    public int activeMon;

    public int turnCount;

    /**
     * Swapping monsters swaps their position in the array
     */
    public Monster[] monsters;
    public String[] monSlots;
    public MonsterTurnInfo[] monsterTurnInfoArray;
    public TurnDisplayList turnDisplayList;
    public DeadMonsterThrowable deadMonsterThrowable;
    public Queue<MoveEffect> moveQueue;
    public Queue<Effect> nextTurnEffects;

    /**
     * Key = Slot<br>
     * </br>
     * Value = Timer
     */
    public HashMap<String, LinkedList<BattleTimer>> battleTimers;

    /**
     * An array of if a move is overridden and why<br>
     * </br>
     * <br>
     * 0. not overridden</br>
     * <br>
     * 1. Missed (thrown from accuracy effect)</br>
     */
    public int[] moveOverrides;

//    transient Logger logger;
//    FileHandler fileHandler;

    public String weather;
    public String terrain;

    public enum State {
        New,
        Ready,
        Paused,
        Resume,
        Complete,
        Waiting,
        Simulating,
        End

    }
    public State state;

    public TurnInfoPackage() {
        // This constructor is intentionally empty. Nothing special is needed here.
        this.monSlots = new String[0];
    }

    public TurnInfoPackage(int monCountTotal, int teamCount, int activeMon) {

//        this.logger = logger;

        this.monCountTotal = monCountTotal;
        this.teamCount = teamCount;
        this.monInTeam = monCountTotal / teamCount;
        this.activeMon = activeMon;

        assert (this.monCountTotal == teamCount * this.monInTeam); // Team count rounding error

        // Instance all the variables
        this.monsters = new Monster[this.monCountTotal];
        this.monSlots = new String[this.monCountTotal];
        this.monsterTurnInfoArray = new MonsterTurnInfo[this.monCountTotal];
        this.turnDisplayList = new TurnDisplayList();
        this.deadMonsterThrowable = new DeadMonsterThrowable("");
        this.battleTimers = new HashMap<>();
        this.turnCount = 0;
        this.moveQueue = new LinkedList<>();
        this.nextTurnEffects = new LinkedList<>();
        this.moveOverrides = new int[this.monCountTotal]; // Defaults to all zeros
        this.weather = "Clear Weather";
        this.terrain = "Clear Terrain";

        // Fill out the monster slots array
        this.fillMonSlots();

        // Fill out the monsterTurnInfo array
        for (int i = 0; i < this.monsterTurnInfoArray.length; i++) {
            this.monsterTurnInfoArray[i] = new MonsterTurnInfo();
        }
        this.state = State.New;
    }
    public void fillMonSlots(){
        if(this.monCountTotal == 0) {
            return;
        }

        this.monSlots = new String[this.monCountTotal];
        for (int i = 0; i < this.monCountTotal; i++) {
            this.monSlots[i] = convertIntToSlot(i);
        }
    }

    public void cleanUpPackage() {
        this.deadMonsterThrowable = new DeadMonsterThrowable("");
        cleanTurnDisplayList();
        this.turnCount++;
        this.deadMonsterThrowable = new DeadMonsterThrowable("");
    }

    public void cleanTurnDisplayList() {
        // Copy the current display list
        ArrayList<TurnDisplayElement> newList = new ArrayList<>();

        // Only add non-error messages to the new list
        for (TurnDisplayElement displayElement : this.turnDisplayList.getDisplayList()) {
            if (!Objects.equals(displayElement.messageType, "Error")) {
                newList.add(displayElement);
            }
//            else {
////                System.out.println("Removing element: " + displayElement);
//                  // Might want to log this better as having an error message in the display list is not good (kinda)
//            }
        }
        // Set the new display list
        this.turnDisplayList.setDisplayList(newList);
//        this.logger.finest("Cleaned up displayList: " + this.turnDisplayList.getDisplayQ().toString());
    }

    public boolean hasDeadActive(String teammateSlot){
        char team = teammateSlot.charAt(0);
        int teamInt = team - 'A';
        int start = teamInt * this.monInTeam;
        int end = teamInt +  this.activeMon;
        for (int i = start; i < end; i++) {
            if (this.monsters[i].isDead()) {
                return true;
            }
        }
        return false;
    }

    public void setTimer(String slot, BattleTimer battleTimer) {
        LinkedList<BattleTimer> input = new LinkedList<>();
        if (this.battleTimers.get(slot) == null) {
            input.push(battleTimer);
        } else {
            this.battleTimers.get(slot).push(battleTimer);
        }
    }

    public LinkedList<BattleTimer> getTimers(String slot) {
        if (this.battleTimers.get(slot) == null) {
            return new LinkedList<>();
        } else {
            return this.battleTimers.get(slot);
        }
    }

    public void purgeTimers(String slot) {
        // Check if there are timers
        if (this.battleTimers.get(slot) != null) {
            // Check each timer
            // Remove a finished timer from the list
            this.battleTimers.get(slot).removeIf(timer -> timer.getTimer() <= 0);
        }
    }

    public MonsterTurnInfo getMonsterTurnInfoBySlot(String slot) {
        return this.monsterTurnInfoArray[convertSlotToIndex(slot)];
    }

    public Monster getMonsterBySlot(String slot) {
        return this.monsters[convertSlotToIndex(slot)];
    }

    public int convertSlotToIndex(String slot) {
        if (slot == null ) {
            throw new IllegalArgumentException("Slot is null");
        } else if (slot.isEmpty()){
            throw new IllegalArgumentException("Slot is empty");
        }
        // Slot format: "(letter)(int 1-9)"
        char team = Character.toUpperCase(slot.charAt(0));

        int teamCode = team - 'A';
        // Check team code is a letter
        if (teamCode < 0 || teamCode > 25) {
//            logger.warning("Team Code is not valid from slot: " + slot);
            throw new IllegalArgumentException("Team Code is not valid from slot: " + slot);
        }

        // Read the int in the second index of the slot
        int slotCode;
        try {

            slotCode = Integer.parseInt(Character.toString(slot.charAt(1)));
            return (teamCode * this.monInTeam) + slotCode;

        } catch (NumberFormatException e) {

//            logger.warning("Slot Code: (" + slot + ") is beyond the monInTeam cap of: " + this.monInTeam);
            return -1;
        }

    }

    public Boolean isTeamDead(String deadSlot) {
        boolean dead = true;
        Monster[] team = getTeamBySlot(deadSlot);
        for (Monster monster : team) {
            if (!monster.isDead())
                dead = false;
        }
        return dead;
    }

    public Monster[] getTeamBySlot(String slot) {
        char teamCode = slot.charAt(0);
        int teamInt = teamCode - 'A';
        int start = teamInt * this.monInTeam;
        int end = (teamInt + 1) * this.monInTeam;
        Monster[] list = new Monster[end - start];
        if (end - start >= 0) System.arraycopy(this.monsters, start, list, 0, end - start);
        return list;

    }

    public String convertIntToSlot(int index) {
        int intTeam = index / this.monInTeam;
        int intMon = index - intTeam * this.monInTeam;

        char team = (char) ('A' + intTeam);
        String slot = String.valueOf(team);
        slot += Integer.toString(intMon);
        return slot;
    }

    public int isMoveOverriddenBySlot(String slot) {
        return this.moveOverrides[convertSlotToIndex(slot)];
    }

    public DeadMonsterThrowable getDeadMonsterThrowable() {
        return this.deadMonsterThrowable;
    }

    public void setDeadMonster(DeadMonsterThrowable deadMonster) {
        this.deadMonsterThrowable = deadMonster;
    }

    public Queue<MoveEffect> getMoveQueue() {
        return moveQueue;
    }

    public void setMoveQueue(Queue<MoveEffect> moveQueue) {
        this.moveQueue = moveQueue;
    }

    public int getMonCountTotal() {
        return monCountTotal;
    }

    public void setMonCountTotal(int monCountTotal) {
        this.monCountTotal = monCountTotal;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public int getMonInTeam() {
        return monInTeam;
    }

    public void setMonInTeam(int monInTeam) {
        this.monInTeam = monInTeam;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public Monster[] getMonsters() {
        return monsters;
    }

    public void setMonsters(Monster[] monsters) {
        this.monsters = monsters;
        // Fill out the monster slots array
        for (int i = 0; i < this.monCountTotal; i++) {
            String slot = convertIntToSlot(i);
            this.monSlots[i] = slot;
            this.monsters[i].setSlot(slot);
            if(this.monsters[i].getMonsterCode() == null)
                this.monsters[i].setMonsterCode(slot);
            this.monsters[i].setTeam(i / this.monInTeam);
        }
    }

    public String[] getMonSlots() {
        return monSlots;
    }

    public void setMonSlots(String[] monSlots) {
        this.monSlots = monSlots;
    }

    public MonsterTurnInfo[] getMonsterTurnInfoArray() {
        return monsterTurnInfoArray;
    }

    public void setMonsterTurnInfoArray(MonsterTurnInfo[] monsterTurnInfoArray) {
        this.monsterTurnInfoArray = monsterTurnInfoArray;
    }

    public TurnDisplayList getTurnDisplayList() {
        return turnDisplayList;
    }

    public void setTurnDisplayList(TurnDisplayList turnDisplayList) {
        this.turnDisplayList = turnDisplayList;
    }

    public void setDeadMonsterThrowable(DeadMonsterThrowable deadMonsterThrowable) {
        this.deadMonsterThrowable = deadMonsterThrowable;
    }

    public HashMap<String, LinkedList<BattleTimer>> getBattleTimers() {
        return battleTimers;
    }

    public void setBattleTimers(HashMap<String, LinkedList<BattleTimer>> battleTimers) {
        this.battleTimers = battleTimers;
    }

    public int[] getMoveOverrides() {
        return moveOverrides;
    }

    public void setMoveOverrides(int[] moveOverrides) {
        this.moveOverrides = moveOverrides;
    }

//    public Logger getLogger() {
//        return logger;
//    }
//
//    public void setLogger(Logger logger) {
//        this.logger = logger;
//    }
//
//    public FileHandler getFileHandler() {
//        return fileHandler;
//    }
//
//    public void setFileHandler(FileHandler fileHandler) {
//        this.fileHandler = fileHandler;
//    }

    public int getActiveMon() {
        return activeMon;
    }

    public void setActiveMon(int activeMon) {
        this.activeMon = activeMon;
    }

    public Queue<Effect> getNextTurnEffects() {
        return nextTurnEffects;
    }

    public void setNextTurnEffects(Queue<Effect> nextTurnEffects) {
        this.nextTurnEffects = nextTurnEffects;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    public String toJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Append fields manually
        jsonBuilder.append("\"monCountTotal\":").append(monCountTotal).append(",");
        jsonBuilder.append("\"teamCount\":").append(teamCount).append(",");
        jsonBuilder.append("\"monInTeam\":").append(monInTeam).append(",");
        jsonBuilder.append("\"activeMon\":").append(activeMon).append(",");
        jsonBuilder.append("\"turnCount\":").append(turnCount).append(",");

        // Append monsters array
        jsonBuilder.append("\"monsters\":[");
        for (int i = 0; i < monsters.length; i++) {
            jsonBuilder.append(monsters[i].toJson());
            if (i < monsters.length - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("],");

        // Convert monSlots array to JSON with quotes around each slot
        jsonBuilder.append("\"monSlots\":[");
        for (int i = 0; i < monSlots.length; i++) {
            jsonBuilder.append("\"").append(monSlots[i]).append("\"");
            if (i < monSlots.length - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("],");

        // Append monstersTurnInfo
        jsonBuilder.append("\"monsterTurnInfoArray\":[");
        for (int i = 0; i < monsterTurnInfoArray.length; i++) {
            jsonBuilder.append(monsterTurnInfoArray[i].toJson());
            if (i < monsterTurnInfoArray.length - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("],");

        jsonBuilder.append("\"turnDisplayList\":").append(turnDisplayList.toJson()).append(",");
        jsonBuilder.append("\"nextTurnEffects\":").append(nextTurnEffects).append(",");
        jsonBuilder.append("\"battleTimers\":").append(battleTimers).append(",");
        jsonBuilder.append("\"moveOverrides\":").append(Arrays.toString(moveOverrides)).append(",");
        jsonBuilder.append("\"weather\":\"").append(weather).append("\",");
        jsonBuilder.append("\"terrain\":\"").append(terrain).append("\",");
        jsonBuilder.append("\"state\":\"").append(state).append("\"");

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
    public void fromJson(String json){
        Gson gson = new Gson();
        TurnInfoPackage turnInfoPackage = gson.fromJson(json, TurnInfoPackage.class);

        // Set all fields based on the parsed object
        // TODO Update if added any variables
        this.monCountTotal = turnInfoPackage.monCountTotal;
        this.teamCount = turnInfoPackage.teamCount;
        this.monInTeam = turnInfoPackage.monInTeam;
        this.activeMon = turnInfoPackage.activeMon;
        this.turnCount = turnInfoPackage.turnCount;
        this.monsters = turnInfoPackage.monsters;
        this.monSlots = turnInfoPackage.monSlots;
        this.monsterTurnInfoArray = turnInfoPackage.monsterTurnInfoArray;
        this.turnDisplayList = turnInfoPackage.turnDisplayList;
        this.deadMonsterThrowable = turnInfoPackage.deadMonsterThrowable;
        this.moveQueue = turnInfoPackage.moveQueue;
        this.nextTurnEffects = turnInfoPackage.nextTurnEffects;
        this.battleTimers = turnInfoPackage.battleTimers;
        this.moveOverrides = turnInfoPackage.moveOverrides;
        this.weather = turnInfoPackage.weather;
        this.terrain = turnInfoPackage.terrain;
        this.state = turnInfoPackage.state;
//        this.logger = turnInfoPackage.logger;
//        this.fileHandler = turnInfoPackage.fileHandler;

        /* * CompactSolution
        // Reflectively copy fields from the parsed object to this object
        for (java.lang.reflect.Field field : TurnInfoPackage.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                field.set(this, field.get(turnInfoPackage));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        */
    }

    public TurnInfoPackage(int monCountTotal, int teamCount, int monInTeam, int activeMon, int turnCount,
                           Monster[] monsters, String[] monSlots, MonsterTurnInfo[] monsterTurnInfoArray,
                           TurnDisplayList turnDisplayList, DeadMonsterThrowable deadMonsterThrowable,
                           Queue<MoveEffect> moveQueue, Queue<Effect> nextTurnEffects, HashMap<String,
            LinkedList<BattleTimer>> battleTimers, int[] moveOverrides, String weather, String terrain, State state) {
        this.monCountTotal = monCountTotal;
        this.teamCount = teamCount;
        this.monInTeam = monInTeam;
        this.activeMon = activeMon;
        this.turnCount = turnCount;
        this.monsters = monsters;
        this.monSlots = monSlots;
        this.monsterTurnInfoArray = monsterTurnInfoArray;
        this.turnDisplayList = turnDisplayList;
        this.deadMonsterThrowable = deadMonsterThrowable;
        this.moveQueue = moveQueue;
        this.nextTurnEffects = nextTurnEffects;
        this.battleTimers = battleTimers;
        this.moveOverrides = moveOverrides;
        this.weather = weather;
        this.terrain = terrain;
        this.state = state;
    }
}
