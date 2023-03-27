package org.jsonMapping;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONIterator {
	@SuppressWarnings("unchecked")
	public Object iterateJSON(Object json,Map finalKeyValueMap) throws JSONException {
    	if (json instanceof JSONObject) {
            // iterate through the keys in the JSON object
            JSONObject jsonObj = (JSONObject) json;
            for (String key : jsonObj.keySet()) {
                // recursively call the function for each key value
                iterateJSON(jsonObj.get(key),finalKeyValueMap);
                if(finalKeyValueMap.containsKey(key))
                {
                	jsonObj.put(key, finalKeyValueMap.get(key));	
                }
            }
        } else if (json instanceof JSONArray) {
            // iterate through the items in the JSON array
            JSONArray jsonArray = (JSONArray) json;
            for (int i = 0; i < jsonArray.length(); i++) {
                // recursively call the function for each item in the array
                iterateJSON(jsonArray.get(i),finalKeyValueMap);
            }
        } else {
            // perform some operation on the JSON node (e.g. print its value)
            System.out.println("---"+json);
        }
		return json;
    }
}