package com.e.tftstats.model

open class FullItem : Item {
    private val item1: Item
    private val item2: Item

    constructor (name: String, imagePath: Int, item1: Item, item2: Item, unique: Boolean = false) : super(name, imagePath, unique) {
        this.item1 = item1
        this.item2 = item2
    }
}