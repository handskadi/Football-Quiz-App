package com.elegantappstore.footballquiz.model

import org.json.JSONArray
import org.json.JSONObject

class ParseFootballerUtility {

    fun parseFootballerObjectFromJSONData() : List<FootData>?{

        val allFootballerObjects: ArrayList<FootData> = ArrayList()
        val downloadingObject = DownloadingObject()
        val topLevelFootballerJSONData = downloadingObject.
                downloadJSONDataFromLink("http://www.elegantappstore.com/api/flashcard.txt")
        val topLevelFootballerJSONObject = JSONObject(topLevelFootballerJSONData)
        val footballerObjectArray: JSONArray = topLevelFootballerJSONObject.getJSONArray("values")
        var index = 0

        while (index < footballerObjectArray.length()){
            val footballerObject = FootData()
            val jsonObject = footballerObjectArray.getJSONObject(index)

            with(jsonObject) {
                footballerObject.nickname = getString("nickname")
                footballerObject.position = getString("position")
                footballerObject.cultivar = getString("cultivar")
                footballerObject.playerFullname = getString("player_fullname")
                footballerObject.pictureName = getString("picture_name")
                footballerObject.description = getString("description")
                footballerObject.difficulty = getInt("difficulty")
                footballerObject.id = getInt("id")
            }
            allFootballerObjects.add(footballerObject)
            index++
        }

        return allFootballerObjects
    }

}