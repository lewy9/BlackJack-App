package com.example.cse438.blackjack

import android.content.Context
import android.media.SoundPool
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class App{
    companion object {
        lateinit  var mAuth: FirebaseAuth
        var user:FirebaseUser? = null


        fun setWinInfo(uid:String){
            var win = 0L
            val db = FirebaseFirestore.getInstance()
            var ref = db.collection("users").document(uid.toString())
            if(ref!=null)
            {
                ref.get().addOnCompleteListener {
                    win = it.result.get("wins") as Long
                    win++
                    val email = it.result.get("email") as String
                    val nickname = it.result.get("nickname") as String
                    val loses = it.result.get("loses") as Long
                    var map:HashMap<String,Any> = HashMap()
                    map["email"] = email
                    map["nickname"] = nickname
                    map["loses"] = loses
                    map["wins"] = win
                    db.collection("users").document(uid.toString()).set(map)
                }
            }
        }



        fun setLoseInfo(uid:String){
            var lose = 0L
            val db = FirebaseFirestore.getInstance()
            var ref = db.collection("users").document(uid.toString())
            if(ref!=null)
            {
                ref.get().addOnCompleteListener {
                    lose = it.result.get("loses") as Long
                    lose++
                    val email = it.result.get("email") as String
                    val nickname = it.result.get("nickname") as String
                    val wins = it.result.get("wins") as Long
                    var map:HashMap<String,Any> = HashMap()
                    map["email"] = email
                    map["nickname"] = nickname
                    map["wins"] = wins
                    map["loses"] = lose
                    db.collection("users").document(uid.toString()).set(map)
                }
            }
        }



        //This part is heavily modified from http://www.java2s.com/Open-Source/Android_Free_Code/App/demo/demo_ashwanik_com_soundpoolmanagerdemoSoundPoolManager_java.htm
        object SoundPoolManager {
            private var sound: HashMap<Int, String> = HashMap()
            var soundIndex: HashMap<String, Int> = HashMap()
            var soundPool: SoundPool = SoundPool.Builder().build()

            init {
                soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                    if (status == 0) {
                        sound[sampleId]?.let {
                            soundIndex.put(it, sampleId)
                        }
                    }
                }
            }

            fun init(context: Context, type: String, rawId: Int) {
                var id = soundPool.load(context, rawId, 1)
                sound.put(id, type);
            }

            fun play(type: String) {
                var id = soundIndex[type]
                id?.let { soundPool.play(id, 1F, 1F, 1, 0, 1F) }
            }

            fun playLoop(type: String) {
                var id = soundIndex[type]
                id?.let { soundPool.play(id, 1F, 1F, 1, 1, 1F) }
            }

            fun stop(type: String) {
                var id = soundIndex[type]
                id?.let { soundPool.stop(id) }
            }
        }


    }
}