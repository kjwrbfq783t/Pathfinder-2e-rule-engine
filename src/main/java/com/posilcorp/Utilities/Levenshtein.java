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
            HashMap<String, ObjectYouCanSpeakTo> ObjectYouCanSpeakTos) {
        String matched = "";
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
        return ObjectYouCanSpeakTos.get(matched);
    }

    public static ObjectWithInventory fetchObjectWithInventory(String text,
            HashMap<String, ObjectWithInventory> ObjectWithInventorys) {
        String matched = "";
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
        return ObjectWithInventorys.get(matched);
    }


    public static Item fetchItem(String text,
            HashMap<String, Item> Items) {
        String matched = "";
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
        return Items.get(matched);
    }

    public static Character fetchCharacter(String text, HashMap<String, Character> Characters) {
        String matched = "";
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
        return Characters.get(matched);
    }


    public static Item fetchItem(String text, Collection<Item> Items) {
        String matched = "";
        Integer best_score = null;
        Item matched_item=null;


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
        return matched_item;
    }

    public static Scene fetchScenes(String text, HashMap<String, Scene> Scenes) {
        String matched = "";
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
        return Scenes.get(matched);
    }

}
