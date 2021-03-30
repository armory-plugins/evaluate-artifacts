import React from 'react';
import {
  ExecutionDetailsTasks,
  IStageTypeConfig,
} from '@spinnaker/core';
import { EvaluateArtifactsStageConfig } from './EvaluateArtifactsStageConfig';
import { EvaluateArtifactsExecutionDetails } from './EvaluateArtifactsExecutionDetails';

// eslint-disable-next-line @typescript-eslint/no-namespace
export namespace EvaluateArtifactsStage {
  export const title = 'Evaluate Artifacts';
}

export const evaluateArtifactsStage: IStageTypeConfig = {
  key: 'evaluateArtifacts',
  label: `Evaluate Artifacts`,
  description: 'Create artifacts from contents.',
  component: EvaluateArtifactsStageConfig,
  executionDetailsSections: [EvaluateArtifactsExecutionDetails, ExecutionDetailsTasks],
  producesArtifacts: true,
};
