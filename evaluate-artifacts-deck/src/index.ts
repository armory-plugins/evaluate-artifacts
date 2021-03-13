import { IDeckPlugin } from '@spinnaker/core';
import { evaluateArtifactsStage } from './EvaluateArtifactsStage';

export const plugin: IDeckPlugin = {
  stages: [evaluateArtifactsStage],
};
