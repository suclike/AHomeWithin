package org.ahomewithin.ahomewithin.models;

import android.util.Log;

import com.google.gson.Gson;

import org.ahomewithin.ahomewithin.parseModel.ParseItem;
import org.ahomewithin.ahomewithin.util.CardContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chezlui on 06/03/16.
 */

@Parcel
public class Item implements Serializable {

    public enum ITEM_TYPE {
        VIDEOS,
        CONVERSATIONS
    }

    public static final String LOG_TAG = Item.class.getSimpleName();
    public static final String SERIALIZABLE_TAG = "item_serializable";

    public String id;
    public ITEM_TYPE type;    // 0: videos, 1: conversation cards
    public String title;
    public String imageUrl;
    public String description;
    public boolean owned;
    public int price;
    public ArrayList<ConversationCard> contentCards;  //
    public String contentUrl; //vidoe url
    public String content;  //card json string, use getCardContentFromJsonString below to deJson


    public static Item fromJson(JSONObject jsonObject, ITEM_TYPE type) {
        Item item = new Item();

        try {
            item.title = jsonObject.has("title") ? jsonObject.getString("title") : "";
            item.imageUrl = jsonObject.has("image_url") ? jsonObject.getString("image_url") : "";  // previously was "image"
            item.description = jsonObject.has("desp") ? jsonObject.getString("desp") : "";
            item.id = jsonObject.has("id") ? jsonObject.getString("id") : "";
            item.type = type;

            item.contentUrl = jsonObject.has("content") ? jsonObject.getString("content") : "";
            item.content = jsonObject.has("content") ? jsonObject.getString("content") : "";
            item.price = jsonObject.has("price") ?
                    Integer.valueOf(jsonObject.getString("price")) : 0;
            item.type = type;
            item.owned = false;

            if (item.type == ITEM_TYPE.VIDEOS) {
                //item.contentU = jsonObject.getJSONObject("content").getString("videoUrl");
            } else {
                item.contentCards = ConversationCard.fromJson(new JSONObject(item.content));

//                JSONObject deckCards = jsonObject.getJSONObject("content");
//                item.contentCards = new ArrayList<>();
//                item.contentCards.add(ConversationCard.fromJson(deckCards.getJSONObject("mind"),
//                        ConversationCard.MIND));
//                item.contentCards.add(ConversationCard.fromJson(deckCards.getJSONObject("body"),
//                        ConversationCard.BODY));
//                item.contentCards.add(ConversationCard.fromJson(deckCards.getJSONObject("heart"),
//                        ConversationCard.HEART));
//                item.contentCards.add(ConversationCard.fromJson(deckCards.getJSONObject("soul"),
//                        ConversationCard.SOUL));
            }
        } catch (JSONException ex) {
            Log.e("Parsing item", ex.toString());
            return null;
        }

        return item;
    }


    public static ArrayList<Item> fromJson(JSONArray jsonArray, ITEM_TYPE type) {
        ArrayList<Item> itemArrayList = new ArrayList<Item>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweet = jsonArray.getJSONObject(i);
                itemArrayList.add(fromJson(tweet, type));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());
            }
        }
        return itemArrayList;
    }


    public static Item getNewInstanceFromParseObject(ParseItem item) {
        Item newItem = new Item();
        newItem.id = item.getObjectId();
        newItem.type = item.getType();
        newItem.title = item.getTitle();
        newItem.description = item.getDesp();
        newItem.price = item.getPrice();
        newItem.imageUrl = item.getImage();
        if (newItem.type == ITEM_TYPE.VIDEOS) { //video
            newItem.contentUrl = item.getContent();
        } else { //cards
            try {
                newItem.content = item.getContentJson().toString();
                newItem.contentCards = ConversationCard.fromJson(new JSONObject(newItem.content));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newItem;
    }

    public static CardContent getCardContentFromJsonString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, CardContent.class);
    }

    public static String getJsonStringFromCardContent(CardContent cardContent) {
        Gson gson = new Gson();
        return gson.toJson(cardContent);
    }
}
