package com.example.cse438.blackjack

enum class MusicType(val resId: Int) {
    ShuffleSingle(R.raw.sound_shuffle_single),
    Win(R.raw.sound_win),
    Background(R.raw.sound_background),
    Pressed(R.raw.sound_pressed),
    Lost(R.raw.sound_lost),
    Draw(R.raw.sound_draw)
}