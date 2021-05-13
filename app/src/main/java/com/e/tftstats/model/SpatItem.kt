package com.e.tftstats.model

class SpatItem(name: String, imagePath: Int, item1: Item, item2: Item, origin: Champion.Origin) : FullItem(name, imagePath, item1, item2, true) {
    var origin: Champion.Origin = origin
        private set

}