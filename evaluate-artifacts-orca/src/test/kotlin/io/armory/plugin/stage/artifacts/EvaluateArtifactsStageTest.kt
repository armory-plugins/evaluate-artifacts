package io.armory.plugin.stage.artifacts

import com.netflix.spinnaker.kork.artifacts.model.ExpectedArtifact
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus
import com.netflix.spinnaker.orca.api.test.stage
import dev.minutest.junit.JUnit5Minutests
import dev.minutest.rootContext
import io.armory.plugin.stage.artifacts.pipeline.stage.EvaluateArtifactsStage
import io.armory.plugin.stage.artifacts.pipeline.task.EvaluateArtifactsTask
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class EvaluateArtifactsStageTest : JUnit5Minutests {

  fun tests() = rootContext {
    test("execute evaluate artifacts stage") {

      val stage = stage {
        type = "evaluateArtifacts"
        context = mapOf(
          "artifactContents" to emptyList<EvaluateArtifactsStage.EvaluateArtifactsStageContext.Content>(),
          "expectedArtifacts" to emptyList<ExpectedArtifact>()
        )
      }

      val task = EvaluateArtifactsTask()
      expectThat(task.execute(stage)) {
        get { status }.isEqualTo(ExecutionStatus.SUCCEEDED)
      }
    }
  }
}
