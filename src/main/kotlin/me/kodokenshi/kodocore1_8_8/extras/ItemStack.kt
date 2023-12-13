package me.kodokenshi.kodocore1_8_8.extras

import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

inline fun createItemStack(material: Material = Material.STONE, amount: Int = 1, op: ItemStack.() -> Unit = {}) = ItemStack(material, amount).apply(op)

fun ItemStack.addItemFlag(vararg flags: ItemFlag) = apply { itemMeta = itemMeta?.apply { addItemFlags(*flags) } }
fun ItemStack?.isNotNullOrAir() = this != null && type != Material.AIR
fun ItemStack?.isNullOrAir() = !isNotNullOrAir()
fun ItemStack.isSimilar(
    other: ItemStack,
    considerType: Boolean = true,
    considerAmount: Boolean = false,
    considerDurability: Boolean = true,
    considerEnchantments: Boolean = false,
    considerDisplayName: Boolean = true,
    considerLore: Boolean = true,
): Boolean {

    if (this == other) return true

    val type = if (considerType) type == other.type else true
    val amount = if (considerAmount) amount == other.amount else true
    val durability = if (considerDurability) durability == other.durability else true
    val displayName = if (considerDisplayName) displayName == other.displayName else true

    if (considerEnchantments) {

        val otherEnchantments = other.enchantments.keys

        if (otherEnchantments.size != enchantments.size) return false

        for (enchantment in enchantments.keys)
            if (otherEnchantments.find { it.name != enchantment.name } == null)
                return false

    }
    if (considerLore) {

        val otherLore = other.lore
        for ((index, line) in lore.withIndex())
            if (otherLore[index] != line)
                return false

    }

    return type && amount && durability && displayName

}
fun ItemStack.hasEnchantment(enchantment: Enchantment) = containsEnchantment(enchantment)
inline var ItemStack.displayName get() = if (hasDisplayName) itemMeta?.displayName!! else "null"
    set(displayName) {
        itemMeta?.apply {
            setDisplayName(displayName)
            this@displayName.itemMeta = this
        }
    }
inline val ItemStack.hasDisplayName get() = itemMeta?.hasDisplayName() == true
inline var ItemStack.material get() = type; set(material) { type = material }
fun ItemStack.removeLore(line: String) { lore = lore.toMutableList().apply { remove(line) } }
fun ItemStack.addLore(vararg line: String) { lore = lore.apply { addAll(line) } }
inline var ItemStack.lore: MutableList<String> get() = if (hasLore) itemMeta?.lore!! else mutableListOf()
    set(lore) {
        itemMeta?.apply {
            setLore(lore)
            this@lore.itemMeta = this
        }
    }
inline val ItemStack.hasLore get() = itemMeta?.hasLore() == true
inline val ItemStack.maxAmount get() = material.maxStackSize.takeIf { it > 0 } ?: 1
fun ItemStack.isAmountInBounds(amount: Int) = amount in 1..maxAmount
inline val ItemStack.isCreatureSpawner get() = material == Material.MOB_SPAWNER
inline var ItemStack.spawnedType: EntityType? get() {
    return if (isCreatureSpawner) {

        val blockStateMeta = (itemMeta as BlockStateMeta)
        if (blockStateMeta.hasBlockState()) (blockStateMeta.blockState as CreatureSpawner).spawnedType
        else null

    } else null
} set(spawnedType) {

        if (!isCreatureSpawner || spawnedType == null) return

        itemMeta = (itemMeta as? BlockStateMeta)?.apply { blockState = (blockState as CreatureSpawner).apply { this.spawnedType = spawnedType } } ?: return

    }
