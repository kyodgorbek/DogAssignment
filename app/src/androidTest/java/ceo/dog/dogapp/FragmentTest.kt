package ceo.dog.dogapp

import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ceo.dog.dogapp.data.Resource
import ceo.dog.dogapp.domain.Repository
import ceo.dog.dogapp.domain.model.Breed
import ceo.dog.dogapp.ui.main.MainActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentTest {

    private val testBreed = listOf(
        Breed("dog 1"),
        Breed("dog 2", listOf(Breed("dog 2 sub 1"), Breed("dog 2 sub 2")))
    )

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestApplication
        val repo = object : Repository {
            override suspend fun getAllBreed(): Flow<Resource<List<Breed>>> {
                return flowOf(Resource.success(testBreed))
            }

            override suspend fun getBreedImages(breed: String): Flow<Resource<List<String>>> {
                val res = when (breed) {
                    testBreed[1].name -> Resource.success(
                        listOf(
                            "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
                            "https://images.dog.ceo/breeds/hound-afghan/n02088094_1007.jpg",
                            "https://images.dog.ceo/breeds/hound-afghan/n02088094_1023.jpg",
                            "https://images.dog.ceo/breeds/hound-afghan/n02088094_10263.jpg",
                            "https://images.dog.ceo/breeds/hound-afghan/n02088094_10715.jpg"
                        )
                    )
                    else -> Resource.error("error")
                }
                return flowOf(res)
            }
        }
        DaggerTestAppComponent
            .builder()
            .testRepositoryModule(TestRepositoryModule(repo))
            .build()
            .inject(app)
    }

    @Test
    fun testClickBreedError() = runBlockingTest {
        val breedPos = 0
        ActivityScenario.launch(MainActivity::class.java)
        onView(allOf(withId(R.id.mainListRV), isDisplayed()))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(breedPos,
                    recyclerChildAction<AppCompatButton>(
                        R.id.breedBtn
                    ) { callOnClick() })
            )
        onView(withId(R.id.title))
            .check(matches(withText(R.string.all_breed_title)))
    }

    @Test
    fun testClickBreedSuccess() = runBlockingTest {
        val breedPos = 1
        ActivityScenario.launch(MainActivity::class.java)
        onView(allOf(withId(R.id.mainListRV), isDisplayed()))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(breedPos,
                    recyclerChildAction<AppCompatButton>(
                        R.id.breedBtn
                    ) { callOnClick() })
            )
        onView(withId(R.id.title))
            .check(matches(withText(testBreed[breedPos].name)))
    }
}

private fun <T : View> recyclerChildAction(@IdRes id: Int, block: T.() -> Unit): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return any(View::class.java)
        }

        override fun getDescription(): String {
            return "Performing action on RecyclerView child item"
        }

        override fun perform(
            uiController: UiController,
            view: View
        ) {
            view.findViewById<T>(id).block()
        }
    }
}
