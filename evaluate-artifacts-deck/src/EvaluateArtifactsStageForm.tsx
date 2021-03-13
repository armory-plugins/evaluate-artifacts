
import React, { useState } from 'react';
import {
  IFormikStageConfigInjectedProps,
  UUIDGenerator
} from '@spinnaker/core';
import { EvaluateArtifactsStageModal, Artifact } from './EvaluateArtifactsStageModal'

export const EvaluateArtifactsStageForm = (props: IFormikStageConfigInjectedProps) => {
  const stage = props.formik.values;
  const { artifactContents, expectedArtifacts } = stage;
  const [modalVisible, setModalVisible] = useState(false);
  const [artifacts, setArtifacts] = useState(artifactContents);
  const [selectedArtifact, setSelectedArtifact] = useState({});
  const toggleModal = () => {
    setModalVisible(!modalVisible);
  };
  const showModal = (artifact: Artifact) => {
    const propArtifact = artifact || { name: '', contents: '', id: UUIDGenerator.generateUuid() };
    setSelectedArtifact(propArtifact);
    toggleModal();
  }
  const deleteArtifact = (id: string) => {
    setArtifacts(artifactContents.filter((artifact: Artifact) => artifact.id !== id));
    stage.expectedArtifacts = expectedArtifacts.filter((artifact: any) => artifact.id !== id);
  }
  return (
    <>
      <table className="table table-condensed">
        <thead>
          <tr>
            <th>Display name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {artifacts && artifacts.map((artifact: any) => (
            <tr key={artifact.id}>
              <td>{artifact.name}</td>
              <td>
                <a className="glyphicon glyphicon-edit" onClick={() => showModal(artifact)} />
                <a className="glyphicon glyphicon-trash" onClick={() => deleteArtifact(artifact.id)} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <button
        type="button"
        className="btn btn-block btn-sm add-new"
        onClick={() => showModal(null)}
      >
        <span className="glyphicon glyphicon-plus-sign" /> Add Artifact
      </button>
      {modalVisible && <EvaluateArtifactsStageModal toggleModal={toggleModal} stage={stage} artifact={selectedArtifact} />}
    </>
  );
}