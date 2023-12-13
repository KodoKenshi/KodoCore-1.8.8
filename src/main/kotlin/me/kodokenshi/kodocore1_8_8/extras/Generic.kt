package me.kodokenshi.kodocore1_8_8.extras

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayOutputStream
import java.util.*

inline fun <reified T : Enum<T>> enumValueOfOrElse(value: String?, orElse: T) = enumValues<T>().find { it.name == value } ?: orElse

fun <T> log(vararg any: T, applyColor: Boolean = true, colorChar: Char = '&') = Bukkit.getConsoleSender().sendMessage((
        if (applyColor) any.map { it.toString().color(colorChar) }
        else any.map { it.toString() }
        ).toTypedArray())
inline fun <T> T.matches(op: (T) -> Boolean) = op(this)

fun <T> T.encodeToString(): String? {

    try {

        val baos = ByteArrayOutputStream()
        val boos = BukkitObjectOutputStream(baos)
        boos.writeObject(when (this) {
            is ItemStack -> serialize()
            is Location -> serialize()
            else -> this
        })
        boos.close()

        return Base64.getEncoder().encodeToString(baos.toByteArray())

    } catch (_: Exception) {}

    return null

}