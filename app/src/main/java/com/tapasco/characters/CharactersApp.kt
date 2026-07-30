package com.tapasco.characters

import android.app.Application
import com.tapasco.characters.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class CharactersApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@CharactersApp)
        }
    }
}
