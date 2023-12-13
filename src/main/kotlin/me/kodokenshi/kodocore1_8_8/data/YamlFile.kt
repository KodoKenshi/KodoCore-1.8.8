package me.kodokenshi.kodocore1_8_8.data

import me.kodokenshi.kodocore1_8_8.extras.*
import me.kodokenshi.kodocore1_8_8.plugin.KPlugin
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

inline fun yamlFile(filePath: String, fileName: String, block: YamlFile.() -> Unit = {}) = YamlFile(filePath, fileName).apply(block)
inline fun yamlFile(fileName: String, block: YamlFile.() -> Unit = {}) = YamlFile(fileName).apply(block)

@Suppress("UNCHECKED_CAST")
class YamlFile {

    private val fileName: String
    private val filePath: File
    private val file: File
    private var config: YamlConfiguration

    constructor(filePath: String, fileName: String) {

        this.fileName = "$fileName.yml"
        this.filePath = File(filePath)
        this.file = File(this.filePath, this.fileName)

        config = if (init()) YamlConfiguration.loadConfiguration(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)) else YamlConfiguration()

    }
    constructor(fileName: String): this("plugins/${javaPlugin<KPlugin>().name}", fileName)

    fun reloadFile() { config.load(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)) }
    fun saveFile() { config.save(file) }
    fun delete() { if (!file.delete()) file.deleteOnExit() }

    fun getSection(path: String, deep: Boolean = false) = config.getConfigurationSection(path)?.getKeys(deep) ?: setOf()
    fun containsPath(key: String) = config.contains(key)
    fun remove(path: String) = config.set(path, null)
    fun setItemStackList(path: String, list: List<ItemStack>) = set(path, list.map { it.encodeToString() ?: "null" })
    fun setLocationList(path: String, list: List<Location>) = set(path, list.map { it.encodeToString() ?: "null" })

    fun put(path: String, data: Any?) = set(path, data)
    operator fun set(path: String, data: Any?) {
        if (data == null) remove(path)
        else config.set(path, when(data) {
            is ItemStack -> data.encodeToString() ?: "null"
            is Location -> data.encodeToString() ?: "null"
            else -> data
        })
    }
    operator fun <T> get(path: String) = config.get(path) as? T
    fun <T> getOrElse(path: String, orElse: T? = null) = get(path) ?: orElse
    fun getString(path: String) = get<Any>(path)?.toString()
    fun getInt(path: String) = getString(path)?.toIntOrNull()
    fun getLong(path: String) = getString(path)?.toLongOrNull()
    fun getDouble(path: String) = getString(path)?.toDoubleOrNull()
    fun getItemStack(path: String) = getString(path)?.decodeToItemStack()
    fun getLocation(path: String) = getString(path)?.decodeToLocation()
    fun getBoolean(path: String, otherParsedAsFalse: Array<String> = arrayOf(), otherParsedAsTrue: Array<String> = arrayOf()): Boolean? {

        val string = getString(path)?.lowercase() ?: return null

        val bool = string.toBooleanStrictOrNull()
        if (bool != null) return bool

        if (otherParsedAsFalse.any { it.equals(string, true) }) return false
        if (otherParsedAsTrue.any { it.equals(string, true) }) return true

        return null

    }
    fun <T> getList(path: String) = get<MutableList<T>>(path)
    fun getItemStackList(path: String) = get<MutableList<String>>(path)?.mapNotNull { it.decodeToItemStack() }
    fun getLocationList(path: String) = get<MutableList<String>>(path)?.mapNotNull { it.decodeToLocation() }

    fun getStringOrElse(path: String, orElse: String) = getString(path) ?: orElse
    fun getIntOrElse(path: String, orElse: Int) = getInt(path) ?: orElse
    fun getLongOrElse(path: String, orElse: Long) = getLong(path) ?: orElse
    fun getDoubleOrElse(path: String, orElse: Double) = getDouble(path) ?: orElse
    fun getItemStackOrElse(path: String, orElse: ItemStack) = getString(path)?.decodeToItemStack() ?: orElse
    fun getLocationOrElse(path: String, orElse: Location) = getString(path)?.decodeToLocation() ?: orElse
    fun <T> getListOrElse(path: String, orElse: MutableList<T>) = getList(path) ?: orElse
    fun getItemStackListOrElse(path: String, orElse: MutableList<ItemStack>) = getItemStackList(path) ?: orElse
    fun getLocationListOrElse(path: String, orElse: MutableList<Location>) = getLocationList(path) ?: orElse
    fun getBooleanOrElse(path: String, orElse: Boolean, otherParsedAsFalse: Array<String> = arrayOf(), otherParsedAsTrue: Array<String> = arrayOf()) = getBoolean(path, otherParsedAsFalse, otherParsedAsTrue) ?: orElse

    private fun init(): Boolean {

        return try {

            if (!filePath.exists()) filePath.mkdirs()
            if (!file.exists()) {

                try { javaPlugin<KPlugin>().saveResource(fileName, true) } catch (_: Exception) { file.createNewFile() }
                file.createNewFile()

            }

            true

        } catch (e: Exception) {
            "&9${javaPlugin<KPlugin>().name}> &7Couldn't create or load file \"$fileName\" in folder \"${filePath.absoluteFile}\".".log()
            e.printStackTrace()
            false
        }

    }

}