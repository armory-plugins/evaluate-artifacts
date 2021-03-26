import React, { useState } from 'react';
import {
  FormikFormField,
  TextInput,
  TextAreaInput,
  ExpectedArtifactService,
  UUIDGenerator,
  WizardModal,
  TaskMonitor
} from '@spinnaker/core';
export interface Artifact {
  name: string;
  contents: string;
  id: string;
}

export const EvaluateArtifactsStageModal = ({ toggleModal, stage, artifact }: any) => {
  const [taskMonitor, setTaskMonitor] = useState(new TaskMonitor({ title: "I'm never used" }))
  const [isSubmitting, setSubmittingState] = useState<boolean>(false)
  const { expectedArtifacts, artifactContents } = stage;
  const generateArtifact = (values: Artifact) => {
    const { name, id, contents } = values;
    const evaluateArtifactsIndex = artifactContents.findIndex((item: Artifact) => item.id === id);
    const producedArtifactIndex =  expectedArtifacts.findIndex((item: any) => item.id === id);
    const hasProducedArtifact = producedArtifactIndex >= 0 && evaluateArtifactsIndex >= 0;

    setSubmittingState(true);

    if (hasProducedArtifact) {
      stage.artifactContents[evaluateArtifactsIndex].name = name;
      stage.artifactContents[evaluateArtifactsIndex].contents = contents;
      stage.expectedArtifacts[producedArtifactIndex].displayName = name;
      stage.expectedArtifacts[producedArtifactIndex].matchArtifact.name = name;
    } else {
      stage.artifactContents.push(values);
      const newArtifact = {
        id,
        displayName: name,
        usePriorArtifact: false,
        useDefaultArtifact: false,
        matchArtifact: {
          artifactAccount: 'embedded-artifact',
          id: UUIDGenerator.generateUuid(),
          customKind: true,
          name,
          type: 'embedded/base64',
        },
        defaultArtifact: {
          id: UUIDGenerator.generateUuid(),
          customKind: true,
        }
      };
      ExpectedArtifactService.addArtifactTo(newArtifact, stage);
    }
    toggleModal();
  }

  return (
    <div className="fade in modal" tabIndex={-1} role="dialog">
      <div className="modal-dialog" role="document">
        <div className="modal-content">
          <WizardModal
            heading="New Artifact"
            initialValues={artifact}
            taskMonitor={taskMonitor}
            dismissModal={toggleModal}
            closeModal={generateArtifact}
            submitButtonLabel="Create Artifact"
            render={(props) => (
              <div className="row">
                <div className="col-sm-12">
                  <div>
                    <label htmlFor="name">Name</label>
                    <FormikFormField
                      name="name"
                      input={(inputProps) => <TextInput {...inputProps} />}
                      required={true}
                    />
                  </div>
                  <div style={{marginTop: '10px'}}>
                    <label htmlFor="contents">Contents</label>
                    <FormikFormField
                      name="contents"
                      input={(inputProps) => <TextAreaInput {...inputProps} />}
                      required={true}
                    />
                  </div>
                </div>
              </div>
            )}
          />
        </div>
      </div>
    </div>
  )
}
