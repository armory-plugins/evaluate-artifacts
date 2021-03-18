![CI](https://github.com/armory-plugins/evaluate-artifacts/workflows/CI/badge.svg)

The [evaluateArtifactsStagePlugin](https://github.com/armory-plugins/evaluate-artifacts), also called the EvaluateArtifacts plugin, creates a custom pipeline stage that evaluates artifacts of a pipeline for SpEL expressions.

# Version Compatibility

| Plugin  | Spinnaker Platform |
|:----------- | :--------- |
| 1.0.x  | 1.24.x |

# Usage

1) Run `./gradlew releaseBundle`
2) Put the `/build/distributions/<project>-<version>.zip` into the [configured plugins location for your service](https://pf4j.org/doc/packaging.html).
3) Configure the Spinnaker service. Put the following in the service yml to enable the plugin and configure the extension:

```
spinnaker:
  extensibility:
    plugins:
      Armory.EvaluateArtifactsPlugin:
        enabled: true
```

Or use the [evaluateArtifactsRepository](https://github.com/armory-plugins/evaluate-artifacts-releases) to avoid copying the plugin `.zip` artifact.

## Deployment on Spinnaker 1.24.0+

See the [Plugin Users Guide](https://spinnaker.io/guides/user/plugins/) and the [pf4jStagePlugin Deployment Example](https://spinnaker.io/guides/user/plugins/deploy-example/).

# Debugging

To debug the `evaluate-artifacts-orca`  server component inside a Spinnaker service (like Orca) using IntelliJ Idea follow these steps:

1) Run `./gradlew releaseBundle` in the plugin project.
2) Copy the generated `.plugin-ref` file under `build` in the plugin project submodule for the service to the `plugins` directory under root in the Spinnaker service that will use the plugin .
3) Link the plugin project to the service project in IntelliJ (from the service project use the `+` button in the Gradle tab and select the plugin build.gradle).
4) Configure the Spinnaker service the same way specified above.
5) Create a new IntelliJ run configuration for the service that has the VM option `-Dpf4j.mode=development` and does a `Build Project` before launch.
6) Debug away...

See the [Test a Pipeline Stage Plugin](https://spinnaker.io/guides/developer/plugin-creators/deck-plugin/) guide for a detailed walkthrough of setting up a plugin local testing environment on your workstation.

# Architecture

The plugin consists of a `evaluate-artifacts-orca` [Kotlin](https://kotlinlang.org/docs/reference/) server component and a `evaluate-artifacts-deck` [React](https://reactjs.org/) UI component that uses the [rollup.js](https://rollupjs.org/guide/en/#plugins-overview) plugin library.

## `evaluate-artifacts-orca`


## `evaluate-artifacts-deck`

Prior to v1.1.4, this component used the [`rollup.js`](https://rollupjs.org/guide/en/#plugins-overview) plugin library to create a UI widget for Deck.

* `rollup.config.js`: configuration for building the JavaScript application
* `package.json`: defines dependencies
* `EvaluateArtifactsStage.tsx`: defines the custom pipeline stage; renders UI output
* `index.ts`: exports the name and custom stages

The code was refactored in v1.1.5 to use the new Deck UI SDK. `rollup.config.js`
now points to the config defined by the UI SDK. It's mostly not necessary to
define your own build config. This is also true of `tsconfig.json`. If you use
the UI SDK, you no longer define how your TypeScript should be compiled.
