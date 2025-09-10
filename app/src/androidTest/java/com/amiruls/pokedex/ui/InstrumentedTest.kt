package com.amiruls.pokedex.ui

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.amiruls.pokedex.MainActivity
import com.amiruls.pokedex.data.repository.PokemonRepositoryInterface
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class InstrumentedTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: PokemonRepositoryInterface

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testPokemonListDisplays() {
        composeRule.onNodeWithText("Bulbasaur").assertIsDisplayed()
    }

    @Test
    fun testPokemonSort() {

        val sortOptions = listOf(
            "Sort A-Z" to "Abomasnow",
            "Sort Z-A" to "Zygarde-complete",
            "Sort by ID" to "Bulbasaur"
        )

        for ((option, expectedFirst) in sortOptions) {
            // Click sort toggle and the option
            composeRule.onNodeWithContentDescription(option).performClick()
            composeRule.waitForIdle()
            // Wait until the first item matches the expected text
            composeRule.onNodeWithText(expectedFirst).assertIsDisplayed()
        }
    }

    @Test
    fun testPokemonDetailDisplays() {
        composeRule.onNodeWithText("Bulbasaur").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Overgrow").assertIsDisplayed()
        composeRule.onNodeWithText("Chlorophyll").assertIsDisplayed()
    }

    @Test
    fun testFavoritePokemon() {
        // Open the detail screen of the first Pokémon ("Bulbasaur")
        composeRule.onNodeWithText("Bulbasaur").performClick()
        composeRule.waitForIdle()

        // Mark it as a favorite by clicking the Favorite button in the Top App Bar
        composeRule.onNodeWithContentDescription("Favorite").performClick()
        composeRule.waitForIdle()

        // Return to the Pokémon list by clicking the Back button
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.waitForIdle()

        // Filter the list to show only favorites by clicking the "Favorites" chip
        composeRule.onNodeWithText("Favorites").performClick()
        composeRule.waitForIdle()

        // Verify that "Bulbasaur" is displayed in the filtered favorites list
        composeRule.onNodeWithText("Bulbasaur").assertIsDisplayed()
    }

}