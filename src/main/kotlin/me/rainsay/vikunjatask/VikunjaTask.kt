package me.rainsay.vikunjatask

import com.intellij.tasks.Comment
import com.intellij.tasks.Task
import com.intellij.tasks.TaskRepository
import com.intellij.tasks.TaskType
import icons.TasksCoreIcons
import java.util.Date
import javax.swing.Icon

class VikunjaTask(
    private val repository: VikunjaRepository,
    private val id: Long,
    private val title: String,
    private val description: String?,
    private val done: Boolean,
    private val created: Date?,
    private val updated: Date?
) : Task() {
    override fun getId(): String = id.toString()
    override fun getSummary(): String = title
    override fun getDescription(): String? = description
    override fun getComments(): Array<Comment> = Comment.EMPTY_ARRAY
    override fun getIcon(): Icon = TasksCoreIcons.Bugzilla
    override fun getType(): TaskType = TaskType.OTHER
    override fun getUpdated(): Date? = updated
    override fun getCreated(): Date? = created
    override fun isClosed(): Boolean = done
    override fun isIssue(): Boolean = true
    override fun getIssueUrl(): String? = "${repository.url}/tasks/$id"
    override fun getRepository(): TaskRepository = repository
}
