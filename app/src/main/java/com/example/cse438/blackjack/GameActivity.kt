package com.example.cse438.blackjack


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Contacts
import android.provider.Contacts.Intents.UI
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.cse438.blackjack.util.CardRandomizer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.game_layout.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.experimental.CoroutineContext

class GameActivity : Activity(){

    var color = arrayOf("clubs", "diamonds","hearts","spades")
    val rand = Random()
    private lateinit var mDetector: GestureDetectorCompat
    var  marginPx:Int = 0
    val musicArray = arrayOf(MusicType.ShuffleSingle, MusicType.Win, MusicType.Background, MusicType.Pressed, MusicType.Lost, MusicType.Draw)
    var win=0L
    var lose = 0L
    var musicManager:MusicManager? = MusicManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (musicType in musicArray) {
            App.Companion.SoundPoolManager.init(this, musicType.name, musicType.resId)
        }
        marginPx = (resources.displayMetrics.density * 25).toInt()
        mDetector = GestureDetectorCompat(this, MyGestureListener())
        setContentView(R.layout.game_layout)
        musicManager?.play(applicationContext, R.raw.sound_background, true)
        leaderBoard.setOnClickListener {
            startActivity(Intent(this,LeaderBoardActivity::class.java))
        }
        signOut.setOnClickListener {
            App.user = null
            finish()
        }
        start(this)
    }

    override fun onStart() {
        super.onStart()
        val db = FirebaseFirestore.getInstance()
        var ref = db.collection("users").document(App.user!!.uid)
        if(ref!=null)
        {
            ref.get().addOnCompleteListener {
                 win = it.result.get("wins") as Long
                 lose = it.result.get("loses") as Long
                wl.text = "W/L  "+win.toString()+"/"+lose.toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        musicManager?.start()
    }

    override fun onPause() {
        super.onPause()
        musicManager?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicManager?.stop()
    }

    override fun onTouchEvent(event: MotionEvent) : Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }


    fun getResoucesId(num:Int):Int
    {
        return resources.getIdentifier(color[rand.nextInt(4)]+num.toString(), "drawable", packageName)
    }

    fun updateScore()
    {
        wl.text = "W/L  "+win.toString()+"/"+lose.toString()
    }


    fun start(context: Context)
    {
        updateScore()
        var temp = Array(52,{i -> (i%13)+1 })
        Rules.cardList = temp.toCollection(ArrayList<Int>())
        Rules.usersCard = arrayListOf(Rules.cardList[rand.nextInt(52)], Rules.cardList[rand.nextInt(52)])
        Rules.dealerCard = arrayListOf(Rules.cardList[rand.nextInt(52)], Rules.cardList[rand.nextInt(52)])
        setDealerCard(Rules.dealerCard,dealerCard)
        setPlayerCard(Rules.usersCard,playerCard)
    }

    fun setPlayerCard(cards:ArrayList<Int>,viewGroup:ViewGroup)
    {
        viewGroup.removeAllViews()
        for ( i in 0 until cards.size)
        {
            var image = ImageView(this)
            var id = getResoucesId(cards[i])
            var drawableResource = resources.getDrawable(id, null)
            image.setImageDrawable(drawableResource)
            viewGroup.addView(image)
            image.layoutParams.width=210
            image.layoutParams.height=285
        }
    }

    fun setDealerCard(cards:ArrayList<Int>, viewGroup: ViewGroup)
    {
        viewGroup.removeAllViews()
        var back = ImageView(this)
        var back_drawable = this.getDrawable(R.drawable.back)
        back.setImageDrawable(back_drawable)
        viewGroup.addView(back)
        back.layoutParams.width = 210
        back.layoutParams.height = 285
        for ( i in 1 until cards.size)
        {
            var image = ImageView(this)
            val id = getResoucesId(cards[i])
            var drawableResource = this.getDrawable(id)
            image.setImageDrawable(drawableResource)
            viewGroup.addView(image)
            image.layoutParams.width=210
            image.layoutParams.height=285
        }
    }


    fun addCardView(card:Int,viewGroup: ViewGroup,callback:View.OnClickListener)
    {
        var image = ImageView(this)
        var drawableResource = this.getDrawable(getResoucesId(card))
        image.setImageDrawable(drawableResource)
        image.visibility = View.GONE
        viewGroup.addView(image)
        image.layoutParams.width=210
        image.layoutParams.height=285
        addCardAnimation(image,getTopMargin(viewGroup).toFloat(),callback)
    }


    fun hit()
    {
        App.Companion.SoundPoolManager.play(MusicType.Pressed.name)
        var card = rand.nextInt(13)+1
        Rules.usersCard.add(card)
        addCardView(card,playerCard,View.OnClickListener { checkPlayer(Rules.usersCard) })
    }

    fun checkPlayer(cards: ArrayList<Int>)
    {
        if(Rules.isBust(Rules.usersCard))
        {
            Toast.makeText(this,"Player bust!", Toast.LENGTH_SHORT).show()
            App.setLoseInfo(App.user!!.uid)
            lose++
            App.Companion.SoundPoolManager.play(MusicType.Lost.name)
            start(this)
        }
        if(Rules.isWin(Rules.usersCard))
        {
            Toast.makeText(this,"Player win!", Toast.LENGTH_SHORT).show()
            App.setWinInfo(App.user!!.uid)
            App.Companion.SoundPoolManager.play(MusicType.Win.name)
            win++
            start(this)
        }
        return
    }

    fun checkDealer(cards: ArrayList<Int>)
    {
        if(Rules.isBust(Rules.dealerCard))
        {
            Toast.makeText(this,"Dealer bust!", Toast.LENGTH_SHORT).show()
            App.setWinInfo(App.user!!.uid)
            win++
            App.Companion.SoundPoolManager.play(MusicType.Win.name)
            start(this)
        }
        else if(Rules.isWin(Rules.dealerCard))
        {
            Toast.makeText(this,"Dealer win!", Toast.LENGTH_SHORT).show()
            App.setLoseInfo(App.user!!.uid)
            lose++
            App.Companion.SoundPoolManager.play(MusicType.Lost.name)
            start(this)
        }
        else
        {
            checkWinner()
        }
    }

    fun stand()
    {
        if (Rules.dealerHit(Rules.dealerCard)) {
            var card3 = rand.nextInt(13)+1
            Rules.dealerCard.add(card3)
            addCardView(card3,dealerCard,View.OnClickListener { stand() })
            Toast.makeText(this,"Dealer hit",Toast.LENGTH_SHORT).show()
        }
        else
        {
            checkDealer(Rules.dealerCard)
        }
    }

    fun checkWinner()
    {
        val dealerPoint = Rules.getPoints(Rules.dealerCard)
        val playerPoint = Rules.getPoints(Rules.usersCard)
        if(dealerPoint>playerPoint)
        {
            Toast.makeText(this,"Dealer win1!", Toast.LENGTH_SHORT).show()
            App.setLoseInfo(App.user!!.uid)
            lose++
            App.Companion.SoundPoolManager.play(MusicType.Lost.name)
        }
        else if(dealerPoint < playerPoint)
        {
            Toast.makeText(this,"Player win!", Toast.LENGTH_SHORT).show()
            App.setWinInfo(App.user!!.uid)
            win++
            App.Companion.SoundPoolManager.play(MusicType.Win.name)
        }
        else
        {
            Toast.makeText(this,"Tie Game!", Toast.LENGTH_SHORT).show()
            App.Companion.SoundPoolManager.play(MusicType.Draw.name)
        }
        start(this)
    }


    fun hide(marginTop: Float): Animation {
        return TranslateAnimation(TranslateAnimation.ABSOLUTE, 0F, TranslateAnimation.ABSOLUTE, 0F,
            TranslateAnimation.RELATIVE_TO_SELF, 0F, TranslateAnimation.ABSOLUTE, -marginTop)
    }

    fun show(marginTop: Float): Animation {
        return TranslateAnimation(TranslateAnimation.ABSOLUTE, 0F, TranslateAnimation.ABSOLUTE, 0F,
            TranslateAnimation.ABSOLUTE, -marginTop, TranslateAnimation.RELATIVE_TO_SELF, 0F)
    }

    fun addCardAnimation(view: View, margin: Float,callback: View.OnClickListener?) {
        var hideAnimation = hide(margin)
        hideAnimation.duration = 1000
        hideAnimation.fillAfter = true
        hideAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                var showAnimation = show(margin)
                showAnimation.duration = 2000
                showAnimation.fillAfter = true
                showAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        callback?.onClick(view)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationStart(animation: Animation?) {
                        view.visibility = View.VISIBLE
                    }
                })

                view.startAnimation(showAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
        view.startAnimation(hideAnimation)
    }

    fun getTopMargin(view: View): Int {
        var array = IntArray(2)
        view.getLocationInWindow(array)
        return array[1]
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        private var swipedistance = 150


        override fun onDoubleTap(e: MotionEvent?): Boolean {
            stand()
            return true
        }


        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if(e2.x - e1.x > swipedistance) {
                hit()
                return true
            }
            return false
        }
    }



    inner class MusicManager {
        var mediaPlayer: MediaPlayer = MediaPlayer()

        init {
            mediaPlayer.setOnPreparedListener({
                mediaPlayer.start()
            })

        }

        fun play(context: Context, resId: Int, loop: Boolean) {
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://${context.packageName}/$resId"))
            mediaPlayer.isLooping = loop
            mediaPlayer.prepareAsync()
        }

        fun start() {
            mediaPlayer.start()
        }

        fun pause() {
            mediaPlayer.pause();
        }

        fun stop() {
            mediaPlayer.stop()
        }
    }

}