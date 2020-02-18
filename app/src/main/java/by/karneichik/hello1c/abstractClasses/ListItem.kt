package by.karneichik.hello1c.abstractClasses

abstract class ListItem {
    val TYPE_ON_DELIVERY : Int = 0
    val TYPE_DELVERED: Int = 1
    val TYPE_CANCELED: Int = 2

    abstract fun getType():Int
}