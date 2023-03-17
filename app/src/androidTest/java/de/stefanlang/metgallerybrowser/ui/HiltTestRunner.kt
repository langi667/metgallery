package de.stefanlang.metgallerybrowser.ui

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication
import de.stefanlang.metgallerybrowser.METGalleryBrowserApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        val retVal = super.newApplication(cl, HiltTestApplication::class.java.name, context)
        METGalleryBrowserApplication.appContext = retVal

        return retVal
    }
}