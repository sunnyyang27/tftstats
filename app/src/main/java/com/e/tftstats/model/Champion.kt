package com.e.tftstats.model

// Create champs locally on startup, persist player's champs in DB
class Champion(name: String, origin1: Origin, origin2: Origin, cost: Int, imagePath: Int, origin3: Origin = Origin.NONE) {

    enum class Origin {
        ABOMINATION,
        DAWNBRINGER,
        DRACONIC,
        FORGOTTEN,
        HELLION,
        INANIMATE,
        IRONCLAD,
        NIGHTBRINGER,
        REDEEMED,
        REVENANT,
        SENTINEL,
        VICTORIOUS,
        ASSASSIN,
        BRAWLER,
        CARETAKER,
        CAVALIER,
        CANNONEER,
        CRUEL,
        INVOKER,
        KNIGHT,
        LEGIONNAIRE,
        MYSTIC,
        RANGER,
        RENEWER,
        SKIRMISHER,
        SPELLWEAVER,
        NONE
    }
    companion object {
        private var currentId : Int = 0
    }

    var id: Int = 0
        private set
    var name: String = name
        private set

    var origins: MutableList<Origin> = ArrayList()
        private set
    var cost: Int = cost
        private set
    var imagePath: Int = imagePath
        private set

    init {
        origins.add(origin1)
        origins.add(origin2)
        if (origin3 != Origin.NONE)
            origins.add(origin3)
        this.id = currentId
        currentId++
    }

    override fun toString(): String {
        return name
    }
}