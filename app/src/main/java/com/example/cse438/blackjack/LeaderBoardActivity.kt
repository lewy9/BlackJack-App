package com.example.cse438.blackjack

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cse438.blackjack.Score
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.leaderboard_layout.*

class LeaderBoardActivity :Activity()
{
    var loseZero = ArrayList<Score>()
    var loseNonZero = ArrayList<Score>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leaderboard_layout)

    }

    override fun onStart() {
        super.onStart()
        var db = FirebaseFirestore.getInstance()
        val ref = db.collection("users").get()
        if(ref != null) {
            ref.addOnCompleteListener {

                for (document in it.result) {
                    var name = document["nickname"] as String
                    var win = document["wins"] as Long
                    var lose = document["loses"] as Long
                    if (lose == 0L) {
                        loseZero.add(Score(name, win, lose, 0F))
                    } else {
                        loseNonZero.add(Score(name, win, lose, (win / lose.toFloat())))
                    }

                }

                runOnUiThread {
                    var rank = 1
                    var newList1 = loseZero.sortedByDescending { it.win }
                    var newlist2 = loseNonZero.sortedByDescending { it.ratio }
                    for(i in newList1)
                    {
                        var row = TableRow(this)
                        row.setPadding(20,0,20,20)
                        var text = TextView(this)
                        text.text = "     " +rank.toString()
                        text.textSize = 24.0F
                        row.addView(text)
                        var text1 = TextView(this)
                        text1.text = "     " +i.name.toString()
                        text1.textSize = 24.0F
                        row.addView(text1)
                        var text2 = TextView(this)
                        text2.text = "     " +i.win.toString()
                        text2.textSize = 24.0F
                        row.addView(text2)
                        var text3 = TextView(this)
                        text3.text = "     " +i.lose.toString()
                        text3.textSize = 24.0F
                        row.addView(text3)
                        leaderlist.addView(row)
                        rank++
                    }
                    for (j in newlist2)
                    {
                        var row = TableRow(this)
                        row.setPadding(20,0,20,20)
                        var text = TextView(this)
                        text.text = "     " +rank.toString()
                        text.textSize = 24.0F
                        row.addView(text)
                        var text1 = TextView(this)
                        text1.text = "     " +j.name.toString()
                        text1.textSize = 24.0F
                        row.addView(text1)
                        var text2 = TextView(this)
                        text2.text = "     " +j.win.toString()
                        text2.textSize = 24.0F
                        row.addView(text2)
                        var text3 = TextView(this)
                        text3.text = "     " +j.lose.toString()
                        text3.textSize = 24.0F
                        row.addView(text3)
                        leaderlist.addView(row)
                        rank++
                    }

                }
            }
        }

        }
}


