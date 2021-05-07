package com.e.tftstats.model

open class Item(name: String, imagePath: Int, unique: Boolean = false) {
    companion object {
        private var currentId : Int = 0
    }

    var name: String = name
        private set
    var id: Int
        private set
    var unique: Boolean = unique
        private set
    var imagePath: Int = imagePath
        private set

    init {
        this.id = currentId
        currentId++
    }


    override fun toString(): String {
        return name
    }
}
