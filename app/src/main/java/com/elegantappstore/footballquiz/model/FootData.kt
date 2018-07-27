package com.elegantappstore.footballquiz.model

class FootData (var nickname: String, var position: String, var cultivar: String, var playerFullname: String,
                var pictureName: String, var description: String, var difficulty: Int, var id: Int = 0) {

    constructor() : this("","",
            "","",
            "","",
            0,0)

    override fun toString(): String {
        return playerFullname

    }
}