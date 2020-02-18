package by.karneichik.hello1c.abstractClasses


class OnDelivery : ListItem() {

    override fun getType(): Int {
        return TYPE_ON_DELIVERY
    }
}

class Delivered : ListItem() {

    override fun getType(): Int {
        return TYPE_DELVERED
    }
}

class Canceled : ListItem() {

    override fun getType(): Int {
        return TYPE_CANCELED
    }
}