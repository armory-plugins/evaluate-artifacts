package io.armory.plugin.stage.artifacts.pipeline.task

import com.netflix.spinnaker.kork.artifacts.model.Artifact
import com.netflix.spinnaker.kork.plugins.api.spring.ExposeToApp
import com.netflix.spinnaker.orca.api.pipeline.Task
import com.netflix.spinnaker.orca.api.pipeline.TaskResult
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution
import io.armory.plugin.stage.artifacts.pipeline.stage.EvaluateArtifactsStage
import java.util.*
import java.util.stream.Collectors
import org.springframework.stereotype.Component

@Component
@ExposeToApp
class EvaluateArtifactsTask() : Task {

  override fun execute(stage: StageExecution): TaskResult {
    val context = stage.mapTo(EvaluateArtifactsStage.EvaluateArtifactsStageContext::class.java)

    val evaluated = context.artifactContents.stream()
    .map { content -> artifactFromContent(content, stage) }
      .collect(Collectors.toList())

    return TaskResult.builder(ExecutionStatus.SUCCEEDED)
    .outputs(Collections.singletonMap("artifacts", evaluated))
    .build()
  }
  private fun artifactFromContent(
    content: EvaluateArtifactsStage.EvaluateArtifactsStageContext.Content,
    stage: StageExecution
  ): Artifact {
    return Artifact.builder()
    .type("embedded/base64")
    .name(content.name)
    .reference(Base64.getEncoder().encodeToString(content.contents.toByteArray()))
    .build()
  }
}
