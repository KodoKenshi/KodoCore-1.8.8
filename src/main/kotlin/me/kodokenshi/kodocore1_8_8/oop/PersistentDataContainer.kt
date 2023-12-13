package me.kodokenshi.kodocore1_8_8.oop

import me.kodokenshi.kodocore1_8_8.extras.javaPlugin
import me.kodokenshi.kodocore1_8_8.data.JsonFile
import me.kodokenshi.kodocore1_8_8.plugin.KPlugin
import java.util.*

class PersistentDataContainer(uuid: UUID) {

    companion object { var container: JsonFile? = null; private set }

    init { if (container == null) container = JsonFile("plugins/${javaPlugin<KPlugin>().name}/PersistentDataContainer", "container") }

    private val dataContainer = container!!
    private val uuid = uuid.toString()

    fun getString(key: String) = dataContainer.getString("$uuid.$key")

    fun hasString(key: String) = dataContainer.containsPath("$uuid.$key")

    fun setString(key: String, string: String) {
        dataContainer["$uuid.$key"] = string
        dataContainer.saveFile()
    }
    fun removeString(key: String) {
        dataContainer.remove("$uuid.$key")
        dataContainer.saveFile()
    }

    fun clear() {
        dataContainer.removeAll(uuid)
        dataContainer.saveFile()
    }

}