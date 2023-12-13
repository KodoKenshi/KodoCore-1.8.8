package me.kodokenshi.kodocore1_8_8.extras

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import java.io.ByteArrayInputStream
import java.util.*

fun String.equalsAny(vararg any: String?, ignoreCase: Boolean = false): Boolean {

    if (any.isEmpty()) return false
    return any.any { it.equals(this, ignoreCase) }

}

inline val String.isValidPlayerNickname get() = length in 3..16 && isAlphanumeric('_')
fun String.toOnlinePlayer() = Bukkit.getPlayer(this)
fun String.toOnlinePlayerNameOrString() = Bukkit.getPlayerExact(this)?.name ?: this

inline val String.isAlphanumeric get() = isAlphanumeric()
fun String.isAlphanumeric(vararg alsoAllowed: Char): Boolean {

    for (c in this)
        if (!c.isLetterOrDigit() && !alsoAllowed.containsIgnoreCase(c))
            return false

    return true

}
inline val String.isNumerical get() = isNumerical()
fun String.isNumerical(vararg alsoAllowed: Char): Boolean {

    for (c in this)
        if (!c.isDigit() && !alsoAllowed.containsIgnoreCase(c))
            return false

    return true

}
inline val String.isAlphabetical get() = isAlphabetical()
fun String.isAlphabetical(vararg alsoAllowed: Char): Boolean {

    for (c in this)
        if (!c.isLetter() && !alsoAllowed.containsIgnoreCase(c))
            return false

    return true

}
fun String.remove(string: String, vararg strings: String, ignoreCase: Boolean = false): String {

    var removed = replace(string, "", ignoreCase = ignoreCase)

    for (loop in strings) removed = removed.replace(loop, "", ignoreCase = ignoreCase)

    return removed

}
inline fun String.replaceFirstCharAsString(transform: (String) -> String) = if (isNotEmpty()) transform("${this[0]}") + substring(1) else this
fun String.removeFirst(string: String, ignoreCase: Boolean = false) = replaceFirst(string, "", ignoreCase)
fun String.removeLast(string: String, ignoreCase: Boolean = false) = replaceLast(string, "", ignoreCase)
fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {

    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    if (index == -1)
        return this

    return substring(0, index) + newValue + substring(index + oldValue.length)

}
fun String.startsWithAny(string: String, vararg any: String, ignoreCase: Boolean = false): Boolean {

    if (startsWith(string, ignoreCase)) return true

    for (s in any) if (startsWith(s, ignoreCase)) return true

    return false

}
fun String.endsWithAny(string: String, vararg any: String, ignoreCase: Boolean = false): Boolean {

    if (endsWith(string, ignoreCase)) return true

    for (s in any) if (endsWith(s, ignoreCase)) return true

    return false

}
fun String.toMaterial() = Material.valueOf(this)
fun String.log(applyColor: Boolean = true, colorChar: Char = '&') =
    log(this, applyColor = applyColor, colorChar = colorChar)
fun String.color(colorChar: Char = '&') = ChatColor.translateAlternateColorCodes(colorChar, this)
fun String.stripColor() = ChatColor.stripColor(this)!!

fun String.decodeToItemStack(): ItemStack? { return ItemStack.deserialize(decodeToClass<Map<String, Any?>>() ?: return null) }
fun String.decodeToLocation(): Location? { return Location.deserialize(decodeToClass<Map<String, Any?>>() ?: return null) }
fun <T> String.decodeToClass(): T? {

    try {

        val bais = ByteArrayInputStream(Base64.getDecoder().decode(this))
        val bois = BukkitObjectInputStream(bais)
        val obj = bois.readObject()
        bois.close()

        return obj as? T?

    } catch (_: Exception) {}

    return null

}