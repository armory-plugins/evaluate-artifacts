/*
 * Copyright 2020 Armory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.armory.plugin.stage.artifacts

import com.fasterxml.jackson.module.kotlin.readValue
import com.netflix.spinnaker.kork.artifacts.model.ExpectedArtifact
import com.netflix.spinnaker.orca.api.test.orcaFixture
import dev.minutest.junit.JUnit5Minutests
import dev.minutest.rootContext
import io.armory.plugin.stage.artifacts.pipeline.stage.EvaluateArtifactsStage
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import strikt.api.expect
import strikt.assertions.isEqualTo

/**
 * This test demonstrates that the EvaluateArtifactsPlugin can be loaded by Orca
 * and that EvaluateArtifactsStage's StageDefinitionBuilder can be retrieved at runtime.
 */
class EvaluateArtifactsStageIntegrationTest : JUnit5Minutests {

  fun tests() = rootContext<OrcaPluginsFixture> {
    context("a running Orca instance") {
      orcaFixture {
        OrcaPluginsFixture()
      }

      test("EvaluateArtifactsStage extension is resolved to the correct type") {
        val stageDefinitionBuilder = stageResolver.getStageDefinitionBuilder(
          EvaluateArtifactsStage::class.java.simpleName, "evaluateArtifacts"
        )

        expect {
          that(stageDefinitionBuilder.type).isEqualTo("evaluateArtifacts")
        }
      }

      test("EvaluateArtifactsStage can be executed as a stage within a live pipeline execution") {
        val response = mockMvc.post("/orchestrate") {
          contentType = MediaType.APPLICATION_JSON
          content = mapper.writeValueAsString(mapOf(
            "application" to "evaluate-artifacts-stage-plugin",
            "stages" to listOf(mapOf(
              "refId" to "1",
              "type" to "evaluateArtifacts",
              "artifactContents" to emptyList<EvaluateArtifactsStage.EvaluateArtifactsStageContext.Content>(),
              "expectedArtifacts" to emptyList<ExpectedArtifact>()
            ))
          ))
        }.andReturn().response

        expect {
          that(response.status).isEqualTo(200)
        }

        val ref = mapper.readValue<ExecutionRef>(response.contentAsString).ref

        var execution: Execution
        do {
          execution = mapper.readValue(mockMvc.get(ref).andReturn().response.contentAsString)
        } while (execution.status != "SUCCEEDED")

        expect {
          that(execution)
            .get { stages.first() }
            .and {
              get { type }.isEqualTo("evaluateArtifacts")
              get { status }.isEqualTo("SUCCEEDED")
            }
        }
      }
    }
  }

  data class ExecutionRef(val ref: String)
  data class Execution(val status: String, val stages: List<Stage>)
  data class Stage(val status: String, val context: EvaluateArtifactsStage.EvaluateArtifactsStageContext, val type: String)
}
