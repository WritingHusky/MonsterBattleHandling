# List of Tests for the project as it stands

## Test: A-1 EffectResolverTest
This will test all the various effects that can be resolved. As such, it will be a large test.
Expect this list to grow as more effects are added and planned.
- Test section: Triggers
    - [x] Test: 1.1 "Always"
    - [x] Test: 1.2 "Never"
    - [x] Test: 1.3 "Unknown"
    - [x] Test: 1.4 "" (empty string)
    - [x] Test: 1.5 null
- Test section: Effects
  - Test subsection: 2 AttackType "None"
    - [x] Test: 2.1 "Swap"
      - [ ] Test: 2.1.1 validate swap with own team
    - [x] Test: 2.2 "None"
  - Test subsection: 3 AttackType "Damage"
      - [x] Test: 3.1 "None" / null / "" / unknown
      - [x] Test: 3.2 "Physical-Damage"
      - [x] Test: 3.3 "Special-Damage"
      - [ ] Test: 3.4 "nth-damage"
      - [ ] Test: 3.5 "Heal"
  - Test subsection: 4 AttackType "State"
    - [x] Test: 4.1 "None" / null / "" / unknown
    - [ ] Test: 4.2 "Clear Status"
    - [ ] Test: 4.3 "Burn"
    - [ ] Test: 4.4 "Freeze"
    - [ ] Test: 4.5 "Paralysis"
    - [ ] Test: 4.6 "Poison"
    - [ ] Test: 4.7 Badly Poison"
    - [ ] Test: 4.8 "Sleep"
    - [ ] Subsection 4.9: Stats
        - [ ] Test: 4.9.1 "None" / null / "" / unknown
        - [ ] Test: 4.9.2 "Attack"
        - [ ] Test: 4.9.3 "Defense"
        - [ ] Test: 4.9.4 "Special Attack"
        - [ ] Test: 4.9.5 "Special Defense"
        - [ ] Test: 4.9.6 "Speed"
        - [ ] Test: 4.9.7 "Accuracy" (unimplemented)
        - [ ] Test: 4.9.8 "Evasion" (unimplemented)
  - Test subsection: 5 AttackType "Alter"
    - [x] Test: 5.1 " None" / null / "" / unknown
    - Subsection: 5.2 Weather
        - [ ] Test: 5.2.2 "Clear Weather"
        - [ ] Test: 5.2.3 "Harsh Sunlight"
        - [ ] Test: 5.2.4 "Rain"
        - [ ] Test: 5.2.5 "Sandstorm"
        - [ ] Test: 5.2.6 "Snow"
        - [ ] Test: 5.3.7 "Fog"
    - Subsection: 5.3 Terrain
      - [ ] Test: 5.3.1 "Clear Terrain"
      - [ ] Test: 5.3.2 "Electric Terrain"
      - [ ] Test: 5.3.3 "Grassy Terrain"
      - [ ] Test: 5.3.4 "Misty Terrain"
      - [ ] Test: 5.3.5 "Psychic Terrain"

## Test: A-2 BattleSimulatorTest
This is the most important test as it will test the entire project.
This class is what runs the entire project and as such, it is important to ensure that it is working as intended.
The other tests will be used to ensure that the internal parts of the project are working but, 
this test will ensure that the turn logic is working properly.

test-able parts:
  - move validation
  - Monster Turn Info
  - Accuracy checks
    - failed check does not allow move to be resolved
  - 
  - turn logic
    - pre move
    - move effect calls
    - post move

## Test: A-3 MoveQueueBuilderTest
These test will ensure that the Move Queue is being built correctly according to all possible scenarios.

test-able parts:
  - priority
  - monster speed
  - multiple monsters / moves

Can give it a mock for the random number generator.

## Test: A-4 TurnInfoPackageTest
The T.I.P is a large part of how this project functions. As such, it is important to ensure that it is working as intended.
There are some parts of the T.I.P that are required to work in order for the project to function as intended.
This class also contains a lot of getters and setters, which are the less important tests however,
they are still useful to ensure that the code is working as intended.

important parts:
  - rules validation
  - TIP creation validation
  - convert between slot and index both ways
  - death checks
  - get team by slot
  - to and from json conversion

## Test: A-5 MonsterTest
This class is pretty important as it is the class that holds all the information about the monster. 
It has a few methods that are important as they handle some logic and thus are very important to test.
It is mostly getters and setters, but it is still important to ensure that the code is working as intended.

important parts:
  - do damage
  - damage calculation
  - tick status active and passive
  - stat effects
  - stat creation and generation
  - to json validation

# List of semi-redundant tests that need to be created
These are test that are not as necessary as they only ensure that the code is working as intended. 
They are not necessary for the project to function as intended.
These classes mostly contain getters and setters and as such are not as useful to test.
However, that does not mean that they are not useful. 
They can be used to ensure that if the code is changed, it still functions as intended.

## Test: B-1 MoveTest

## Test: B-2 EffectTest

## Test: B-3 BattleTimerTest

## Test: B-4 TurnDisplayTest

## Test: B-5 MonsterTurnInfoTest
