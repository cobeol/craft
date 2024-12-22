package io.github.cobeol.craft.commands

import io.github.cobeol.craft.invfx.InvFX
import io.github.cobeol.craft.invfx.openFrame
import io.github.cobeol.craft.plugin.CraftPlugin
import io.github.cobeol.craft.sample.SampleStatus
import io.github.cobeol.craft.status.StatusServer
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class TestCommand(private val plugin: CraftPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return false
        val statusServer = StatusServer.create<SampleStatus>(plugin, SampleStatus::class.java)

        statusServer.addPlayer(player).let {
            val stats = statusServer.status[player.uniqueId]?.stats
            stats?.let { player.sendMessage(Component.text(it.test)) }
        }

        val invFrame = InvFX.frame(1, Component.text("SAMPLE_INVENTORY")) {
            var clicked = false

            onOpen { openEvent ->
                openEvent.player.sendMessage("You opened the inventory")
            }

            onClose { closeEvent ->
                if (!clicked) {
                    closeEvent.player.sendMessage("Please click the diamond")
                    object: BukkitRunnable() {
                        override fun run() {
                            player.openFrame(this@frame)
                        }
                    }.runTaskLater(plugin, 1)
                }
            }

            onClick { x, y, clickEvent ->
                if (0 == x && 0 == y) {
                    // Action
                }
            }

            slot (0, 0) {
                item = ItemStack(Material.DIAMOND)
                onClick { clickEvent ->
                    clicked = true
                    clickEvent.whoClicked.closeInventory()
                }
            }


            pane(0, 0, 4, 1) {
                item(0, 0, ItemStack(Material.STONE))
                onClick { x, y, clickEvent ->
                    if (x == 0 && y == 0) {
                        // Action
                    }
                }
            }
        }
        player.openFrame(invFrame)

//        val eventManager = EntityEventManager(plugin)
//        eventManager.registerEvents(player, object : Listener {
//            @EventHandler
//            @TargetEntity(DefaultProvider.EntityDamageByEntity.Damager::class)
//            fun onEntityDamage(event: EntityDamageByEntityEvent) {
//                println("${event.damager.name}가 공격했습니다.")
//            }
//
//            @EventHandler
//            @TargetEntity(DefaultProvider.EntityDamageByEntity.Damagee::class)
//            fun onEntityDamaged(event: EntityDamageByEntityEvent) {
//                println("${event.entity.name}가 공격받았습니다.")
//            }
//        }

        return true
    }
}