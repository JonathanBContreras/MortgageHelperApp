package com.example.mortgagehelperapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testMortgageCalculation() {
        // Input values
        onView(withId(R.id.homePriceInput)).perform(typeText("300000"), closeSoftKeyboard())
        onView(withId(R.id.squareFootageInput)).perform(typeText("2000"), closeSoftKeyboard())
        onView(withId(R.id.downPaymentPercentInput)).perform(typeText("20"), closeSoftKeyboard())
        onView(withId(R.id.interestRateInput)).perform(typeText("4.5"), closeSoftKeyboard())
        onView(withId(R.id.hoaFeesInput)).perform(typeText("100"), closeSoftKeyboard())

        // Click calculate button
        onView(withId(R.id.calculateButton)).perform(click())

        // Verify results are displayed
        onView(withId(R.id.monthlyPaymentResult)).check(matches(isDisplayed()))
        onView(withId(R.id.totalCostResult)).check(matches(isDisplayed()))
        onView(withId(R.id.costPerSqFtResult)).check(matches(isDisplayed()))
        onView(withId(R.id.breakdownResult)).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidInput() {
        // Input invalid values
        onView(withId(R.id.homePriceInput)).perform(typeText("-1000"), closeSoftKeyboard())
        onView(withId(R.id.squareFootageInput)).perform(typeText("0"), closeSoftKeyboard())
        onView(withId(R.id.downPaymentPercentInput)).perform(typeText("-10"), closeSoftKeyboard())
        onView(withId(R.id.interestRateInput)).perform(typeText("0"), closeSoftKeyboard())
        onView(withId(R.id.hoaFeesInput)).perform(typeText("-50"), closeSoftKeyboard())

        // Click calculate button
        onView(withId(R.id.calculateButton)).perform(click())

        // Verify error message is displayed
        onView(withText("Please enter valid values")).check(matches(isDisplayed()))
    }
} 