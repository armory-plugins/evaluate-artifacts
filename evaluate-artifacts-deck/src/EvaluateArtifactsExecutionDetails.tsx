import { get } from 'lodash';
import * as React from 'react';

import {
  ExecutionDetailsSection,
  IExecutionDetailsSectionProps,
  StageFailureMessage,
} from '@spinnaker/core';

export class EvaluateArtifactsExecutionDetails extends React.Component<IExecutionDetailsSectionProps> {
  public static title = 'terraformStatus';

  public render() {
    const { current, name, stage } = this.props;
    const { outputs } = stage;
    const errorMessage = get(outputs, ['status', 'error'], '');
    return (
      <ExecutionDetailsSection name={name} current={current}>
        <StageFailureMessage stage={stage} message={errorMessage || stage.failureMessage} />
      </ExecutionDetailsSection>
    );
  }
}
