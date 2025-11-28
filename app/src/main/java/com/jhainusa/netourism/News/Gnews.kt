package com.jhainusa.netourism.News

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.http.GET
import retrofit2.http.Query

data class GNewsResponse(
    val totalArticles: Int,
    val articles: List<GNewsArticle>
)

data class GNewsArticle(
    val title: String,
    val description: String?,
    val url: String,
    val image: String?,
    val publishedAt: String,
    val source: GNewsSource
)

data class GNewsSource(
    val name: String,
    val url: String
)

interface GNewsApi {

    @GET("search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("lang") lang: String = "en",
        @Query("token") token: String = "78c9832a22133a68b05f0b71977fbc88"
    ): GNewsResponse
}
object RetrofitClient {
    private const val BASE_URL = "https://gnews.io/api/v4/"

    val api: GNewsApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(GNewsApi::class.java)
    }
}
class NewsRepository {

    suspend fun getNorthEastNews(): List<GNewsArticle> {
        val query = "Assam OR Meghalaya OR Nagaland OR Manipur OR Mizoram OR Tripura OR Arunachal Pradesh OR North East India"
        val response = RetrofitClient.api.searchNews(query)
        return response.articles
    }
}
class NewsViewModel : ViewModel() {

    private val repo = NewsRepository()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _news = MutableStateFlow<List<GNewsArticle>>(emptyList())
    val news: StateFlow<List<GNewsArticle>> = _news

    fun loadNews() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                delay(1200)
                _news.value = repo.getNorthEastNews()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}