package oupson.apng.exceptions

class BadCRCException : Exception()

// TODO BETTER MESSAGES
class BadApngException(override val message: String? = null) : Exception()


class BadBitmapsDiffSize(firstBitmapWidth : Int, firstBitmapHeight : Int, secondBitmapWidth : Int, secondBitmapHeight : Int) : Exception() {
    override val message: String = "${firstBitmapWidth}x${firstBitmapHeight} must be bigger than or equal to ${secondBitmapWidth}x${secondBitmapHeight}"
}

class BadParameterException(message : String) : Exception(message)