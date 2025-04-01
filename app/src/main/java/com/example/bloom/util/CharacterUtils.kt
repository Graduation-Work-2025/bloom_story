package com.example.bloom.util

import com.example.bloom.R

fun getCharacterResId(id: Int): Int {
    return when (id) {
        1 -> R.drawable.character1
        2 -> R.drawable.character2
        3 -> R.drawable.character3
        4 -> R.drawable.character4
        5 -> R.drawable.character5
        6 -> R.drawable.character6
        7 -> R.drawable.character7
        8 -> R.drawable.character8
        else -> R.drawable.character1
    }
}
