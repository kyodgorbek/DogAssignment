package ceo.dog.dogapp

import ceo.dog.dogapp.data.AllBreedResponse
import ceo.dog.dogapp.data.BreedImagesResponse
import ceo.dog.dogapp.data.DogApiService
import ceo.dog.dogapp.data.RepositoryImpl
import ceo.dog.dogapp.data.Resource
import ceo.dog.dogapp.data.Status
import ceo.dog.dogapp.domain.Repository
import ceo.dog.dogapp.domain.model.Breed
import ceo.dog.dogapp.ui.main.MainViewModel
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import okio.IOException
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RepositoryTests {
    @get:Rule
    val testCoroutineRule = CoroutineRuleTest()
    private val dispatcher = TestCoroutineDispatcher()

    @Test
    fun `test fetch all breed success`() = testCoroutineRule.runBlockingTest {
        val api = mock<DogApiService> {
            onBlocking { fetchAllBreed() } doReturn AllBreedResponse(
                mutableMapOf("dog" to listOf("dog 1", "dog 2")),
                Status.SUCCESS
            )
        }
        val repo = RepositoryImpl(api, dispatcher)
        val flow = repo.getAllBreed()
        val result = flow.single()
        assert(result.status == Status.SUCCESS)
        assert(result.data!!.isNotEmpty())
    }

    @Test
    fun `test fetch all breed failed`() = testCoroutineRule.runBlockingTest {
        val api = mock<DogApiService> {
            onBlocking { fetchAllBreed() } doAnswer { throw IOException() }
        }
        val repo = RepositoryImpl(api, dispatcher)
        val flow = repo.getAllBreed()
        val result = flow.single()
        assert(result.status == Status.ERROR)
        assert(result.data == null)
    }

    @Test
    fun `test get breed image success`() = testCoroutineRule.runBlockingTest {
        val api = mock<DogApiService> {
            onBlocking { getImagesByBreed("dog") } doReturn BreedImagesResponse(listOf("somre"), Status.SUCCESS)
        }
        val repo = RepositoryImpl(api, dispatcher)
        val flow = repo.getBreedImages("dog")
        val result = flow.single()
        assert(result.status == Status.SUCCESS)
        assert(result.data!!.isNotEmpty())
    }

    @Test
    fun `test get breed image failed`() = testCoroutineRule.runBlockingTest {
        val api = mock<DogApiService> {
            onBlocking { getImagesByBreed("dog") } doAnswer { throw IOException() }
        }
        val repo = RepositoryImpl(api, dispatcher)
        val flow = repo.getBreedImages("dog")
        val result = flow.single()
        assert(result.status == Status.ERROR)
        assert(result.data == null)
    }
}

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ViewModelTest() {
    @get:Rule
    val testCoroutineRule = CoroutineRuleTest()
    private val repo = mock<Repository>()
    private val viewModel = MainViewModel(repo)

    @Test
    fun `test all breed success`() = testCoroutineRule.runBlockingTest {
        val result = Resource.success(
            listOf(
                Breed("dog 1", emptyList()),
                Breed("dog 2", listOf(Breed("dog 2 sub", emptyList())))
            )
        )
        val channel = Channel<Resource<List<Breed>>>()
        val flow = channel.consumeAsFlow()
        whenever(repo.getAllBreed()) doReturn flow
        launch { channel.send(result) }
        viewModel.getAllBreed()
        assert(viewModel.state.value.dogList.isNotEmpty())
        assert(viewModel.viewEvents.first() is MainViewModel.UIEvents.Loading)
        assert(viewModel.state.value.dogList.size == 2)
    }

    @Test
    fun `test all breed error`() = testCoroutineRule.runBlockingTest {
        val result = Resource.error<List<Breed>>("error")
        val channel = Channel<Resource<List<Breed>>>()
        val flow = channel.consumeAsFlow()
        whenever(repo.getAllBreed()) doReturn flow
        launch { channel.send(result) }
        viewModel.getAllBreed()
        assert(viewModel.viewEvents.first() is MainViewModel.UIEvents.Loading)
        assert(viewModel.viewEvents.first() is MainViewModel.UIEvents.Error)
        assert(viewModel.state.value.dogList.isEmpty())
    }

    @Test
    fun `test get breed success`() = testCoroutineRule.runBlockingTest {
        val breed = "dog"
        val result = Resource.success(listOf("some url"))
        val channel = Channel<Resource<List<String>>>()
        val flow = channel.consumeAsFlow()
        whenever(repo.getBreedImages(breed)) doReturn flow
        launch { channel.send(result) }
        viewModel.breedDescription(breed)
        assert(viewModel.viewEvents.first() is MainViewModel.UIEvents.Loading)
        assert(viewModel.viewEvents.first() is MainViewModel.UIEvents.BreedDescriptionLoaded)
        with(viewModel.state.value) {
            assert(imageList.isNotEmpty())
            assert(imageList.size == 1)
            assert(selectedBreed == breed)
        }
    }

    @Test
    fun `test get breed failed`() = testCoroutineRule.runBlockingTest {
        val breed = "dog"
        val result = Resource.error<List<String>>("error")
        val channel = Channel<Resource<List<String>>>()
        val flow = channel.consumeAsFlow()
        whenever(repo.getBreedImages(breed)) doReturn flow
        launch { channel.send(result) }
        viewModel.breedDescription(breed)
        assert(viewModel.viewEvents.first() is MainViewModel.UIEvents.Loading)
        assert(viewModel.viewEvents.first() is MainViewModel.UIEvents.Error)
        with(viewModel.state.value) {
            assert(imageList.isEmpty())
            assert(selectedBreed == null)
        }
    }
}
