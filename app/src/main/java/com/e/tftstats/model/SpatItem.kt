package com.e.tftstats.model

class SpatItem : FullItem {
    var origin: Champion.Origin
        private set

    constructor (name: String, imagePath: Int, item1: Item, item2: Item, origin: Champion.Origin) : super(name, imagePath, item1, item2, true) {
        this.origin = origin
    }
}