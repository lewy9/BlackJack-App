package com.example.cse438.blackjack

import java.util.*
import kotlin.collections.ArrayList

class Rules{

    companion object {

        lateinit  var usersCard:ArrayList<Int>
        lateinit  var dealerCard :ArrayList<Int>
        lateinit var cardList:ArrayList<Int>

        fun isWin(deck: ArrayList<Int>): Boolean {
            var aCount = 0
            var sum = 0
            for (i in deck) {
                if(i > 10)
                {
                    sum = sum + 10
                }
                else if( i==1)
                {
                    aCount++
                }
                else
                {
                    sum = sum + i
                }
            }

            return sum+aCount*11>=21 && sum+aCount <=21
        }

        fun isBust(deck: ArrayList<Int>):Boolean{
            var aCount = 0;
            var sum = 0
            for (i in deck) {
                if(i > 10)
                {
                    sum = sum + 10
                }
                else if( i==1)
                {
                    aCount++
                }
                else
                {
                    sum = sum + i
                }
            }
            var bust = (sum +aCount*1) > 21

            return bust
        }

        fun dealerHit(deck: ArrayList<Int>):Boolean{
            var aCount = 0
            var sum = 0
            for (i in deck) {
                if(i > 10)
                {
                    sum = sum + 10
                }
                else if( i==1)
                {
                    aCount++
                }
                else
                {
                    sum = sum + i
                }
            }
            var hit = (sum + aCount*11) <17

            return hit
        }

        fun getPoints(cards:ArrayList<Int>):Int
        {
            var sum = 0
            var aCount = 0
            for(i in cards)
            {
                if(i>10)
                {
                    sum = sum + 10
                }
                else if (i == 1)
                {
                    aCount++
                }
                else
                {
                    sum = sum+i
                }
            }
            return sum+aCount*11
        }
    }
}