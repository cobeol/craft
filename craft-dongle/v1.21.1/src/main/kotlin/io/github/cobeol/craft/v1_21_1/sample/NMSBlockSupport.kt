package io.github.cobeol.craft.v1_21_1.sample

import io.github.cobeol.craft.sample.BlockSupport
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase
import org.bukkit.block.Block
import org.bukkit.craftbukkit.block.CraftBlockState

class NMSBlockSupport: BlockSupport {
    override fun getHardness(block: Block): Float {
        val nmsBlock = (block.state as CraftBlockState).handle as BlockStateBase
        return nmsBlock.destroySpeed
    }
}