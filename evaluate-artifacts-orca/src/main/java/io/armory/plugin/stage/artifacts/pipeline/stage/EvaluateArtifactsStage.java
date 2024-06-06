package io.armory.plugin.stage.artifacts.pipeline.stage;

import com.netflix.spinnaker.orca.pipeline.tasks.artifacts.BindProducedArtifactsTask;
import io.armory.plugin.stage.artifacts.pipeline.task.EvaluateArtifactsTask;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.kork.expressions.ExpressionEvaluationSummary;
import com.netflix.spinnaker.kork.plugins.api.spring.ExposeToApp;
import com.netflix.spinnaker.orca.api.pipeline.graph.TaskNode;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import com.netflix.spinnaker.orca.pipeline.ExpressionAwareStageDefinitionBuilder;
import com.netflix.spinnaker.orca.pipeline.expressions.PipelineExpressionEvaluator;
import com.netflix.spinnaker.orca.pipeline.model.StageContext;
import com.netflix.spinnaker.orca.pipeline.util.ContextParameterProcessor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ExposeToApp
@Slf4j
public class EvaluateArtifactsStage extends ExpressionAwareStageDefinitionBuilder {

    public static String STAGE_TYPE = "evaluateArtifact";

    private ObjectMapper mapper;


    @Autowired
    public EvaluateArtifactsStage(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void taskGraph(@NotNull StageExecution stage, @NotNull TaskNode.Builder builder) {
        builder
            .withTask("evaluateArtifact", EvaluateArtifactsTask.class)
            .withTask("bindArtifacts", BindProducedArtifactsTask.class);
    }

    @Override
    public boolean processExpressions(
            @NotNull StageExecution stage,
            @NotNull ContextParameterProcessor contextParameterProcessor,
            @NotNull ExpressionEvaluationSummary summary) {

        processDefaultEntries(
                stage, contextParameterProcessor, summary, Collections.singletonList("artifactContents"));

        EvaluateArtifactsStageContext context = stage.mapTo(EvaluateArtifactsStageContext.class);

        StageContext augmentedContext = contextParameterProcessor.buildExecutionContext(stage);

        Map<String, Object> varSourceToEval = new HashMap<>();
        int lastFailedCount = 0;

        List<ArtifactContent> artifactContents =
                Optional.ofNullable(context.getArtifactContents()).orElse(Collections.emptyList());

        for (ArtifactContent artifactContent : artifactContents) {
            if (artifactContent.getContents() instanceof String) {
                varSourceToEval.put("contents", artifactContent.getContents());

                Map<String, Object> evaluatedContents =
                        contextParameterProcessor.process(
                                varSourceToEval,
                                augmentedContext,
                                true,
                                summary
                        );

                boolean evaluationSucceeded = summary.getFailureCount() == lastFailedCount;
                if (evaluationSucceeded) {
                    artifactContent.setContents(evaluatedContents.get("contents"));
                    augmentedContext.put(artifactContent.name, artifactContent.contents);
                } else {
                    lastFailedCount = summary.getFailureCount();
                }
            }
        }
        Map<String, Object> evaluatedContext =
                mapper.convertValue(context, new TypeReference<Map<String, Object>>() {});
        stage.getContext().putAll(evaluatedContext);

        if (summary.getFailureCount() > 0) {
            stage
                    .getContext()
                    .put(
                            PipelineExpressionEvaluator.SUMMARY,
                            mapper.convertValue(summary.getExpressionResult(), Map.class));
        }

        return false;
    }

    public static final class EvaluateArtifactsStageContext {
        private final List<ArtifactContent> artifactContents;

        @JsonCreator
        public EvaluateArtifactsStageContext(
                @JsonProperty("artifactContents") @Nullable List<ArtifactContent> artifactContents) {
            this.artifactContents = artifactContents;
        }

        public @Nullable List<ArtifactContent> getArtifactContents() {
            return artifactContents;
        }
    }

    public static class ArtifactContent {
        /** Variable name: NOT processed by SpEL */
        private String name;

        /** Variable evaluated value (processed by SpEL) */
        private Object contents;

        private String id;

        public ArtifactContent() {}

        public void setName(String name) {
            this.name = name;
        }

        public void setContents(Object contents) {
            this.contents = contents;
        }

        public String getName() {
            return name;
        }

        public Object getContents() {
            return contents;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }
}
