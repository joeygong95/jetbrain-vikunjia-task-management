package me.rainsay.vikunjatask

import com.intellij.openapi.project.Project
import com.intellij.tasks.TaskRepository
import com.intellij.tasks.config.TaskRepositoryEditor
import com.intellij.tasks.impl.BaseRepositoryType
import com.intellij.openapi.util.IconLoader
import com.intellij.util.Consumer
import javax.swing.Icon

class VikunjaRepositoryType : BaseRepositoryType<VikunjaRepository>() {
    override fun getName(): String = "Vikunja"

    override fun getIcon(): Icon = IconLoader.getIcon("/icons/vikunja.svg", VikunjaRepositoryType::class.java)

    override fun createRepository(): TaskRepository = VikunjaRepository(this)

    override fun createEditor(
        repository: VikunjaRepository,
        project: Project,
        changeListener: Consumer<in VikunjaRepository>
    ): TaskRepositoryEditor = VikunjaRepositoryEditor(project, repository, changeListener)

    override fun getRepositoryClass(): Class<VikunjaRepository> = VikunjaRepository::class.java
}
