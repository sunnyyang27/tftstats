package com.e.tftstats.model

open class FullItem(name: String, imagePath: Int, private val item1: Item, private val item2: Item, unique: Boolean = false) :
    Item(name, imagePath, unique)