package com.tapasco.characters.core.utils

fun String?.normalizedQuery(): String? = this?.trim()?.takeIf(String::isNotEmpty)