/*
 * Copyright (C) 2024 Cobeol
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * For the full license text, see the LICENSE file in the project root.
 */

package io.github.cobeol.craft.util

import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

/**
 * 특정 부모 클래스를 상속받은 자식 요소의 인스턴스를 가져오는 확장 함수입니다
 *
 * @param T 부모 클래스
 */
@Suppress("unused")
inline fun <reified T : Any> Any.getChildInstancesOfType(): List<T> =
    this::class.memberProperties
        .filter { it.returnType.isSubtypeOf(T::class.starProjectedType) }
        .mapNotNull { it.getter.call(this) as? T }

/**
 * [Any.getChildInstancesOfType]의 조건을 달성하면서, 클래스의 끝 이름이 부모와 동일한 인스턴스를 가져오는 함수입니다
 *
 * @param T 부모 클래스
 */
@Suppress("unused")
inline fun <reified T : Any> Any.getChildInstancesOfPerfectType(): List<T> =
    getChildInstancesOfType<T>().filter {
        it::class.simpleName?.endsWith(T::class.simpleName ?: "") == true
    }