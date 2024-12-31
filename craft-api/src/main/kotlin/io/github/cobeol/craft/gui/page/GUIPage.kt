package io.github.cobeol.craft.gui.page

@Suppress("unused")
open class GUIPage {
    companion object {
        /**
         * 직관적인 [GUIPage] 생성을 위한 함수입니다.
         *
         * page를 생성하고 apply를 적용하는 방식과 동일합니다.
         *
         * ```
         * // Example
         * val widget = GUIWidget.create {
         *     width(3)
         *     margin(2)
         *     onClick { clickEvent ->
         *         // Execute
         *     }
         * }
         *
         * val button = GUIButton.create {
         *     icon(ItemStack(Material.STONE))
         *     onClick { clickEvent ->
         *         // Execute
         *     }
         * }
         *
         * val page = GUIPage.create {
         *     header(button)
         *     body(widget)
         * }
         * ```
         */
        fun create(init: GUIPage.() -> Unit): GUIPage {
            return GUIPage().apply(init)
        }
    }

    /**
     * 다른 [GUIPage]를 연결하기 위한 구역입니다.
     */
    val headers: List<GUIButton>
        get() = _headers.toList()

    private var _headers: MutableList<GUIButton> = mutableListOf()

    /**
     * [GUIPage]의 내용을 나타내는 구역입니다.
     *
     * 초과되는 부분은 [GUIPage.footers]에서 다음 서브페이지로 넘기는 식으로 진행합니다.
     */
    val bodies: List<GUIWidget>
        get() = _bodies.toList()

    private var _bodies: MutableList<GUIWidget> = mutableListOf()

    /**
     * [GUIPage.bodyGroups]를 넘기기 위한 구역입니다. 이 값은 변경할 수 없습니다.
     */
    val footers: List<GUIButton>
        get() = _footers.toList()

    private var _footers: MutableList<GUIButton> = mutableListOf()

    /**
     * [GUIPage]의 그래픽, `Texture Pack`을 이용하여 글자에 사진을 넣어 표현합니다.
     *
     * [GUIPage.title]값에 유니코드가 들어갑니다.
     */
    var title: String = ""
        protected set

    private val headerGroups: MutableList<Int>
        get() {
            val groups = mutableListOf<Int>()
            val pane = GUIPane(9, 1)

            headers.forEachIndexed { index, widget ->
                if (pane.isPlaceable(widget))
                    return@forEachIndexed

                groups.add(index)
                pane.clear()
            }

            return groups
        }

    private val bodyGroups: List<Int>
        get() {
            val groups = mutableListOf<Int>()
            val pane = GUIPane(9, 4)

            bodies.forEachIndexed { index, widget ->
                if (pane.isPlaceable(widget))
                    return@forEachIndexed

                groups.add(index)
                pane.clear()
            }

            return groups
        }

//    private val footerGroups: MutableList<Int> = mutableListOf()

    private val index: MutableList<Int> = MutableList(3) { 0 }

    private var headerIndex: Int
        get() = index[0]
        set(value) { index[0] = value }

    private var bodyIndex: Int
        get() = index[1]
        set(value) { index[1] = value }

//    private var footerIndex: Int
//        get() = index[2]
//        set(value) { index[2] = value }

    /**
     * [GUIPage.title]을 정의합니다.
     */
    fun title(title: String) { this.title = title }

    /**
     * [GUIPage.headers]에 값을 추가합니다.
     */
    fun header(header: GUIButton) = this._headers.add(header)

    /**
     * [GUIPage.headers]을 정의합니다.
     */
    fun headers(headers: MutableList<GUIButton>) { this._headers = headers }

    /**
     * [GUIPage.bodies]에 값을 추가합니다.
     */
    fun body(body: GUIButton) = this._bodies.add(body)

    /**
     * [GUIPage.bodies]을 정의합니다.
     */
    fun bodies(bodies: MutableList<GUIWidget>) { this._bodies = bodies }

    init {
        TODO("footer에 button 추가하기")
    }

    fun build() {
        //
    }
}