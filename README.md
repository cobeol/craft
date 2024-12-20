# Cobeol Craft Api //B (1.21+)

[![Kotlin](https://img.shields.io/badge/java-21-ED8B00.svg?logo=java)](https://www.azul.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.20-585DEF.svg?logo=kotlin)](http://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-8.10-02303A.svg?logo=gradle)](https://gradle.org)
[![GitHub](https://img.shields.io/github/license/monun/paper-sample-complex)](https://www.gnu.org/licenses/gpl-3.0.html)
[![Kotlin](https://img.shields.io/badge/youtube-코벌-red.svg?logo=youtube)](https://www.youtube.com/@cobeol0111)

---

> 코드 구조를 [24/12.16 패치](https://github.com/cobeol/craft/commit/eabc235a1be2e98d82a1af4a41ed5e95869ab9db) 이 후, 변경하였습니다.
> <br>
> 함수 자체는 유사하지만, 코드 스타일을 정의하여 완전히 통일할 예정입니다.
> 
> 예를 들면, <br>
> NMS 간접 사용, 클래스 변수 접근 불가능 -> ```io.github.cobeol.craft.api```<br>
> 아니라면? -> ```io.github.cobeol.craft.core```

* ### Features
    * ```StatusServer```: 모든 플레이어의 ```Status```를 관리하는 서버입니다.
        * ```Status```: 플레이어의 정보를 나타내며, ```Stat```이 내부에 포함됩니다.
            * ```Stat```: 플레이어의 능력치를 나타내기 위한 클래스입니다.
        * ```EntityEventManager```: 특정 엔티티의 이벤트를 등록할 수 있는 기능입니다.
            * ```TargetEntity```: 이벤트의 주체자와 대상이 나뉠 때, 뭘 기준으로 잡아서 이벤트를 호출할 것인지 정할 수 있습니다.

* ### TODO::Features
    * ```GUIBuilder```: ```Status```를 플레이어에게 보여주기 위해, ```Entity:Interaction```이나 ```Inventory```로 표시해주는 기능입니다.
    * ```PacketSupport```: ```ProtocolLib```에 의존하지 않고, ```Minecraft NMS```를 직접 이용하여 패킷을 전송하기 위한 기능입니다.
    * ```SkillTree```: 특정 조건을 달성하면 찍을 수 있는 커스텀 스킬 트리입니다. 액티브나 패시브를 정할 수 있습니다.

* ### Supported minecraft versions
    * (None)
---

### NOTE

[monun/tap](https://github.com/monun/tap), [monun/psychics](https://github.com/monun/psychics/)를 참고하여, 컨텐츠에서 사용할 플러그인을 만들기 위한 메인 플러그인 api를 제작하였습니다.<br>
*코드가 유사할 수 있어요ヽ(°〇°)ﾉ 난 코딩 벌레입니다  (˚ ˃̣̣̥⌓˂̣̣̥ )

---