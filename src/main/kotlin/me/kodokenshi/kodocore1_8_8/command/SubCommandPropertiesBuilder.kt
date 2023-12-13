package me.kodokenshi.kodocore1_8_8.command

class SubCommandPropertiesBuilder: CommandProperties() {

    override var alias get() = super.alias; public set(alias) { super.alias = alias }

    fun setAlias(vararg alias: String) { this.alias = alias.toList() }

}