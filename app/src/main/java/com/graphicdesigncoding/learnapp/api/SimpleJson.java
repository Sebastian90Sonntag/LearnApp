package com.graphicdesigncoding.learnapp.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleJson {

    public JSONObject Decode(String str){

        JSONObject _obj;

        try {
            _obj = new JSONObject(str);
            return _obj;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public Object Get(JSONObject obj,String name){

        Object _obj;
        try {
            _obj = obj.get(name);
            return _obj;
        } catch (JSONException e) {
            return null;
        }

    }

    public JSONArray GetArray(JSONObject obj, String name){

        JSONArray _obj;
        try {
            _obj = obj.getJSONArray(name);
            return _obj;
        } catch (JSONException e) {
            return new JSONArray();
        }

    }

    public JSONObject GetObjectInJArray(JSONArray obj, int index){

        JSONObject _obj;
        try {
            _obj = obj.getJSONObject(index);
            return _obj;
        } catch (JSONException e) {
            return new JSONObject();
        }

    }

}
