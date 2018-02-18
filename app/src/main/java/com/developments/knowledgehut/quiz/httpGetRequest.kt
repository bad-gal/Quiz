package com.developments.knowledgehut.quiz

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpGetRequest: AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String?): String {
        val stringUrl = params[0]
        var result: String

        try {
            val url = URL(stringUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.readTimeout = 15000
            connection.connectTimeout = 15000
            connection.connect()

            val streamReader = InputStreamReader(connection.inputStream)
            val bufferReader = BufferedReader(streamReader)
            val stringBuilder = StringBuilder()


            var inputLine = bufferReader.readLine()

            while (inputLine!= null) {
                stringBuilder.append(inputLine)
                inputLine = bufferReader.readLine()
            }

            bufferReader.close()
            streamReader.close()
            result = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            result = ""
        }

        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }
}
