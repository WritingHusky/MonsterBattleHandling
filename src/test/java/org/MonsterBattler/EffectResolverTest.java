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

    @Test
    @DisplayName("Effect triggers successfully")
    void effectTriggersSuccessfully() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Unknown");
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(new TurnDisplayList());
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(turnInfoPackage, times(1)).getTurnDisplayList();
    }

    @Test
    @DisplayName("Effect does not trigger")
    void effectDoesNotTrigger() {
        when(effect.getTrigger()).thenReturn("Never");
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(turnInfoPackage, never()).getTurnDisplayList();
    }

    @Test
    @DisplayName("Effect triggers with unknown trigger")
    void effectTriggersWithUnknownTrigger() {
        when(effect.getTrigger()).thenReturn("Unknown");
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(turnInfoPackage, never()).getTurnDisplayList();
    }

    @Test
    @DisplayName("Handle effect with NONE attack type and SWAP result code")
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
    @DisplayName("Handle effect with Damage attack type and Physical-Damage result code")
    void handleEffectWithDamageAttackTypeAndPhysicalDamageResultCode() {
        when(effect.getTrigger()).thenReturn("Always");
        when(effect.getAttackType()).thenReturn("Damage");
        when(effect.getResultCode()).thenReturn("Physical-Damage");
        when(turnInfoPackage.getMonsterBySlot(anyString())).thenReturn(monster);
        when(turnInfoPackage.getTurnDisplayList()).thenReturn(turnDisplayList);
        when(effect.getEffectValue()).thenReturn("100");
        when(monster.getType()).thenReturn("Normal");
        when(monster.getStats()).thenReturn(new int[]{45, 45, 45, 45, 45, 45});
        when(monster.getDamageByCalculation(any(turnInfoPackage.getClass()), any(effect.getClass()),anyString(), anyBoolean(), anyBoolean(),anyDouble(), anyInt())).thenReturn(10);
        EffectResolver.doEffect(effect, turnInfoPackage, attacker, defender);
        verify(turnInfoPackage, times(6)).getMonsterBySlot(anyString());
        verify(monster, times(1)).doDamage(10, turnInfoPackage);
        verify(monster, times(0)).doDamage(0, turnInfoPackage);
        verify(turnDisplayList, times(1)).addMSGToList(any(TurnDisplayElement.class));

    }
}