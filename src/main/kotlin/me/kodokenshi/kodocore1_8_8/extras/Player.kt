package me.kodokenshi.kodocore1_8_8.extras

import me.kodokenshi.kodocore1_8_8.modules.ChatInput
import me.kodokenshi.kodocore1_8_8.plugin.JsonMessage
import me.kodokenshi.kodocore1_8_8.plugin.KPlugin
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

fun Player.pickupSound(volume: Double = .7) = playSound(Sound.CHICKEN_EGG_POP, volume = volume, distortion = ((randomFloat() - randomFloat()) * .7 + 1f) * 2f)
fun Player.playSound(sound: Sound, volume: Double = 1.0, distortion: Double = 1.0) = playSound(location, sound, volume.toFloat(), distortion.toFloat())
fun HumanEntity.playTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 70, fadeOut: Int = 20) {

    val titlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"$title\"}"), fadeIn, stay, fadeOut)
    val subtitlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"$subtitle\"}"), fadeIn, stay, fadeOut)

    (this as CraftPlayer).handle.playerConnection.apply {
        sendPacket(titlePacket)
        sendPacket(subtitlePacket)
    }

}
fun HumanEntity.playActionBar(message: String) = (this as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"$message\"}"), 2))
inline fun Player.sendJsonMessage(jsonMessage: JsonMessage.() -> Unit) = JsonMessage().apply(jsonMessage).sendTo(this)
fun Player.sendJsonMessage(jsonMessage: JsonMessage) = jsonMessage.sendTo(this)
fun HumanEntity.faceLocation(location: Location) {
    val direction = location.clone().subtract(eyeLocation).toVector()
    val playerLocation = this.location.setDirection(direction)
    teleport(playerLocation)
}
fun HumanEntity.sendColoredMessage(message: String) = sendMessage(message.color())
fun HumanEntity.sendColoredMessage(vararg message: String) = sendMessage(message.toList().color().toTypedArray())
fun HumanEntity.isWaitingAnyChatInput() = javaPlugin<KPlugin>().modules.chatInput.hasAny(this)
fun HumanEntity.isWaitingChatInput(id: String) = javaPlugin<KPlugin>().modules.chatInput.has("$name.$id")
fun HumanEntity.waitChatInput(
    input: ChatInput.Result.() -> Unit,
    whenQuitWithoutInput: () -> Unit = {},
    timeout: Long = 0L,
    onTimeout: () -> Unit = {},
    persistent: Boolean = false,
    sync: Boolean = false,
    id: String = ""
) = javaPlugin<KPlugin>().modules.chatInput.waitInput(this@waitChatInput, input, whenQuitWithoutInput, timeout, onTimeout, persistent, sync, "$name.$id")
fun HumanEntity.cancelChatInput(id: String) = javaPlugin<KPlugin>().modules.chatInput.cancel("$name.$id")
fun HumanEntity.cancelAllChatInput() = javaPlugin<KPlugin>().modules.chatInput.cancelAll(this)
