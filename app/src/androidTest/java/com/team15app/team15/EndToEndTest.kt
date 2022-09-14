package com.team15app.team15

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    private lateinit var context: Context

    private val teamA = "Mayo"
    private val teamB = "Dublin"
    private val matchInfo = "Croke Park"

    @Before
    fun setup() {
        context = getInstrumentation().targetContext
        clearFiles()
    }

    @Test
    fun testEvent() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.moveToState(Lifecycle.State.RESUMED)

        // 1. check default UI is shown
        checkDefaultUI()

        // 2. open match info dialog (where team names, fixture details and jerseys) are set
        onView(withId(R.id.rl_match_info)).perform(click())
        onView(withId(R.id.ll_match_info_write)).check(matches(isDisplayed()))

        // 3. enter team names and fixture details in the dialog
        onView(withId(R.id.et_team_a)).perform(typeText(teamA))
        onView(withId(R.id.et_team_b)).perform(typeText(teamB))
        onView(withId(R.id.et_match_info_name_write))
            .perform(typeText(matchInfo))
        ViewActions.closeSoftKeyboard()

        // 4. select goalkeeper jersey
        onView(withId(R.id.iv_goalkeeper)).perform(click())
        onData(anything()).inAdapterView(allOf(withId(R.id.gv_jerseys), isDisplayed()))
            .atPosition(0).perform(click())

        // 5. select outfielder jersey
        onView(withId(R.id.iv_outfielder)).perform(click())
        onData(anything()).inAdapterView(allOf(withId(R.id.gv_jerseys), isDisplayed()))
            .atPosition(10).perform(click())

        // 6. close match info dialog
        onView(withText("OK")).perform(click())

        // 7. check custom UI is shown with values entered by user for the match
        checkCustomUI()

        // 8. back should open the load teams dialog now that one team has been added
        Espresso.pressBack()

        // 9. create a new team
        onView(withText(context.getString(R.string.create_new_team))).perform(click())

        // 10. check default UI is shown (as we've selected to create a new team)
        checkDefaultUI()

        // 11. back should open the load teams dialog now that one team has been added
        Espresso.pressBack()

        // 12. select saved team from file (created in 6.)
        onView(withText(teamA + " " +
                context.getString(R.string.versus) + " " +
                teamB))
            .perform(click())

        // 13. check custom UI is shown with values entered by user for the match
        checkCustomUI()

        // 14. open the overflow menu and delete the team
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(context.getString(R.string.delete_title))).perform(click())
        onView(withText("OK")).perform(click())

        // 15. check default UI is shown
        checkDefaultUI()
    }

    private fun checkDefaultUI() {
        onView(withId(R.id.tv_match_info_team_a))
            .check(matches(withText(context.getString(R.string.default_team_name_a))))
        onView(withId(R.id.tv_match_info_team_b))
            .check(matches(withText(context.getString(R.string.versus) + " " +
                    context.getString(R.string.default_team_name_b))))
        onView(withId(R.id.tv_match_info_name_read))
            .check(matches(withText(context.getString(R.string.default_match_info))))
    }

    private fun checkCustomUI() {
        onView(withId(R.id.tv_match_info_team_a)).check(matches(withText(teamA)))
        onView(withId(R.id.tv_match_info_team_b)).check(matches(withText(
            context.getString(R.string.versus) + " " +teamB)))
        onView(withId(R.id.tv_match_info_name_read)).check(matches(withText(matchInfo)))
    }

    private fun clearFiles() {
        val file = getInstrumentation().targetContext.getExternalFilesDirs(null)[0]
        file.deleteRecursively()
        assert(file.listFiles().isNullOrEmpty())
    }

    @After
    fun tearDown() {
        clearFiles()
    }
}