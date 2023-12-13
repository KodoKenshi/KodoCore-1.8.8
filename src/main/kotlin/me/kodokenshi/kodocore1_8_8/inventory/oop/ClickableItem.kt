package me.kodokenshi.kodocore1_8_8.inventory.oop

import me.kodokenshi.kodocore1_8_8.inventory.InventoryType
import me.kodokenshi.kodocore1_8_8.inventory.event.InventoryClickEvent

open class ClickableItem {

    var ignoreCancelled = false

    var cancelClickEventOn = InventoryType.NONE
    var listenClickEventOn = InventoryType.ANY

    internal var clickEvent: InventoryClickEvent.() -> Unit = {}

    fun onClick(clickEvent: InventoryClickEvent.() -> Unit): ClickableItem {
        this.clickEvent = clickEvent
        return this
    }

}