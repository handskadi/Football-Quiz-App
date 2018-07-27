package com.elegantappstore.footballquiz.controller

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings.*
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.elegantappstore.footballquiz.model.DownloadingObject
import com.elegantappstore.footballquiz.model.FootData
import com.elegantappstore.footballquiz.model.ParseFootballerUtility
import com.elegantappstore.footballquiz.R

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var imageTaken: ImageView? = null

    // Instance variables
    var correctAnswerIndex: Int = 0
    var correctFootballer: FootData? = null
    private var numberOfTimesUserAnsweredCorrectly: Int = 0
    private var numberOfTimesUserAnsweredIncorrectly: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setProgressBar(false)
        displayUIWidgets(false)
        YoYo.with(Techniques.Pulse)
                .duration(700)
                .repeat(5)
                .playOn(btnNextFootballer)

        imageTaken = findViewById(R.id.imgTaken)

        // See the next Player
        btnNextFootballer.setOnClickListener {
            if(checkForInternetConnection()) {
                setProgressBar(true)
                try {
                    val innerClassObject = DownloadingFootballTask()
                    innerClassObject.execute()
                } catch (e: Exception){
                    e.printStackTrace()

                }

                val gradientColors = IntArray(2)
                gradientColors[0] = Color.parseColor("#fcd204")
                gradientColors[1] = Color.parseColor("#a10000")
                val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,gradientColors)
                gradientDrawable.cornerRadius = dipToFloat(this,3f)
                gradientDrawable.setStroke(4, Color.parseColor("#b1b0b0"))

                button1.background = gradientDrawable
                button2.background = gradientDrawable
                button3.background = gradientDrawable
                button4.background = gradientDrawable
                textViewAnswer.text = ""
            }
        }
        // On Button Clicked!
        button1.setOnClickListener { specifyTheRightAndWrongAnswer(0) }
        button2.setOnClickListener { specifyTheRightAndWrongAnswer(1) }
        button3.setOnClickListener { specifyTheRightAndWrongAnswer(2) }
        button4.setOnClickListener { specifyTheRightAndWrongAnswer(3) }
    }

    // Convert Dip to Float
    private fun dipToFloat(context: Context, dpsValue: Float): Float {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpsValue, metrics)
    }


    override fun onResume() {
        super.onResume()
        checkForInternetConnection()
    }


    @SuppressLint("StaticFieldLeak")
    inner class DownloadingFootballTask: AsyncTask<String,Int, List<FootData>>(){
        override fun doInBackground(vararg params: String?): List<FootData>? {
            // can only ACCESS background thread. Not user interface thread
            try {
                val parseFootballer = ParseFootballerUtility()
                return parseFootballer.parseFootballerObjectFromJSONData()
            } catch (e: Exception){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: List<FootData>?) {
            super.onPostExecute(result)
            // can access user interface thread
            val numberOfFootballers = result?.size ?: 0

            if(numberOfFootballers > 0){
                val randomFootballerIndexForButton1: Int = (Math.random() * result!!.size).toInt()
                val randomFootballerIndexForButton2: Int = (Math.random() * result.size).toInt()
                val randomFootballerIndexForButton3: Int = (Math.random() * result.size).toInt()
                val randomFootballerIndexForButton4: Int = (Math.random() * result.size).toInt()

                val allRandomFootballer = ArrayList<FootData>()
                allRandomFootballer.add(result[randomFootballerIndexForButton1])
                allRandomFootballer.add(result[randomFootballerIndexForButton2])
                allRandomFootballer.add(result[randomFootballerIndexForButton3])
                allRandomFootballer.add(result[randomFootballerIndexForButton4])

                button1.text = result[randomFootballerIndexForButton1].toString()
                button2.text = result[randomFootballerIndexForButton2].toString()
                button3.text = result[randomFootballerIndexForButton3].toString()
                button4.text = result[randomFootballerIndexForButton4].toString()

                correctAnswerIndex = (Math.random() * allRandomFootballer.size).toInt()
                correctFootballer = allRandomFootballer[correctAnswerIndex]

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomFootballer[correctAnswerIndex].pictureName)
            } else {
                Toast.makeText(this@MainActivity, "Oops.. You\'re offline",
                        Toast.LENGTH_LONG).show()
                setProgressBar(false)
            }


        }

    }


    // Check for internet connection
    private fun checkForInternetConnection(): Boolean {
        val connectivityManager: ConnectivityManager = this.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null &&  networkInfo.isConnectedOrConnecting
        return if (isDeviceConnectedToInternet){
            true
        } else {
            createAlert()
            false
        }
    }

    private fun createAlert(){
        val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Please check for internet connection")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK") {
            _: DialogInterface?, _: Int ->
            startActivity(Intent(ACTION_SETTINGS))
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") {
            _: DialogInterface?, _: Int ->
            Toast.makeText(this@MainActivity,
                    "You must be connected to internet", Toast.LENGTH_LONG).show()
            finish()
        }

        alertDialog.show()
    }

    // Specify the wrong or right answer
    private fun specifyTheRightAndWrongAnswer(userGuess: Int){
        when(correctAnswerIndex){
            0 -> button1.setBackgroundColor(Color.GREEN)
            1 -> button2.setBackgroundColor(Color.GREEN)
            2 -> button3.setBackgroundColor(Color.GREEN)
            3 -> button4.setBackgroundColor(Color.GREEN)
        }

        if (userGuess == correctAnswerIndex){
            textViewAnswer.text = getString(R.string.rightAnswer)
            this.numberOfTimesUserAnsweredCorrectly++
            txtRightAnswers.text = "$numberOfTimesUserAnsweredCorrectly"
        } else {
            val correctFootballerName = correctFootballer.toString()
            textViewAnswer.text = getString(R.string.wrongAnswer).plus(correctFootballerName)

            numberOfTimesUserAnsweredIncorrectly++
            txtWrongAnswers.text = "$numberOfTimesUserAnsweredIncorrectly"
        }
    }

    // Downloading Image Process
    @SuppressLint("StaticFieldLeak")
    inner  class  DownloadingImageTask: AsyncTask<String, Int, Bitmap?>(){
        override fun doInBackground(vararg pictureName: String?): Bitmap? {
            try {
                val downloadingObject = DownloadingObject()
                return downloadingObject.downloadFootballerPicture(pictureName[0])
            } catch (e: Exception){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            setProgressBar(false)
            displayUIWidgets(true)
            playAnimationOnView(imgTaken, Techniques.Tada )
            playAnimationOnView(button1, Techniques.RollIn )
            playAnimationOnView(button2, Techniques.RollIn )
            playAnimationOnView(button3, Techniques.RollIn )
            playAnimationOnView(button4, Techniques.RollIn )
            playAnimationOnView(textViewAnswer, Techniques.Swing )
            playAnimationOnView(txtWrongAnswers, Techniques.FlipInX )
            playAnimationOnView(txtRightAnswers, Techniques.Landing )
            imageTaken?.setImageBitmap(result)
        }
    }

    private fun setProgressBar(show: Boolean){
        if (show){
            progressbarLayout.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            btnNextFootballer.visibility = View.INVISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        }else if (!show){
            progressbarLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
            btnNextFootballer.visibility = View.VISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    // set the visibility of UI Widgets
    private fun displayUIWidgets(display: Boolean){
        if(display){
            imgTaken.visibility = View.VISIBLE
            button1.visibility = View.VISIBLE
            button2.visibility = View.VISIBLE
            button3.visibility = View.VISIBLE
            button4.visibility = View.VISIBLE
            textViewAnswer.visibility = View.VISIBLE
            txtRightAnswers.visibility = View.VISIBLE
            txtWrongAnswers.visibility = View.VISIBLE
        }else{
            imgTaken.visibility = View.INVISIBLE
            button1.visibility = View.INVISIBLE
            button2.visibility = View.INVISIBLE
            button3.visibility = View.INVISIBLE
            button4.visibility = View.INVISIBLE
            textViewAnswer.visibility = View.INVISIBLE
            txtRightAnswers.visibility = View.INVISIBLE
            txtWrongAnswers.visibility = View.INVISIBLE
        }

    }

    // Playing Animation
    private fun playAnimationOnView(view: View, technique: Techniques){
        YoYo.with(technique)
                .duration(700)
                .repeat(0)
                .playOn(view)
    }

}
