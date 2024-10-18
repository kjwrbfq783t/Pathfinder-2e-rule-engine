package com.posilcorp.Utilities;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.text.similarity.LevenshteinDistance;

import com.posilcorp.Character;
import com.posilcorp.ObjectYouCanSpeakTo;
import com.posilcorp.Scene;
import com.posilcorp.EquipmentLogic.Item;
import com.posilcorp.EquipmentLogic.ObjectWithInventory;

public class Levenshtein {
    public static ObjectYouCanSpeakTo fetchObjectYouCanSpeakTo(String text,
            HashMap<String, ObjectYouCanSpeakTo> ObjectYouCanSpeakTos) throws Exception {
        String matched = null;
        Integer best_score = null;
        for (String fetched_name : ObjectYouCanSpeakTos.keySet()) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
                matched = fetched_name;
            }
            int new_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
            if (new_score < best_score) {
                matched = fetched_name;
                best_score = new_score;
            }
        }
        if (ObjectYouCanSpeakTos.get(matched) == null)
            throw new Exception("oggeto non trovato..");
        return ObjectYouCanSpeakTos.get(matched);
    }

    public static ObjectWithInventory fetchObjectWithInventory(String text,
            HashMap<String, ObjectWithInventory> ObjectWithInventorys) throws Exception {
        String matched = null;
        Integer best_score = null;
        for (String fetched_name : ObjectWithInventorys.keySet()) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
                matched = fetched_name;
            }
            int new_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
            if (new_score < best_score) {
                matched = fetched_name;
                best_score = new_score;
            }
        }
        if (ObjectWithInventorys.get(matched) == null)
            throw new Exception("oggeto non trovato..");
        return ObjectWithInventorys.get(matched);
    }

    public static Item fetchItem(String text,
            HashMap<String,? extends Item> Items) throws Exception {
        String matched = null;
        Integer best_score = null;
        for (String fetched_name : Items.keySet()) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
                matched = fetched_name;
            }
            int new_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
            if (new_score < best_score) {
                matched = fetched_name;
                best_score = new_score;
            }
        }
        if (Items.get(matched) == null)
            throw new Exception("oggeto non trovato..");

        return Items.get(matched);
    }

    public static Character fetchCharacter(String text, HashMap<String, Character> Characters) throws Exception {
        String matched = null;
        Integer best_score = null;
        for (String fetched_name : Characters.keySet()) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
                matched = fetched_name;
            }
            int new_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
            if (new_score < best_score) {
                matched = fetched_name;
                best_score = new_score;
            }
        }
        if (Characters.get(matched) == null)
            throw new Exception("oggeto non trovato..");

        return Characters.get(matched);
    }

    public static Item fetchItem(String text, Collection<? extends Item> Items) throws Exception {
        Integer best_score = null;
        Item matched_item = null;

        for (Item fetched_Item : Items) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_Item.getName());
                matched_item = fetched_Item;
            }
            int new_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_Item.getName());
            if (new_score < best_score) {
                matched_item = fetched_Item;
                best_score = new_score;
            }
        }
        if (matched_item == null)
            throw new Exception("oggeto non trovato..");
        return matched_item;
    }

    public static Scene fetchScenes(String text, HashMap<String, Scene> Scenes) throws Exception {
        String matched = null;
        Integer best_score = null;
        for (String fetched_name : Scenes.keySet()) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
                matched = fetched_name;
                continue;
            }
            int new_score = LevenshteinDistance.getDefaultInstance().apply(text, fetched_name);
            if (new_score < best_score) {
                matched = fetched_name;
                best_score = new_score;
            }
        }
        if (Scenes.get(matched) == null)
            throw new Exception("oggeto non trovato..");
        return Scenes.get(matched);
    }

}
