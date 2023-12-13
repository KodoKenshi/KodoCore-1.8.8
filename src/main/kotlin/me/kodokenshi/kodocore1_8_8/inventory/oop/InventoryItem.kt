package me.kodokenshi.kodocore1_8_8.inventory.oop

import me.kodokenshi.kodocore1_8_8.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InventoryItem(
    inventory: Inventory,
    slot: Int,
    itemStack: ItemStack
): ClickableItem() {

    var id = ""
    var userData: Any? = null

    var inventory: Inventory? = inventory
        private set(inventory) {

            if (slot != -1) field?.inventory?.clear(slot)

            field = inventory

            itemStack = itemStack

        }
    var slot = slot
        set(slot) {

            if (field != -1) inventory?.inventory?.clear(field)

            field = slot

            itemStack = itemStack

        }

    var itemStack = itemStack
        set(itemStack) {

            field = itemStack

            if (slot != -1) {

                val atSlot = inventory?.items?.getOrNull(slot)
                if (atSlot != this) {

                    atSlot?.slot = -1
                    inventory?.inventory?.clear(slot)

                }

                inventory?.inventory?.setItem(slot, itemStack)

            }

        }

}