package me.kodokenshi.kodocore1_8_8.compatibility

import org.bukkit.Bukkit

enum class ServerVersion(val version: String, val nmsVersion: String) {

    UNKNOWN("UNKNOWN", "UNKNOWN"),
    MC_1_8("1.8.8", "v1_8_R3");

    fun isOrLess(other: ServerVersion) = ordinal <= other.ordinal
    fun isLessThan(other: ServerVersion) = ordinal < other.ordinal
    fun isAtLeast(other: ServerVersion) = ordinal >= other.ordinal
    fun isGreaterThan(other: ServerVersion) = ordinal > other.ordinal
    fun isIn(min: ServerVersion, max: ServerVersion) = ordinal in min.ordinal..max.ordinal

    companion object {

        val CURRENT by lazy {
            val version = Bukkit.getBukkitVersion().split("-")[0]
            entries.firstOrNull { version.contains(it.version) } ?: UNKNOWN
        }

    }

}