package com.jeandavid.tensorflowpaises

import java.io.Serializable

class Country : Serializable {
    lateinit var name: String
    lateinit var nameCapital: String
    lateinit var codeISO2: String
    lateinit var codeISONum: String
    lateinit var codeISO3: String
    lateinit var codeFIPS: String
    lateinit var telPrefix: String
    lateinit var center0: String
    lateinit var center1: String
    var geoWest = 0.0
    var geoEast = 0.0
    var geoNorth = 0.0
    var geoSouth = 0.0
    lateinit var linkban: String

    constructor() {}
    constructor(
        name: String,
        nameCapital: String,
        codeISO2: String,
        codeISONum: String,
        codeISO3: String,
        codeFIPS: String,
        telPrefix: String,
        center0: String,
        center1: String,
        geoWest: Double,
        geoEast: Double,
        geoNorth: Double,
        geoSouth: Double,
        linkban: String
    ) {
        this.name = name
        this.nameCapital = nameCapital
        this.codeISO2 = codeISO2
        this.codeISONum = codeISONum
        this.codeISO3 = codeISO3
        this.codeFIPS = codeFIPS
        this.telPrefix = telPrefix
        this.center0 = center0
        this.center1 = center1
        this.geoWest = geoWest
        this.geoEast = geoEast
        this.geoNorth = geoNorth
        this.geoSouth = geoSouth
        this.linkban = linkban
    }
}
