package io.armory.plugin.stage.artifacts;

import com.netflix.spinnaker.kork.plugins.api.spring.SpringLoader;
import com.netflix.spinnaker.kork.plugins.api.spring.SpringLoaderPlugin;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.List;

public class EvaluateArtifactsPlugin extends SpringLoaderPlugin {

    private static final String SPRING_LOADER_BEAN_NAME = String.format("Armory.EvaluateArtifactsPlugin.%s", SpringLoader.class.getName());
    private static final List<String> ORCA_BEANS_DEPENDING_ON_PLUGIN = List.of();

    public EvaluateArtifactsPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public List<String> getPackagesToScan() {
        return List.of(
                "io.armory.plugin.commons",
                "io.armory.plugin.stage.artifacts");
    }

    @Override
    public void start() {
        log.info("EvaluateArtifactsPlugin.start()");
    }

    @Override
    public void stop() {
        log.info("EvaluateArtifactsPlugin.stop()");
    }

    @Override
    public void registerBeanDefinitions(BeanDefinitionRegistry registry) {
        super.registerBeanDefinitions(registry);

        ORCA_BEANS_DEPENDING_ON_PLUGIN.forEach(bean -> {
            if (registry.containsBeanDefinition(bean)) {
                registry
                        .getBeanDefinition(bean)
                        .setDependsOn(SPRING_LOADER_BEAN_NAME);
            }
        });
    }
}