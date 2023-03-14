package de.stefanlang.metgallerybrowser

import android.app.Application
import android.content.Context
import de.stefanlang.network.NetworkAPI

class METGalleryBrowserApplication : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        setupModules()
    }

    private fun setupModules() {
        NetworkAPI.setup(this)
    }
}