package me.rainsay.vikunjatask

import com.intellij.openapi.project.Project
import com.intellij.tasks.TaskBundle
import com.intellij.tasks.config.BaseRepositoryEditor
import com.intellij.util.Consumer

class VikunjaRepositoryEditor(
    project: Project,
    repository: VikunjaRepository,
    changeListener: Consumer<in VikunjaRepository>
) : BaseRepositoryEditor<VikunjaRepository>(project, repository, changeListener) {

    init {
        myPasswordLabel.text = TaskBundle.message("label.token")
        myUsernameLabel.isVisible = false
        myUserNameText.isVisible = false
        myTestButton.isEnabled = myRepository.isConfigured
    }

    override fun apply() {
        super.apply()
        myTestButton.isEnabled = myRepository.isConfigured
    }
}
