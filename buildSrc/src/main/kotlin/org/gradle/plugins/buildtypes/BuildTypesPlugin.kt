package org.gradle.plugins.buildtypes

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.kotlin.dsl.*


class BuildTypesPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {
        val buildTypes = container(BuildType::class.java)
        extensions.add("buildTypes", buildTypes)
        buildTypes.all {
            register(this)
        }
    }

    private
    fun Project.register(buildType: BuildType) =
        tasks.create(buildType.name) {

            val invokedTaskNames = gradle.startParameter.taskNames
            buildType.findUsedTaskNameAndIndexIn(invokedTaskNames)?.let { (usedName, index) ->
                require(usedName.isNotEmpty())
                if (!isTaskHelpInvocation(invokedTaskNames, index)) {
                    buildType.active = true
                    buildType.onProjectProperties = { properties: ProjectProperties ->
                        properties.forEach(project::setOrCreateProperty)
                    }
                    afterEvaluate {
                        invokedTaskNames.removeAt(index)

                        val subproject = usedName.substringBeforeLast(":", "")
                        insertBuildTypeTasksInto(invokedTaskNames, index, buildType, subproject)

                        gradle.startParameter.setTaskNames(invokedTaskNames)
                    }
                }
            }

            group = "Build Type"

            description = "The $name build type (can only be abbreviated to '${buildType.abbreviation}')"

            doFirst {
                throw GradleException("'$name' is a build type and must be invoked directly, and its name can only be abbreviated to '${buildType.abbreviation}'.")
            }
        }

    private
    fun BuildType.findUsedTaskNameAndIndexIn(taskNames: List<String>): Pair<String, Int>? {
        val candidates = arrayOf(name, abbreviation)
        val nameSuffix = ":$name"
        val abbreviationSuffix = ":$abbreviation"
        return taskNames.indexOfFirst {
            it in candidates || it.endsWith(nameSuffix) || it.endsWith(abbreviationSuffix)
        }.takeIf {
            it >= 0
        }?.let {
            taskNames[it] to it
        }
    }

    private
    fun isTaskHelpInvocation(taskNames: List<String>, taskIndex: Int) =
        taskIndex >= 2
            && taskNames[taskIndex - 1] == "--task"
            && taskNames[taskIndex - 2].let(helpTaskRegex::matches)

    private
    val helpTaskRegex = Regex("h(e(lp?)?)?")

    private
    fun Project.insertBuildTypeTasksInto(
        taskNames: MutableList<String>,
        index: Int,
        buildType: BuildType,
        subproject: String) {

        if (subproject.isEmpty() || findProject(subproject) != null) {
            buildType.tasks.reversed().forEach {
                val path = subproject + it
                if (subproject.isEmpty() || tasks.findByPath(path) != null) {
                    taskNames.add(index, path)
                } else {
                    println("Skipping task '$path' requested by build type $name, as it does not exist.")
                }
            }
        } else {
            println("Skipping execution of build type '${buildType.name}'. Project '$subproject' not found in root project '$name'.")
        }
        if (taskNames.isEmpty()) {
            taskNames.add("help") //do not trigger the default tasks
        }
    }
}


fun Project.setOrCreateProperty(propertyName: String, value: Any) {
    when {
        hasProperty(propertyName) -> setProperty(propertyName, value)
        else                      -> extra.set(propertyName, value)
    }
}
