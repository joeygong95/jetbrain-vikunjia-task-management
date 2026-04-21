package me.rainsay.vikunjatask

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.util.text.StringUtil
import com.intellij.tasks.Task
import com.intellij.tasks.TaskRepositoryType
import com.intellij.tasks.impl.gson.TaskGsonUtil
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl
import com.intellij.util.xmlb.annotations.Tag
import org.apache.http.HttpRequestInterceptor
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Tag("Vikunja")
class VikunjaRepository : NewBaseRepositoryImpl {

    companion object {
        private val GSON: Gson = TaskGsonUtil.createDefaultBuilder().create()
        private val LIST_TYPE = object : TypeToken<List<VikunjaTaskData>>() {}
    }

    @Suppress("unused")
    constructor() : super()

    constructor(type: TaskRepositoryType<*>) : super(type)

    constructor(other: VikunjaRepository) : super(other)

    override fun clone(): VikunjaRepository = VikunjaRepository(this)

    override fun getRestApiPathPrefix(): String = "/api/v1/"

    override fun createRequestInterceptor(): HttpRequestInterceptor {
        return HttpRequestInterceptor { request, _ ->
            request.addHeader("Authorization", "Bearer $myPassword")
        }
    }

    override fun isConfigured(): Boolean = super.isConfigured() && StringUtil.isNotEmpty(myPassword)

    override fun getIssues(query: String?, offset: Int, limit: Int, withClosed: Boolean): Array<Task> {
        val uri = URIBuilder(getRestApiUrl("tasks"))
            .addParameter("page", ((offset / limit) + 1).toString())
            .addParameter("per_page", limit.toString())
            .addParameter("sort_by", "updated")
            .addParameter("order_by", "desc")
        if (!query.isNullOrBlank()) {
            uri.addParameter("s", query)
        }
        if (!withClosed) {
            uri.addParameter("filter", "done = false")
        }
        val request = HttpGet(uri.build())
        val response = httpClient.execute(request)
        val body = response.entity.content.bufferedReader().readText()
        val tasks: List<VikunjaTaskData> = GSON.fromJson(body, LIST_TYPE.type)
        return tasks.map { it.toTask(this) }.toTypedArray()
    }

    override fun findTask(id: String): Task? {
        val request = HttpGet(getRestApiUrl("tasks", id))
        val response = httpClient.execute(request)
        val body = response.entity.content.bufferedReader().readText()
        val data = GSON.fromJson(body, VikunjaTaskData::class.java) ?: return null
        return data.toTask(this)
    }

    override fun createCancellableConnection(): CancellableConnection {
        return HttpTestConnection(HttpGet(getRestApiUrl("tasks") + "?per_page=1"))
    }

    data class VikunjaTaskData(
        val id: Long = 0,
        val title: String = "",
        val description: String? = null,
        val done: Boolean = false,
        val created: String? = null,
        val updated: String? = null
    ) {
        fun toTask(repo: VikunjaRepository): VikunjaTask = VikunjaTask(
            repository = repo,
            id = id,
            title = title,
            description = description,
            done = done,
            created = parseDate(created),
            updated = parseDate(updated)
        )

        private fun parseDate(dateStr: String?): Date? {
            if (dateStr.isNullOrBlank() || dateStr.startsWith("0001")) return null
            return try {
                val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                fmt.timeZone = TimeZone.getTimeZone("UTC")
                fmt.parse(dateStr)
            } catch (_: Exception) {
                null
            }
        }
    }
}
