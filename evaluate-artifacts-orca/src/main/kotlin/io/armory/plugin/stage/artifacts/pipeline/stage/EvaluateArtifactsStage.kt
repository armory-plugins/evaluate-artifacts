package io.armory.plugin.stage.artifacts.pipeline.stage

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.kork.artifacts.model.ExpectedArtifact
import com.netflix.spinnaker.kork.expressions.ExpressionEvaluationSummary
import com.netflix.spinnaker.kork.plugins.api.spring.ExposeToApp
import com.netflix.spinnaker.orca.api.pipeline.graph.TaskNode
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution
import com.netflix.spinnaker.orca.pipeline.ExpressionAwareStageDefinitionBuilder
import com.netflix.spinnaker.orca.pipeline.expressions.PipelineExpressionEvaluator
import com.netflix.spinnaker.orca.pipeline.tasks.artifacts.BindProducedArtifactsTask
import com.netflix.spinnaker.orca.pipeline.util.ContextParameterProcessor
import io.armory.plugin.stage.artifacts.pipeline.task.EvaluateArtifactsTask
import org.jetbrains.annotations.NotNull
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList

@Component
@ExposeToApp
class EvaluateArtifactsStage(mapper: ObjectMapper) : ExpressionAwareStageDefinitionBuilder() {

  /**
   * This function describes the sequence of substeps, or "tasks" that comprise this
   * stage. The task graph is generally linear though there are some looping mechanisms.
   *
   * This method is called just before a stage is executed. The task graph can be generated
   * programmatically based on the stage's context.
   */
  private val mapper: ObjectMapper
  init {
    this.mapper = mapper
  }

  override fun taskGraph(stage: StageExecution, builder: TaskNode.Builder) {
    builder
    .withTask("evaluateArtifacts", EvaluateArtifactsTask::class.java)
    .withTask("bindArtifacts", BindProducedArtifactsTask::class.java)
  }
  override fun processExpressions(
    @NotNull stage: StageExecution,
    @NotNull contextParameterProcessor: ContextParameterProcessor,
    @NotNull summary: ExpressionEvaluationSummary
  ): Boolean {
    var lastFailedCount = 0
    val context = stage.mapTo(EvaluateArtifactsStageContext::class.java)
    val augmentedContext = contextParameterProcessor.buildExecutionContext(stage)
    val contents = Optional.ofNullable(context.artifactContents).orElse(Collections.emptyList())
    val successfulEvaluations = ArrayList<EvaluateArtifactsStageContext.Content>()
    for (content in contents) {
      val evaluated = contextParameterProcessor.process(
        Collections.singletonMap("content", content.contents) as Map<String, Any>?,
        augmentedContext,
        true,
        summary)
      val evaluationSucceeded = summary.failureCount == lastFailedCount
      if (evaluationSucceeded) {
        content.contents = evaluated["content"] as String
        successfulEvaluations.add(content)
      } else {
        lastFailedCount = summary.getFailureCount()
      }
    }
    context.artifactContents = successfulEvaluations
    val evaluatedContext = mapper.convertValue(context, object : TypeReference<Map<String, Any>>() {
    })
    stage.getContext().putAll(evaluatedContext)
    if (summary.getFailureCount() > 0) {
      stage
      .getContext()
      .put(
        PipelineExpressionEvaluator.SUMMARY,
        mapper.convertValue(summary.getExpressionResult(), Map::class.java))
    }
    return false
  }
  data class EvaluateArtifactsStageContext(var artifactContents: List<Content>, val expectedArtifacts: List<ExpectedArtifact>) {
    data class Content(val name: String? = "", var contents: String = "")
  }
}
