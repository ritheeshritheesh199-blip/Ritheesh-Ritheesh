package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.example.data.Destination
import com.example.ui.DestinationCard
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleDest = Destination(
        id = "ooty",
        name = "Ooty",
        region = "Western Ghats",
        category = "Hill Stations",
        summary = "Queen of Hill Stations",
        description = "Breathtaking landscapes",
        bestTimeToVisit = "April to June",
        attractions = listOf("Nilgiri Mountain Railway", "Botanical Garden"),
        localFood = listOf("Varkey", "Nilgiri Tea"),
        insiderTip = "Book toy train early",
        imageUrl = "",
        coordinates = "11.4102° N"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        DestinationCard(
            destination = sampleDest,
            isBookmarked = true,
            onBookmarkToggle = {},
            onClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
