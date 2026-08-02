package com.tapasco.characters.presentation.mapper

import androidx.annotation.StringRes
import com.tapasco.characters.R
import com.tapasco.characters.domain.model.GenderCharacter
import com.tapasco.characters.domain.model.StatusCharacter

val StatusCharacter.labelRes: Int
    @StringRes
    get() = when (this) {
        StatusCharacter.ALIVE -> R.string.status_alive
        StatusCharacter.DEAD -> R.string.status_dead
        StatusCharacter.UNKNOWN -> R.string.status_unknown
    }

val GenderCharacter.labelRes: Int
    @StringRes
    get() = when (this) {
        GenderCharacter.FEMALE -> R.string.gender_female
        GenderCharacter.MALE -> R.string.gender_male
        GenderCharacter.GENDERLESS -> R.string.gender_genderless
        GenderCharacter.UNKNOWN -> R.string.gender_unknown
    }
