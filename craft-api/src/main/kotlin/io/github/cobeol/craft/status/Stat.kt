package io.github.cobeol.craft.status

import io.github.cobeol.craft.monun.config.Config
import io.github.cobeol.craft.monun.config.RangeInt
import io.github.cobeol.craft.util.Formula
import org.bukkit.Material
import org.bukkit.event.Listener
import java.util.*

open class Stat {
    /**
     * GUI에 보여줄 능력치의 준말을 정합니다
     */
    @Config(required = false)
    var symbol: String? = null
        protected set

    /**
     * 시작 능력치를 정합니다.
     *
     * !! >> [level]은 [maxLevel]보다 클 수 없습니다.
     */
    @Config(required = false)
    @RangeInt(min = 0)
    var level: Int = 0
        protected set (value) {
            field = value
            maxExp = Formula.calculateMaxExp(value, 4)
        }

    /**
     * 시작 시, 랜덤으로 증가하는 능력치의 최대 수치를 정합니다.
     *
     * !! >> {[level] + [randomLevel]}은 [maxLevel]보다 클 수 없습니다.
     */
    @Config(required = false)
    @RangeInt(min = 0, max = 10)
    var randomLevel: Int = 0
        protected set (value) {
            require(value <= maxLevel) { "{level + randomLevel}은 {maxLevel}보다 클 수 없습니다." }

            field = value
            if (level != 0)
                return

            level += (0..randomLevel).random()
        }

    /**
     * 능력치가 이 수치에 도달하면, 더 이상 증가하지 않습니다.
     */
    @Config(required = false)
    @RangeInt(min = 0)
    var maxLevel: Int = 100
        protected set

    /**
     * 시작 경험치를 정합니다.
     */
    var exp: Long = 0
        protected set (value) {
            field = value
            if (value >= maxExp) {
                if (isLocked) return

                field -= maxExp
                level ++
            }
        }

    /**
     * 자동으로 계산되는 다음 능력치 상승까지의 경험치입니다.
     */
    var maxExp: Long = 0
        protected set

    /**
     * 경험치가 처리되는 이벤트입니다.
     */
    @Config(required = true)
    lateinit var expEvent: StatEventListener<out Stat>
        protected set

    /**
     * 능력을 표시하는 아이콘입니다.
     */
    @Config("icon", required = false)
     var icon: Material? = null
        protected set

    /**
     * 경험치에 도달해도, 능력치가 증가하는 것을 막습니다.
     */
    var isLocked: Boolean = false
        protected set

    /**
     * 누구의 스텟인가요?
     */
    @Config(required = true)
    lateinit var uniqueId: UUID
        protected set

    /**
     * 경험치를 추가합니다. [expEvent]에서 사용할 함수입니다.
     *
     * @param value 추가될 경험치로, 음수일 수 없습니다.
     */
    fun addExp(value: Long) {
        require(value >= 0) { "{value}는 음수일 수 없습니다." }
        exp += value
    }

    /**
     * 능력치를 추가합니다. [expEvent]에서 사용할 함수입니다.
     *
     * @param value 추가될 능력치로, 음수일 수 없습니다.
     */
    fun addLevel(value: Int) {
        require(value >= 0) { "{value}는 음수일 수 없습니다." }
        level += value
    }
}

open class StatEventListener<T: Stat>(stat: T): Listener