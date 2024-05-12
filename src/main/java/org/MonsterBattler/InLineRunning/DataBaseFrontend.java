package org.MonsterBattler.InLineRunning;

import org.MonsterBattler.Effect;
import org.MonsterBattler.Monster;
import org.MonsterBattler.MoveEffect;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class DataBaseFrontend {

    final String dexDataUrl = "\\dex.json";
    final String moveDataUrl = "\\moves.json";

    public static JSONArray dexJsObject;
    public static JSONArray moveJsArray;

    public DataBaseFrontend() {
        try {
            Path currentRelativePath = Paths.get("");
            String currentPathString = currentRelativePath.toAbsolutePath() + "\\black_box";
            File dexData = new File(currentPathString + dexDataUrl);
            InputStream is = new FileInputStream(dexData);
            String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
            dexJsObject = new JSONObject(jsonTxt).getJSONArray("dex");

            File moveData = new File(currentPathString + moveDataUrl);
            is = new FileInputStream(moveData);
            jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONObject moveJsObject = new JSONObject(jsonTxt);
            moveJsArray = moveJsObject.getJSONArray("moves");
        } catch (Exception ignored) {

        }
    }

    public Monster pullMonster(int dexID, int id) {
        Monster monster = new Monster();
        // Load up all the data about the mon from a database

        /*
         * Info to get:
         * name
         * hp (Generated with stats)
         * dexid
         * ability
         * stats
         * level
         * type
         * moves
         */
        // Get Name
        JSONObject monsterJsonObject = dexJsObject.getJSONObject(dexID);
        monster.setName(monsterJsonObject.getString("Name") + id);
        // Set dexid
        monster.setDexID(dexID);
        // Get stats
        JSONArray statArray = monsterJsonObject.getJSONArray("Stats");
        int[] newStats = new int[6];
        for (int i = 0; i < 6; i++) {
            newStats[i] = statArray.getInt(i);
        }
        monster.setStats(newStats);
        // Get level or set basic
        monster.setLevel(25);
        // Generate stats and HP
        monster.generateStats();

        // Get type
        monster.setType(monsterJsonObject.getString("Type"));

        // Get moves
        JSONArray moveArray = monsterJsonObject.getJSONArray("Known-Moves"); // Read out the moves
        // Build out move array
        MoveEffect[] moves = new MoveEffect[5];
        for (int i = 0; i < moves.length; i++) {
            moves[i] = pullMoveEffect(moveArray.getInt(i));
        }
        monster.setMoves(moves);

        // Get the ability
        monster.setAbility(convertJsonToMoveEffect(monsterJsonObject.getJSONObject("ability")));
        monster.setAbilityTrigger(monsterJsonObject.getString("Ability-Trigger"));

        monster.setMonsterCode("" + id);

        // Push out the monster
        return monster;
    }

    public static MoveEffect pullMoveEffect(int moveID) {
        return convertJsonToMoveEffect(moveJsArray.getJSONObject(moveID));
    }

    private static MoveEffect convertJsonToMoveEffect(JSONObject json) {
        MoveEffect move = new MoveEffect();
        /*
         * Info to get:
         * name
         * priority
         * power
         * accuracy
         * effects []
         * type
         */
        // Get name
        move.setMoveName(json.getString("Name"));

        // Get priority
        move.setPriority(json.getInt("Priority"));

        // Get power
        move.setPower(json.getInt("Power"));

        // Get accuracy
        move.setAccuracy(json.getInt("Accuracy"));

        // Get typing
        move.setTyping(json.getString("Type"));

        // Get effects[]
        move.setMoveEffects(new LinkedList<>());
        JSONArray effectArray = json.getJSONArray("effects");
        for (int i = 0; i < effectArray.length(); i++) {
            Effect effect = new Effect();
            JSONObject effectJsonObject = effectArray.getJSONObject(i);
            effect.setTrigger(effectJsonObject.getString("Trigger"));
            effect.setFailedTriggerMSG(effectJsonObject.getString("FailedMsg"));
            effect.setResultCode(effectJsonObject.getString("Result"));
            effect.setEffectValue(effectJsonObject.getString("Value"));
            effect.setAttackType(effectJsonObject.getString("Attack-Type"));
            effect.setMoveType(move.getTyping());
            move.getMoveEffects().add(effect);
        }

        return move;
    }
}
