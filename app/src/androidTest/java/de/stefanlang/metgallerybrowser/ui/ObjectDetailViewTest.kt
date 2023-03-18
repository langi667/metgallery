package de.stefanlang.metgallerybrowser.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.HiltAndroidTest
import de.stefanlang.metgallerybrowser.*
import de.stefanlang.metgallerybrowser.data.remote.METAPI
import de.stefanlang.metgallerybrowser.domain.METObjectEntryBuilder
import de.stefanlang.metgallerybrowser.domain.repository.ImageRepositoryImpl
import de.stefanlang.metgallerybrowser.domain.repository.METObjectRepositoryImpl
import de.stefanlang.metgallerybrowser.ui.common.Tags
import de.stefanlang.metgallerybrowser.ui.objectdetail.ObjectDetailView
import de.stefanlang.metgallerybrowser.ui.objectdetail.ObjectDetailViewModel
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
class ObjectDetailViewTest : HiltInstrumentedTest() {

    private val objectID: Int = 253343

    @Inject
    lateinit var api: METAPI

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testLoadingState() {
        setupRule()
        rule.onNodeWithText(getString(R.string.loading_state_hint)).assertExists()
        rule.onNodeWithTag(Tags.PROGRESSBAR.name).assertExists()
    }

    @Test
    fun testNotFound() {
        setupRule(-1)
        rule.waitUntilFoundWithText(Timeout.LONG, getString(R.string.no_results_state_hint))
    }

    @Test
    fun testFound() {
        setupRule()

        rule.waitUntilFoundWithText(Timeout.LONG, "Chalcedony scaraboid")
        rule.waitUntilFoundWithText(Timeout.LONG, "Greek and Roman Art")
        rule.waitAndFindAtLeastWithTag(Timeout.LONG, Tags.GALLERY_IMAGE_PREVIEW.name, 3)
    }

    @Test
    fun testGallery() {
        setupRule()
        val contentDescriptionGallery =
            "https://images.metmuseum.org/CRDImages/gr/web-large/DP328403.jpg"
        val contentDescriptionPreview =
            getString(id = R.string.preview_content_description, contentDescriptionGallery)

        rule.waitUntilFoundWithContentDescription(Timeout.VERY_LONG, contentDescriptionPreview)
        rule.onNodeWithContentDescription(contentDescriptionPreview).performClick().assertExists()
        rule.waitUntilFoundWithTag(Timeout.MEDIUM, Tags.GALLERY_BACKGROUND.name)

        rule.onNodeWithContentDescription(contentDescriptionGallery).performClick()
        rule.waitUntilNotFoundWithTag(Timeout.LONG, Tags.GALLERY_BACKGROUND.name)

        rule.onNodeWithContentDescription(contentDescriptionPreview).performClick().assertExists()
        rule.waitUntilFoundWithTag(Timeout.MEDIUM, Tags.GALLERY_BACKGROUND.name)

        rule.onNodeWithTag(Tags.BACK_BUTTON.name).performClick()
        rule.waitUntilNotFoundWithTag(Timeout.LONG, Tags.GALLERY_BACKGROUND.name)
    }

    private fun setupRule(objectID: Int = this.objectID) {
        rule.setContent {
            ObjectDetailView(
                navController = rememberNavController(),
                objectID = objectID,
                viewModel = ObjectDetailViewModel(
                    ImageRepositoryImpl(15, api),
                    METObjectRepositoryImpl(api),
                    METObjectEntryBuilder(appContext)
                )
            )
        }
    }
}