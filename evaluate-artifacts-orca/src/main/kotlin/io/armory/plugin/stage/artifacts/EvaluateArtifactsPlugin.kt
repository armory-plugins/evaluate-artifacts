package io.armory.plugin.stage.artifacts

import com.netflix.spinnaker.kork.plugins.api.spring.SpringLoaderPlugin
import org.pf4j.PluginWrapper
import org.slf4j.LoggerFactory

class EvaluateArtifactsPlugin(wrapper: PluginWrapper) : SpringLoaderPlugin(wrapper) {

  private val logger = LoggerFactory.getLogger(EvaluateArtifactsPlugin::class.java)

  override fun getPackagesToScan(): List<String> {
    return listOf(
      "io.armory.plugin.stage.artifacts"
    )
  }

  override fun start() {
    logger.info("EvaluateArtifactsPlugin.start()")
  }

  override fun stop() {
    logger.info("EvaluateArtifactsPlugin.stop()")
  }
}
