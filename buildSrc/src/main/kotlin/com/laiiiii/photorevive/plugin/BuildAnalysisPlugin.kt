package com.laiiiii.photorevive.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import java.io.File

class BuildAnalysisPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        createCleanCacheTask(project)
        createDependencyAnalysisTask(project)
        bindTasksToBuildLifecycle(project)
    }

    private fun createCleanCacheTask(project: Project) {
        val cleanTask = project.tasks.register("cleanBuildCache", Delete::class.java)
        cleanTask.configure {
            description = "清理构建缓存目录"
            group = "build"

            doLast {
                delete(
                    File(project.buildDir, "tmp"),
                    File(project.buildDir, "intermediates"),
                    File(project.buildDir, "generated")
                )
                println("已清理构建缓存目录")
            }
        }
    }

    private fun createDependencyAnalysisTask(project: Project) {
        val analyzeTask = project.tasks.register("analyzeDependencies")
        analyzeTask.configure {
            description = "分析并输出项目依赖树"
            group = "build"

            doLast {
                val outputFile = File(project.buildDir, "reports/dependencies.txt")
                outputFile.parentFile.mkdirs()

                val sb = StringBuilder()
                project.configurations
                    .filter { it.isCanBeResolved }
                    .forEach { config ->
                        try {
                            sb.append("Configuration: ${config.name}\n")
                            config.resolvedConfiguration
                                .lenientConfiguration
                                .allModuleDependencies
                                .forEach { dep ->
                                    sb.append("  ${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}\n")
                                }
                            sb.append("\n")
                        } catch (e: Exception) {
                            sb.append("  Error: ${e.message}\n\n")
                        }
                    }

                outputFile.writeText(sb.toString())
                println("依赖分析报告已生成: ${outputFile.absolutePath}")
            }
        }
    }

    private fun bindTasksToBuildLifecycle(project: Project) {
        project.afterEvaluate {
            val preBuild = project.tasks.findByName("preBuild")
            preBuild?.dependsOn("cleanBuildCache")

            project.tasks.findByName("assembleDebug")?.finalizedBy("analyzeDependencies")
            project.tasks.findByName("assembleRelease")?.finalizedBy("analyzeDependencies")
        }
    }
}