# Cobeol Craft Api (1.21+)

[![Kotlin](https://img.shields.io/badge/java-21-ED8B00.svg?logo=java)](https://www.azul.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.20-585DEF.svg?logo=kotlin)](http://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-8.10-02303A.svg?logo=gradle)](https://gradle.org)
[![GitHub](https://img.shields.io/github/license/monun/paper-sample-complex)](https://www.gnu.org/licenses/gpl-3.0.html)
[![Kotlin](https://img.shields.io/badge/youtube-코벌-red.svg?logo=youtube)](https://www.youtube.com/@cobeol0111)

## 프로젝트 구성하기

1. 저장소 복제 `git clone https://github.com/monun/paper-sample.git`
2. 프로젝트 이름 변경 (`settings.gradle.kts` 의 `rootProject.name`)
3. 구성 태스크 실행 `./gradlew setupModules`

---

#### 개요

[monun/tap](https://github.com/monun/tap), [monun/psychics](https://github.com/monun/psychics/)를 참고하여, 컨텐츠에서 사용할 플러그인을 만들기 위한 메인 플러그인 api를 제작하였습니다.

사용자 정의 능력치나 스킬, 그리고 아바타 같은 것들이 있습니다.


---

#### API

최상위 계층 인터페이스

---

#### CORE

API의 구현, 실제 실행 코드, `net.minecraft.server` 를 참조하는 코드

하위에 참조할 버전 이름의 프로젝트를 생성 `ex) v1.18`

---

#### PLUGIN

PaperMC 와 상호작용할 JavaPlugin 을 포함한 코드

* `./gradlew devJar` mojang mapped bundler jar
* `./gradlew reobfJar` reobfusecated bundler jar
* `./gradlew clipJar` clip jar

---

#### PUBLISH

배포용 프로젝트

* `./gradlew publishToMavenLocal -Pdev` 로 로컬 저장소에 mojangmapping 버전의 jar 파일을 배포 가능

--- 