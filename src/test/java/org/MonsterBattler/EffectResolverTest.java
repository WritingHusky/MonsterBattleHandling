package org.MonsterBattler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class EffectResolverTest {

    private TurnInfoPackage turnInfoPackage;
    private Effect effect;
    private String attacker;
    private String defender;
    private Monster monster;
    private TurnDisplayList turnDisplayList;
    private TurnDisplayElement turnDisplayElement;

    @BeforeEach
    void setUp() {
        turnInfoPackage = Mockito.mock(TurnInfoPackage.class);
        effect = Mockito.mock(Effect.class);
        attacker = "A1";
        defender = "B1";
        monster = Mockito.mock(Monster.class);
        turnDisplayList = Mockito.mock(TurnDisplayList.class);
        turnDisplayElement = Mockito.mock(TurnDisplayElement.class);

    }

    //#region Triggers T:1

    @Test
    @DisplayName("T:1.1 Effect triggers successfully")
    void effectTriggersSuccessfully() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Unknown");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(new TurnDisplayList());
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(turnInfoPackage, times(1)).getTurnDisplayList();
        verify(effect, times(1)).getAttackType();
    }

    @Test
    @DisplayName("T:1.2 Effect does not trigger")
    void effectDoesNotTrigger() {
        when(effect.getTrigger()).thenReturn("Never");
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(effect, never()).getAttackType();
    }

    @Test
    @DisplayName("T:1.3 Effect triggers with unknown trigger")
    void effectTriggersWithUnknownTrigger() {
        when(effect.getTrigger()).thenReturn("Unknown");
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(effect, never()).getAttackType();
    }

    @Test
    @DisplayName("T:1.4 Effect triggers with empty trigger")
    void effectTriggersWithEmptyAttackType() {
        when(effect.getTrigger()).thenReturn("");
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(effect, never()).getAttackType();
    }
    @Test
    @DisplayName("T:1.5 Effect triggers with null trigger")
    void effectTriggersWithNullAttackType() {
        when(effect.getTrigger()).thenReturn(null);
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(effect, never()).getAttackType();
    }

    //#endregion

    //#region None Effects T:2

    @Test
    @DisplayName("T:2.1 Handle effect with NONE attack type and SWAP result code")
    void handleEffectWithNoneAttackTypeAndSwapResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("NONE");
        when(effect.getResultCode()).thenReturn("SWAP");
        when(turnInfoPackage.getMonsters()).thenReturn(new Monster[]{monster, monster});
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(new TurnDisplayList());
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(turnInfoPackage, times(1)).getMonsters();
    }

    @Test
    @DisplayName("T:2.1b EffectResolver handles Swap effect correctly")
    void handleSwapEffectCorrectly() {
        // Mock necessary objects
        Monster attacker = Mockito.mock(Monster.class);
        Monster defender = Mockito.mock(Monster.class);
        Monster[] monsters = {attacker, defender};

        // Define behavior of mocks
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("None");
        when(effect.getResultCode()).thenReturn("SWAP");
        when(turnInfoPackage.convertSlotToIndex("A0")).thenReturn(0);
        when(turnInfoPackage.convertSlotToIndex("B0")).thenReturn(1);
        when(turnInfoPackage.getMonsters()).thenReturn(monsters);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(new TurnDisplayList());

        // Call the method to test
        EffectResolver.doEffect(effect, turnInfoPackage, "A0", "B0");

        // Verify that the monsters array in TurnInfoPackage has been swapped
        verify(turnInfoPackage, times(1)).setMonsters(new Monster[]{defender, attacker});
    }

    @Test
    @DisplayName("T:2.2 Handle effect with NONE attack type and None result code")
    void handleEffectWithNoneAttackTypeAndNoneResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("NONE");
        when(effect.getResultCode()).thenReturn("None");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(new TurnDisplayList());

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(turnInfoPackage, times(0)).getMonsters();
    }

    //#endregion

    //#region Damage Effects T:3

    @Test
    @DisplayName("T:3.1a Handle effect with Damage attack type and None result code")
    void handleEffectWithDamageAttackTypeAndNoneResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Damage");
        when(effect.getResultCode()).thenReturn("None");
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(monster);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify that the monsters were retrieved and no damage was done
        verify(turnInfoPackage, times(2)).getMonsterBySlot(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));

        //Verify nothing else happened that wasn't supposed to
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:3.1b Handle effect with Damage attack type and Null result code")
    void handleEffectWithDamageAttackTypeAndNullResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Damage");
        when(effect.getResultCode()).thenReturn(null);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify that the monsters were retrieved and no damage was done
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));

        //Verify nothing else happened that wasn't supposed to
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:3.1c Handle effect with Damage attack type and Empty result code")
    void handleEffectWithDamageAttackTypeAndEmptyResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Damage");
        when(effect.getResultCode()).thenReturn("");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify that the monsters were retrieved and no damage was done
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));

        //Verify nothing else happened that wasn't supposed to
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:3.1d Handle effect with Damage attack type and Unknown result code")
    void handleEffectWithDamageAttackTypeAndUnknownResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Damage");
        when(effect.getResultCode()).thenReturn("Unknown");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify that the monsters were retrieved and no damage was done
        verify(turnInfoPackage, times(2)).getMonsterBySlot(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));

        //Verify nothing else happened that wasn't supposed to
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:3.2 Handle effect with Damage attack type and Physical-Damage result code")
    void handleEffectWithDamageAttackTypeAndPhysicalDamageResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Damage");
        when(effect.getResultCode()).thenReturn("Physical-Damage");
        when(effect.getEffectValue()).thenReturn("100");

        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(monster);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        when(monster.getType()).thenReturn("Normal");
        when(monster.getStats()).thenReturn(new int[]{45, 45, 45, 45, 45, 45});
        when(monster.getDamageByCalculation(any(turnInfoPackage.getClass()), any(effect.getClass()),anyString(), anyBoolean(), anyBoolean(),anyDouble(), anyInt())).thenReturn(10);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        verify(turnInfoPackage, times(2)).getMonsterBySlot(anyString());
        verify(monster, times(1)).doDamage(10, turnInfoPackage);
        verify(monster, times(0)).doDamage(0, turnInfoPackage);
        verify(turnDisplayList, times(1)).addMSGToList(any(TurnDisplayElement.class));

        //Verify nothing else happened that wasn't supposed to
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:3.3 Handle effect with Damage attack type and Special-Damage result code")
    void handleEffectWithDamageAttackTypeAndSpecialDamageResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Damage");
        when(effect.getResultCode()).thenReturn("Special-Damage");
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(monster);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);
        when(effect.getEffectValue()).thenReturn("100");
        when(monster.getType()).thenReturn("Normal");
        when(monster.getStats()).thenReturn(new int[]{45, 45, 45, 45, 45, 45});
        when(monster.getDamageByCalculation(any(turnInfoPackage.getClass()), any(effect.getClass()),anyString(), anyBoolean(), anyBoolean(),anyDouble(), anyInt())).thenReturn(10);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        verify(turnInfoPackage, times(2)).getMonsterBySlot(anyString());
        verify(monster, times(1)).doDamage(10, turnInfoPackage);
        verify(monster, times(0)).doDamage(0, turnInfoPackage);
        verify(turnDisplayList, times(1)).addMSGToList(any(TurnDisplayElement.class));

        //Verify nothing else happened that wasn't supposed to
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    //#endregion

    //#region State Effects T:4

    @Test
    @DisplayName("T:4.1a Handle effect with State attack type and None result code")
    void handleEffectWithStateAttackTypeAndNoneResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("State");
        when(effect.getResultCode()).thenReturn("None");
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(monster);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(2)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:4.1b Handle effect with State attack type and Null result code")
    void handleEffectWithStateAttackTypeAndNullResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("State");
        when(effect.getResultCode()).thenReturn(null);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:4.1c Handle effect with State attack type and Empty result code")
    void handleEffectWithStateAttackTypeAndEmptyResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("State");
        when(effect.getResultCode()).thenReturn("");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:4.1d Handle effect with State attack type and Unknown result code")
    void handleEffectWithStateAttackTypeAndUnknownResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("State");
        when(effect.getResultCode()).thenReturn("Unknown");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(2)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    //#endregion

    //#region Alter Effects T:5

    @Test
    @DisplayName("T:5.1a Handle effect with Alter attack type and None result code")
    void handleEffectWithAlterAttackTypeAndNoneResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Alter");
        when(effect.getResultCode()).thenReturn("None");
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(monster);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:5.1b Handle effect with Alter attack type and Null result code")
    void handleEffectWithAlterAttackTypeAndNullResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Alter");
        when(effect.getResultCode()).thenReturn(null);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:5.1c Handle effect with Alter attack type and Empty result code")
    void handleEffectWithAlterAttackTypeAndEmptyResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Alter");
        when(effect.getResultCode()).thenReturn("");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    @Test
    @DisplayName("T:5.1d Handle effect with Alter attack type and Unknown result code")
    void handleEffectWithAlterAttackTypeAndUnknownResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Alter");
        when(effect.getResultCode()).thenReturn("Unknown");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);

        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);

        // Verify nothing else happened that wasn't supposed to
        verify(turnInfoPackage, times(0)).getMonsterBySlot(anyString());
        verify(monster, times(0)).effectStat(anyInt(), anyInt());
        verify(monster, times(0)).setStatus(anyString());
        verify(monster, times(0)).doDamage(anyInt(), eq(turnInfoPackage));
        verify(turnInfoPackage, times(0)).getMonsters();
        verify(turnInfoPackage, times(0)).setMonsters(any(Monster[].class));
        verify(turnInfoPackage, times(0)).purgeTimers(anyString());
        verify(turnInfoPackage, times(0)).setWeather(anyString());
        verify(turnInfoPackage, times(0)).setTerrain(anyString());
    }

    //#endregion
}