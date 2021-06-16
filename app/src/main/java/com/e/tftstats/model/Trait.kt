package com.e.tftstats.model

class Trait(origin: Champion.Origin,  levels: Array<Int>, colors: Array<Int>, imagePath: Int) {
    var origin: Champion.Origin = origin
        private set
    var levels: Array<Int> = levels
        private set
    var colors: Array<Int> = colors
        private set
    var imagePath: Int = imagePath
        private set

    override fun toString(): String {
        return Helper.originName(origin)
    }

    fun traitColor(index: Int): Int {
        return Helper.traitColors[colors[index]]
    }
}