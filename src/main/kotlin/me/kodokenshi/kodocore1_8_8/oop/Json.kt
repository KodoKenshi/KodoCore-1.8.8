package me.kodokenshi.kodocore1_8_8.oop

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.kodokenshi.kodocore1_8_8.extras.decodeToItemStack
import me.kodokenshi.kodocore1_8_8.extras.decodeToLocation
import me.kodokenshi.kodocore1_8_8.extras.encodeToString
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import java.io.Serializable

inline fun json(block: Json.() -> Unit) = Json().apply(block)
inline fun buildJson(block: Json.() -> Unit) = Json().apply(block).build()
fun String.toJson() = Json(this)
fun String.toJsonEmptyIfError() = try { Json(this) } catch (_: Exception) { Json() }
fun MutableMap<String, Any>.toJson() = Json(this)

open class Json: Serializable {

    companion object { val GSON: Gson = GsonBuilder().setPrettyPrinting().create() }

    internal var data = mutableMapOf<String, Any>()

    constructor()
    constructor(jsonString: String) {
        if (jsonString.isNotBlank())
            data = GSON.fromJson<MutableMap<String, Any>>(jsonString, MutableMap::class.java)
    }
    constructor(map: MutableMap<String, Any>) { data = map }

    fun containsPath(path: String) = data.containsKey(path)
    val pathList get() = data.keys.toList()
    fun getSection(path: String) = data.keys.filter { it.startsWith("$path.") }.map {

        val substring = it.substring(path.length + 1)
        if (substring.contains(".")) substring.substring(0, substring.indexOf(".")) else substring

    }

    fun remove(path: String) = data.remove(path)
    fun removeAll(path: String) = buildMap {
        for (datum in data.toMap().filter { it.key.startsWith(path) })
            this[datum.key] = data.remove(datum.key)
    }
    fun setItemStackList(path: String, list: List<ItemStack>) = set(path, list.map { it.encodeToString() ?: "null" })
    fun setLocationList(path: String, list: List<Location>) = set(path, list.map { it.encodeToString() ?: "null" })

    fun put(path: String, data: Any?) = set(path, data)
    operator fun set(path: String, data: Any?) {
        if (data == null) remove(path)
        else this.data[path] = when (data) {
            is ItemStack -> data.encodeToString() ?: "null"
            is Location -> data.encodeToString() ?: "null"
            else -> data
        }
    }
    @Suppress("UNCHECKED_CAST") operator fun <T> get(path: String) = data[path] as? T?
    fun <T> getOrElse(path: String, orElse: T) = get(path) ?: orElse

    fun getString(path: String) = get<Any>(path)?.toString()
    fun getInt(path: String) = getDouble(path)?.toInt()
    fun getShort(path: String) = getInt(path)?.toShort()
    fun getLong(path: String) = getDouble(path)?.toLong()
    fun getDouble(path: String) = getString(path)?.toDoubleOrNull()
    fun getItemStack(path: String) = getString(path)?.decodeToItemStack()
    fun getLocation(path: String) = getString(path)?.decodeToLocation()
    fun <T> getList(path: String) = get<MutableList<T>>(path)
    fun getItemStackList(path: String) = get<MutableList<String>>(path)?.mapNotNull { it.decodeToItemStack() }?.toMutableList()
    fun getLocationList(path: String) = get<MutableList<String>>(path)?.mapNotNull { it.decodeToLocation() }?.toMutableList()
    fun getBoolean(path: String, otherParsedAsFalse: Array<String> = arrayOf(), otherParsedAsTrue: Array<String> = arrayOf()): Boolean? {

        val string = getString(path)?.lowercase() ?: return null

        val bool = string.toBooleanStrictOrNull()
        if (bool != null) return bool

        if (otherParsedAsFalse.any { it.equals(string, true) }) return false
        if (otherParsedAsTrue.any { it.equals(string, true) }) return true

        return null

    }

    fun getSstringOrElse(path: String, orElse: String) = getString(path) ?: orElse
    fun getIntOrElse(path: String, orElse: Int) = getInt(path) ?: orElse
    fun getLongOrElse(path: String, orElse: Long) = getLong(path) ?: orElse
    fun getShortOrElse(path: String, orElse: Short) = getShort(path) ?: orElse
    fun getItemStackOrElse(path: String, orElse: ItemStack) = getString(path)?.decodeToItemStack() ?: orElse
    fun getLocationOrElse(path: String, orElse: Location) = getString(path)?.decodeToLocation() ?: orElse
    fun <T> getListOrElse(path: String, orElse: List<T> = listOf()) = getList(path) ?: orElse.toMutableList()
    fun getItemStackListOrElse(path: String, orElse: List<ItemStack> = listOf()) = getItemStackList(path) ?: orElse.toMutableList()
    fun getLocationListOrElse(path: String, orElse: List<Location> = listOf()) = getLocationList(path) ?: orElse.toMutableList()
    fun getBooleanOrElse(path: String, orElse: Boolean, otherParsedAsFalse: Array<String> = arrayOf(), otherParsedAsTrue: Array<String> = arrayOf()) = getBoolean(path, otherParsedAsFalse, otherParsedAsTrue) ?: orElse

    fun build(): String = GSON.toJson(data)

}