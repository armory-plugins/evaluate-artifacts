package io.armory.plugin.stage.artifacts.pipeline.task;

import com.google.common.base.Strings;
import com.netflix.spinnaker.kork.artifacts.ArtifactTypes;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import com.netflix.spinnaker.kork.plugins.api.spring.ExposeToApp;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import io.armory.plugin.stage.artifacts.pipeline.stage.EvaluateArtifactsStage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ExposeToApp
@Slf4j
public class EvaluateArtifactsTask implements Task {

    @NotNull
    @Override
    public TaskResult execute(@NotNull StageExecution stage) {
        EvaluateArtifactsStage.EvaluateArtifactsStageContext context =
                stage.mapTo(EvaluateArtifactsStage.EvaluateArtifactsStageContext.class);

        Map<String, Object> outputs = new HashMap<>();

        List<Artifact> artifacts = context.getArtifactContents().stream()
                .filter(artifactContent -> !Strings.isNullOrEmpty(artifactContent.getContents().toString()))
                .map(artifactContent -> artifactFromContent(artifactContent))
                .collect(Collectors.toList());
        outputs = Collections.singletonMap("artifacts", artifacts);

        return TaskResult.builder(ExecutionStatus.SUCCEEDED)
                .outputs(outputs)
                .build();
    }

    private Artifact artifactFromContent(EvaluateArtifactsStage.ArtifactContent artifactContent) {
        return Artifact.builder()
                .type(ArtifactTypes.EMBEDDED_BASE64.getMimeType())
                .artifactAccount("embedded-artifact")
                .name(artifactContent.getName())
                .reference(Base64.getEncoder().encodeToString(artifactContent.getContents().toString().getBytes(StandardCharsets.UTF_8)))
                .build();
    }
}
