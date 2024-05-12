package org.MonsterBattler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonsterTest {

    private Monster monster;
    private Monster monster1;
    private Monster monster2;

    @BeforeEach
    void setUp() {
        monster = new Monster("TestMonster", 1, new int[]{10, 10, 10, 10, 10, 10}, 1, "Normal", new MoveEffect[0]);
        monster1 = new Monster("TestMonster", 1, new int[]{10, 20, 30, 40, 50, 60}, 1, "Normal", new MoveEffect[0]);
        monster1.setIvValues(new int[]{5, 5, 5, 5, 5, 5});
        monster1.setEvValues(new int[]{10, 10, 10, 10, 10, 10});
        monster1.setLevel(50);
        monster1.generateStats();

        monster2 = new Monster("TestMonster", 1, new int[]{70, 80, 90, 70, 55, 38}, 1, "Normal", new MoveEffect[0]);
        monster2.setIvValues(new int[]{5, 5, 5, 50, 50, 50});
        monster2.setEvValues(new int[]{10, 10, 10, 10, 10, 10});
        monster2.setLevel(50);
        monster2.generateStats();
    }

    @Test
    @DisplayName("Monster takes damage correctly")
    void monsterTakesDamageCorrectly() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        monster.doDamage(5, turnInfoPackage);
        assertEquals(11, monster.getMaxHp());
        assertEquals(6, monster.getCurrentHp());
    }

    @Test
    @DisplayName("Damage calculation is correct for physical attack")
    void damageCalculationIsCorrectForPhysicalAttack() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        Effect effect = Mockito.mock(Effect.class);
        Monster attackerMonster = Mockito.mock(Monster.class);
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(attackerMonster);
        when(effect.getMoveType()).thenReturn("Normal");
        when(effect.getEffectValue()).thenReturn("100");
        when(attackerMonster.getStats()).thenReturn(new int[]{10, 10, 10, 10, 10, 10});
        int damage = monster.getDamageByCalculation(turnInfoPackage, effect, "A1", true,
                false, 1.0, 2);
        assertEquals(18, damage);
    }

    @Test
    @DisplayName("Damage calculation is correct for special attack")
    void damageCalculationIsCorrectForSpecialAttack() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        Effect effect = Mockito.mock(Effect.class);
        Monster attackerMonster = Mockito.mock(Monster.class);
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(attackerMonster);
        when(effect.getMoveType()).thenReturn("Normal");
        when(effect.getEffectValue()).thenReturn("100");
        when(attackerMonster.getStats()).thenReturn(new int[]{10, 10, 10, 10, 10, 10});
        int damage = monster.getDamageByCalculation(turnInfoPackage, effect, "A1", false,
                false, 1.0, 2);
        assertEquals(18, damage);
    }

    @Test
    @DisplayName("Damage calculation is correct with critical hit")
    void damageCalculationIsCorrectWithCriticalHit() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        Effect effect = Mockito.mock(Effect.class);
        Monster attackerMonster = Mockito.mock(Monster.class);
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(attackerMonster);
        when(effect.getMoveType()).thenReturn("Normal");
        when(effect.getEffectValue()).thenReturn("100");
        when(attackerMonster.getStats()).thenReturn(new int[]{10, 10, 10, 10, 10, 10});
        int damage = monster.getDamageByCalculation(turnInfoPackage, effect, "A1", true,
                true, 1.0, 2);
        assertEquals(35, damage);
    }

    @Test
    @DisplayName("Damage calculation is correct with type advantage 2x")
    void damageCalculationIsCorrectWithTypeAdvantage2x() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        Effect effect = Mockito.mock(Effect.class);
        Monster attackerMonster = Mockito.mock(Monster.class);
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(attackerMonster);
        when(effect.getMoveType()).thenReturn("Normal");
        when(effect.getEffectValue()).thenReturn("100");
        when(attackerMonster.getStats()).thenReturn(new int[]{10, 10, 10, 10, 10, 10});
        int damage = monster.getDamageByCalculation(turnInfoPackage, effect, "A1", true,
                false, 1.0, 4);
        assertEquals(35, damage);
    }

    @Test
    @DisplayName("Damage calculation is correct with type advantage 0.5x")
    void damageCalculationIsCorrectWithTypeAdvantage0_5x() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        Effect effect = Mockito.mock(Effect.class);
        Monster attackerMonster = Mockito.mock(Monster.class);
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(attackerMonster);
        when(effect.getMoveType()).thenReturn("Normal");
        when(effect.getEffectValue()).thenReturn("100");
        when(attackerMonster.getStats()).thenReturn(new int[]{10, 10, 10, 10, 10, 10});
        int damage = monster.getDamageByCalculation(turnInfoPackage, effect, "A1", true,
                false, 1.0, 1);
        assertEquals(9, damage);
    }

    @Test
    @DisplayName("Damage calculation is correct with type advantage 0x")
    void damageCalculationIsCorrectWithTypeAdvantage0() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        Effect effect = Mockito.mock(Effect.class);
        Monster attackerMonster = Mockito.mock(Monster.class);
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(attackerMonster);
        when(effect.getMoveType()).thenReturn("Normal");
        when(effect.getEffectValue()).thenReturn("100");
        when(attackerMonster.getStats()).thenReturn(new int[]{10, 10, 10, 10, 10, 10});
        int damage = monster.getDamageByCalculation(turnInfoPackage, effect, "A1", true,
                false, 1.0, 0);
        assertEquals(0, damage);
    }

    @Test
    @DisplayName("Monster dies when taking too much damage")
    void monsterDiesWhenTakingTooMuchDamage() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        monster.doDamage(11, turnInfoPackage);
        assertTrue(monster.isDead());
    }

    @Test
    @DisplayName("Monster takes correct amount of damage")
    void monsterTakesCorrectAmountOfDamage() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        monster.doDamage(2, turnInfoPackage);
        assertEquals(9, monster.getCurrentHp());
        assertEquals(11, monster.getMaxHp());
    }

    @Test
    @DisplayName("Monster dies when taking max damage")
    void monsterDiesWhenTakingMaxDamage() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        monster.doDamage(Integer.MAX_VALUE, turnInfoPackage);
        assertTrue(monster.isDead());
        assertEquals(0, monster.getCurrentHp());
    }

    @Test
    @DisplayName("Monster heals correctly")
    void monsterHealsCorrectAmount() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        monster.doDamage(5, turnInfoPackage);
        assertEquals(6, monster.getCurrentHp());
        monster.doDamage(-2, turnInfoPackage);
        assertEquals(8, monster.getCurrentHp());
    }

    @Test
    @DisplayName("Monster heals correctly with Overflow")
    void monsterHealsCorrectAmountWithOverFlow() {
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        assertEquals(11, monster.getMaxHp());
        monster.doDamage(5, turnInfoPackage);
        assertEquals(6, monster.getCurrentHp());
        monster.doDamage(-10, turnInfoPackage);
        assertEquals(11, monster.getCurrentHp());
        monster.doDamage(Integer.MIN_VALUE, turnInfoPackage);
        assertEquals(11, monster.getCurrentHp());
    }

    @Test
    @DisplayName("Monster stats are affected correctly")
    void monsterStatsAreAffectedCorrectly() {
        monster.effectStat(1, 2);
        assertEquals(2, monster.getStatusEffectedStats()[1]);
    }

    @Test
    @DisplayName("Monster stats are not affected beyond limits")
    void monsterStatsAreNotAffectedBeyondLimits() {
        monster.effectStat(1, 7);
        assertEquals(6, monster.getStatusEffectedStats()[1]);
    }

    @Test
    @DisplayName("Monster stats are not reduced beyond limits")
    void monsterStatsAreNotReducedBeyondLimits() {
        monster.effectStat(1, -7);
        assertEquals(-6, monster.getStatusEffectedStats()[1]);
    }

    @Test
    @DisplayName("Monster stats are not reduced beyond limits")
    void monsterStatsAreNotReducedWhen0() {
        monster.effectStat(1, 0);
        assertEquals(0, monster.getStatusEffectedStats()[1]);
    }

    @Test
    @DisplayName("effectStat does not affect other stats")
    void effectStatDoesNotAffectOtherStats() {
        monster.effectStat(1, 2);
        for (int i = 0; i < monster.getStatusEffectedStats().length; i++) {
            if (i != 1) {
                assertEquals(0, monster.getStatusEffectedStats()[i]);
            }
        }
    }

    @Test
    @DisplayName("effectStatBeyond increases stat correctly")
    void effectStatBeyondIncreasesStatCorrectly() {
        monster.effectStatBeyond(1, 2);
        assertEquals(2, monster.getBeyondEffectedStats()[1]);
    }

    @Test
    @DisplayName("effectStatBeyond decreases stat correctly")
    void effectStatBeyondDecreasesStatCorrectly() {
        monster.effectStatBeyond(1, -2);
        assertEquals(-2, monster.getBeyondEffectedStats()[1]);
    }

    @Test
    @DisplayName("effectStatBeyond does not affect other stats")
    void effectStatBeyondDoesNotAffectOtherStats() {
        monster.effectStatBeyond(1, 2);
        for (int i = 0; i < monster.getBeyondEffectedStats().length; i++) {
            if (i != 1) {
                assertEquals(0, monster.getBeyondEffectedStats()[i]);
            }
        }
    }

    @Test
    @DisplayName("Monster ability triggers correctly")
    void monsterAbilityTriggersCorrectly() {
        monster.setAbilityTrigger("TestTrigger");
        assertTrue(monster.doesAbilityTrigger("TestTrigger"));
    }

    @Test
    @DisplayName("Monster ability does not trigger incorrectly")
    void monsterAbilityDoesNotTriggerIncorrectly() {
        monster.setAbilityTrigger("TestTrigger");
        assertFalse(monster.doesAbilityTrigger("WrongTrigger"));
    }

    @Test
    @DisplayName("Stats are generated correctly")
    void statsAreGeneratedCorrectly() {

        int[] expectedStats = {73, 28, 38, 48, 58, 68};
        assertArrayEquals(expectedStats, monster1.getGeneratedStats());
        int[] expectedStats2 = {133, 88, 98, 101, 86, 69};
        assertArrayEquals(expectedStats2, monster2.getGeneratedStats());
    }

    @Test
    @DisplayName("Health is generated correctly")
    void healthIsGeneratedCorrectly() {

        assertEquals(73, monster1.getMaxHp());
        assertEquals(73, monster1.getCurrentHp());
        assertEquals(133, monster2.getMaxHp());
        assertEquals(133, monster2.getCurrentHp());
    }

    @Test
    @DisplayName("toJson generates correct JSON")
    void toJsonGeneratesCorrectJson() {
        Monster monster = new Monster("TestMonster", 1, new int[]{10, 10, 10, 10, 10, 10}, 1, "Normal", new MoveEffect[0]);
        String expectedJson = "{\"name\":\"TestMonster\",\"currentHp\":11,\"maxHp\":11,\"dexID\":1,\"monsterCode\":\"null\"" +
                ",\"status\":\"None\",\"statusTimer\":0,\"isDead\":false,\"team\":0,\"position\":\"Ground\"," +
                "\"stats\":[10, 10, 10, 10, 10, 10],\"level\":1,\"type\":\"Normal\",\"slot\":\"null\",\"moves\":[]," +
                "\"generatedStats\":[11, 5, 5, 5, 5, 5],\"statusEffectedStats\":[0, 0, 0, 0, 0, 0],\"beyondEffectedStats\":[0, 0, 0, 0, 0, 0]}";
        assertEquals(expectedJson, monster.toJson());
    }

    @Test
    @DisplayName("Monster status is set correctly")
    void monsterStatusIsSetCorrectly() {
        monster.setStatus("Burn");
        assertEquals("Burn", monster.getStatus());
    }

    @Test
    @DisplayName("getStatus retrieves correct status")
    void getStatusRetrievesCorrectStatus() {
        monster.setStatus("Burn");
        assertEquals("Burn", monster.getStatus());
    }

    @Test
    @DisplayName("getStatus retrieves default status correctly")
    void getStatusRetrievesDefaultStatusCorrectly() {
        assertEquals("None", monster.getStatus());
    }

    //TODO Add more tickStatus tests when more status handling types are added

    @Test
    @DisplayName("tickStatusActive handles Burn status correctly")
    void tickStatusActiveHandlesBurnStatusCorrectly() {
        monster.setStatus("Burn");
        monster.setMaxHp(16);
        monster.setCurrentHp(16);
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        TurnDisplayList turnDisplayList = Mockito.mock(TurnDisplayList.class);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);
        monster.tickStatusActive(turnInfoPackage);
        assertEquals(15, monster.getCurrentHp());
    }

    @Test
    @DisplayName("tickStatusActive handles Poison status correctly")
    void tickStatusActiveHandlesPoisonStatusCorrectly() {
        monster.setStatus("Poison");
        monster.setMaxHp(16);
        monster.setCurrentHp(16);
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        TurnDisplayList turnDisplayList = Mockito.mock(TurnDisplayList.class);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);
        monster.tickStatusActive(turnInfoPackage);
        assertEquals(14, monster.getCurrentHp());
    }

    @Test
    @DisplayName("tickStatusActive handles Badly Poison status correctly")
    void tickStatusActiveHandlesBadlyPoisonStatusCorrectly() {
        monster.setStatus("Badly Poison");
        monster.setMaxHp(16);
        monster.setCurrentHp(16);
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        TurnDisplayList turnDisplayList = Mockito.mock(TurnDisplayList.class);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);
        monster.tickStatusActive(turnInfoPackage);
        assertEquals(15, monster.getCurrentHp());
        monster.tickStatusActive(turnInfoPackage);
        assertEquals(13, monster.getCurrentHp());
    }

    @Test
    @DisplayName("tickStatusActive handles no status correctly")
    void tickStatusActiveHandlesNoStatusCorrectly() {
        monster.setStatus("None");
        monster.setMaxHp(16);
        monster.setCurrentHp(16);
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        TurnDisplayList turnDisplayList = Mockito.mock(TurnDisplayList.class);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);
        monster.tickStatusActive(turnInfoPackage);
        assertEquals(16, monster.getCurrentHp());
    }

    @Test
    @DisplayName("tickStatusPassive does nothing")
    void tickStatusPassiveDoesNothing() {
        monster.setStatus("None");
        monster.setMaxHp(16);
        monster.setCurrentHp(16);
        TurnInfoPackage turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        monster.tickStatusPassive(turnInfoPackage);
        assertEquals(16, monster.getCurrentHp());
    }

    @Test
    @DisplayName("getAbilityTrigger retrieves correct ability trigger")
    void getAbilityTriggerRetrievesCorrectAbilityTrigger() {
        monster.setAbilityTrigger("Always");
        assertEquals("Always", monster.getAbilityTrigger());
    }

    @Test
    @DisplayName("setAbilityTrigger sets ability trigger correctly")
    void setAbilityTriggerSetsAbilityTriggerCorrectly() {
        monster.setAbilityTrigger("Never");
        assertEquals("Never", monster.getAbilityTrigger());
    }

    @Test
    @DisplayName("getAbilityEffect retrieves correct ability effect")
    void getAbilityEffectRetrievesCorrectAbilityEffect() {
        MoveEffect abilityEffect = Mockito.mock(MoveEffect.class);
        monster.setAbilityEffect(abilityEffect);
        assertEquals(abilityEffect, monster.getAbilityEffect());
    }

    @Test
    @DisplayName("setAbilityEffect sets ability effect correctly")
    void setAbilityEffectSetsAbilityEffectCorrectly() {
        MoveEffect abilityEffect = Mockito.mock(MoveEffect.class);
        monster.setAbilityEffect(abilityEffect);
        assertEquals(abilityEffect, monster.getAbilityEffect());
    }

    @Test
    @DisplayName("getEvValues retrieves correct EV values")
    void getEvValuesRetrievesCorrectEvValues() {
        int[] evValues = {1, 2, 3, 4, 5, 6};
        monster.setEvValues(evValues);
        assertArrayEquals(evValues, monster.getEvValues());
    }

    @Test
    @DisplayName("setEvValues sets EV values correctly")
    void setEvValuesSetsEvValuesCorrectly() {
        int[] evValues = {1, 2, 3, 4, 5, 6};
        monster.setEvValues(evValues);
        assertArrayEquals(evValues, monster.getEvValues());
    }

    @Test
    @DisplayName("getIvValues retrieves correct IV values")
    void getIvValuesRetrievesCorrectIvValues() {
        int[] ivValues = {1, 2, 3, 4, 5, 6};
        monster.setIvValues(ivValues);
        assertArrayEquals(ivValues, monster.getIvValues());
    }

    @Test
    @DisplayName("setIvValues sets IV values correctly")
    void setIvValuesSetsIvValuesCorrectly() {
        int[] ivValues = {1, 2, 3, 4, 5, 6};
        monster.setIvValues(ivValues);
        assertArrayEquals(ivValues, monster.getIvValues());
    }

    @Test
    @DisplayName("getStatusEffectedStats retrieves correct status effected stats")
    void getStatusEffectedStatsRetrievesCorrectStatusEffectedStats() {
        int[] statusEffectedStats = {1, 2, 3, 4, 5, 6};
        monster.setStatusEffectedStats(statusEffectedStats);
        assertArrayEquals(statusEffectedStats, monster.getStatusEffectedStats());
    }

    @Test
    @DisplayName("setStatusEffectedStats sets status effected stats correctly")
    void setStatusEffectedStatsSetsStatusEffectedStatsCorrectly() {
        int[] statusEffectedStats = {1, 2, 3, 4, 5, 6};
        monster.setStatusEffectedStats(statusEffectedStats);
        assertArrayEquals(statusEffectedStats, monster.getStatusEffectedStats());
    }

    @Test
    @DisplayName("getBeyondEffectedStats retrieves correct beyond effected stats")
    void getBeyondEffectedStatsRetrievesCorrectBeyondEffectedStats() {
        int[] beyondEffectedStats = {1, 2, 3, 4, 5, 6};
        monster.setBeyondEffectedStats(beyondEffectedStats);
        assertArrayEquals(beyondEffectedStats, monster.getBeyondEffectedStats());
    }

    @Test
    @DisplayName("setBeyondEffectedStats sets beyond effected stats correctly")
    void setBeyondEffectedStatsSetsBeyondEffectedStatsCorrectly() {
        int[] beyondEffectedStats = {1, 2, 3, 4, 5, 6};
        monster.setBeyondEffectedStats(beyondEffectedStats);
        assertArrayEquals(beyondEffectedStats, monster.getBeyondEffectedStats());
    }

    @Test
    @DisplayName("getPosition retrieves correct position")
    void getPositionRetrievesCorrectPosition() {
        String position = "Air";
        monster.setPosition(position);
        assertEquals(position, monster.getPosition());
    }

    @Test
    @DisplayName("setPosition sets position correctly")
    void setPositionSetsPositionCorrectly() {
        String position = "Ground";
        monster.setPosition(position);
        assertEquals(position, monster.getPosition());
    }

    @Test
    @DisplayName("getMoves retrieves correct moves")
    void getMovesRetrievesCorrectMoves() {
        MoveEffect[] moves = {new MoveEffect(), new MoveEffect()};
        monster.setMoves(moves);
        assertArrayEquals(moves, monster.getMoves());
    }

    @Test
    @DisplayName("setMoves sets moves correctly")
    void setMovesSetsMovesCorrectly() {
        MoveEffect[] moves = {new MoveEffect(), new MoveEffect()};
        monster.setMoves(moves);
        assertArrayEquals(moves, monster.getMoves());
    }

    @Test
    @DisplayName("getTeam retrieves correct team")
    void getTeamRetrievesCorrectTeam() {
        int team = 1;
        monster.setTeam(team);
        assertEquals(team, monster.getTeam());
    }

    @Test
    @DisplayName("setTeam sets team correctly")
    void setTeamSetsTeamCorrectly() {
        int team = 2;
        monster.setTeam(team);
        assertEquals(team, monster.getTeam());
    }

    @Test
    @DisplayName("getSlot retrieves correct slot")
    void getSlotRetrievesCorrectSlot() {
        String slot = "Slot1";
        monster.setSlot(slot);
        assertEquals(slot, monster.getSlot());
    }

    @Test
    @DisplayName("setSlot sets slot correctly")
    void setSlotSetsSlotCorrectly() {
        String slot = "Slot2";
        monster.setSlot(slot);
        assertEquals(slot, monster.getSlot());
    }

    @Test
    @DisplayName("getIsDead retrieves correct death status")
    void getIsDeadRetrievesCorrectDeathStatus() {
        monster.setIsDead(true);
        assertTrue(monster.getIsDead());
    }

    @Test
    @DisplayName("setIsDead sets death status correctly")
    void setIsDeadSetsDeathStatusCorrectly() {
        monster.setIsDead(false);
        assertFalse(monster.getIsDead());
    }

    @Test
    @DisplayName("getCurrentHp retrieves correct current HP")
    void getCurrentHpRetrievesCorrectCurrentHp() {
        int currentHp = 50;
        monster.setCurrentHp(currentHp);
        assertEquals(currentHp, monster.getCurrentHp());
    }

    @Test
    @DisplayName("setCurrentHp sets current HP correctly")
    void setCurrentHpSetsCurrentHpCorrectly() {
        int currentHp = 75;
        monster.setCurrentHp(currentHp);
        assertEquals(currentHp, monster.getCurrentHp());
    }

    @Test
    @DisplayName("getMaxHp retrieves correct max HP")
    void getMaxHpRetrievesCorrectMaxHp() {
        int maxHp = 100;
        monster.setMaxHp(maxHp);
        assertEquals(maxHp, monster.getMaxHp());
    }

    @Test
        @DisplayName("setMaxHp sets max HP correctly")
        void setMaxHpSetsMaxHpCorrectly() {
            int maxHp = 150;
            monster.setMaxHp(maxHp);
            assertEquals(maxHp, monster.getMaxHp());
        }

    @Test
    @DisplayName("getName retrieves correct name")
    void getNameRetrievesCorrectName() {
        String name = "MonsterName";
        monster.setName(name);
        assertEquals(name, monster.getName());
    }

    @Test
    @DisplayName("setName sets name correctly")
    void setNameSetsNameCorrectly() {
        String name = "NewMonsterName";
        monster.setName(name);
        assertEquals(name, monster.getName());
    }

    @Test
    @DisplayName("getDexID retrieves correct Dex ID")
    void getDexIDRetrievesCorrectDexID() {
        int dexID = 123;
        monster.setDexID(dexID);
        assertEquals(dexID, monster.getDexID());
    }

    @Test
    @DisplayName("setDexID sets Dex ID correctly")
    void setDexIDSetsDexIDCorrectly() {
        int dexID = 456;
        monster.setDexID(dexID);
        assertEquals(dexID, monster.getDexID());
    }

    @Test
    @DisplayName("getMonsterCode retrieves correct Monster Code")
    void getMonsterCodeRetrievesCorrectMonsterCode() {
        String monsterCode = "MonsterCode123";
        monster.setMonsterCode(monsterCode);
        assertEquals(monsterCode, monster.getMonsterCode());
    }

    @Test
    @DisplayName("setMonsterCode sets Monster Code correctly")
    void setMonsterCodeSetsMonsterCodeCorrectly() {
        String monsterCode = "NewMonsterCode456";
        monster.setMonsterCode(monsterCode);
        assertEquals(monsterCode, monster.getMonsterCode());
    }

    @Test
    @DisplayName("getLevel retrieves correct Level")
    void getLevelRetrievesCorrectLevel() {
        int level = 10;
        monster.setLevel(level);
        assertEquals(level, monster.getLevel());
    }

    @Test
    @DisplayName("setLevel sets Level correctly")
    void setLevelSetsLevelCorrectly() {
        int level = 20;
        monster.setLevel(level);
        assertEquals(level, monster.getLevel());
    }

    @Test
    @DisplayName("getType retrieves correct Type")
    void getTypeRetrievesCorrectType() {
        String type = "Fire";
        monster.setType(type);
        assertEquals(type, monster.getType());
    }

    @Test
    @DisplayName("setType sets Type correctly")
    void setTypeSetsTypeCorrectly() {
        String type = "Water";
        monster.setType(type);
        assertEquals(type, monster.getType());
    }
    @Test
    @DisplayName("getStatusTimer retrieves correct Status Timer")
    void getStatusTimerRetrievesCorrectStatusTimer() {
        int statusTimer = 5;
        monster.setStatusTimer(statusTimer);
        assertEquals(statusTimer, monster.getStatusTimer());
    }

    @Test
    @DisplayName("setStatusTimer sets Status Timer correctly")
    void setStatusTimerSetsStatusTimerCorrectly() {
        int statusTimer = 10;
        monster.setStatusTimer(statusTimer);
        assertEquals(statusTimer, monster.getStatusTimer());
    }
}