package de.stefanlang.metgallerybrowser

import android.app.Application
import de.stefanlang.network.NetworkAPI

class METGalleryBrowserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupModules()
    }

    private fun setupModules() {
        NetworkAPI.setup(this)
    }
}