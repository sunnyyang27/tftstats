package com.e.tftstats.model

class SpatItem(name: String, imagePath: Int, origin: Champion.Origin) : Item(name, imagePath, true) {
    var origin: Champion.Origin = origin
        private set
}