package com.example.matheusmaxwellmeireles.borrowme

import org.json.JSONArray
import org.json.JSONObject
import com.google.gson.Gson
import org.json.JSONException

class JSONParser {

    internal var jsonArray = JSONArray()
    internal var jsonClass: String? = null
    companion object {
        fun getJsonArrayFromJsonObject(jsonObject: JSONObject, jsonAttribute: String): JSONArray {
            return jsonObject.getJSONArray(jsonAttribute)
        }

        fun getJsonObjectFromResponse(response: String): JSONObject {
            return JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1))
        }
    }

}
