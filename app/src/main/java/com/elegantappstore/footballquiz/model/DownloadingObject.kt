package com.elegantappstore.footballquiz.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DownloadingObject {
    @Throws(IOException::class)
    fun downloadJSONDataFromLink(link: String): String {
        val stringBuilder = StringBuilder()

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val bufferedInputString =
                    BufferedInputStream(urlConnection.inputStream)
            val bufferedReader =
                    BufferedReader(InputStreamReader(bufferedInputString))
            // Temporary string to hold each line read from the BufferedReader

            var inputLine: String?
            inputLine = bufferedReader.readLine()
            while (inputLine != null) {
                stringBuilder.append(inputLine)
                inputLine = bufferedReader.readLine()
            }
        } finally {
            urlConnection.disconnect()
            // Regardless of success of failure this code is going to fire up.  we will disconnect from URL connect
        }
        return  stringBuilder.toString()
    }

    fun downloadFootballerPicture(pictureName: String?) : Bitmap? {
        var bitmap: Bitmap? = null


        val pictureLink = "$ELEGANTAPPSTORE_COM/api/photos/$pictureName"
        val pictureURL = URL(pictureLink)
        val inputStream = pictureURL.openConnection().getInputStream()
        if (inputStream != null ) {
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
        return bitmap
    }

    companion object {
        const val ELEGANTAPPSTORE_COM: String = "http://www.elegantappstore.com"
    }



}