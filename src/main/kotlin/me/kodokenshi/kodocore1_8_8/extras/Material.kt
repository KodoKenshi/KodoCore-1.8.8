package me.kodokenshi.kodocore1_8_8.extras

import org.bukkit.Material

val Material.isTool get() = name.endsWithAny("_PICKAXE", "_AXE", "_SPADE", "_HOE", "_SWORD", "BOW")
val Material.isArmor get() = name.endsWithAny("_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS")