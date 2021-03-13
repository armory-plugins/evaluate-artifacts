import React from 'react';
import {
  FormikStageConfig,
  IStageConfigProps,
} from '@spinnaker/core';
import { EvaluateArtifactsStageForm } from './EvaluateArtifactsStageForm';
import './EvaluateArtifactsStage.scss';

export const EvaluateArtifactsStageConfig = (props: IStageConfigProps) => {
  if (props.stage.isNew) {
    props.stage.artifactContents = [];
    props.stage.expectedArtifacts = [];
  }
  return (
    <div className="EvaluateArtifactsStageConfig">
      <FormikStageConfig
        {...props}
        onChange={props.updateStage}
        render={props => <EvaluateArtifactsStageForm {...props} />}
      />
    </div>
  );
}
