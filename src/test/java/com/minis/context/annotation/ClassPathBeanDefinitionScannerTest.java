package com.minis.context.annotation;

import com.minis.app.App;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.core.io.Resource;
import com.minis.utils.ClassUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathBeanDefinitionScannerTest {

    BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

    String packageName = ClassUtils.getPackageName(App.class.getName());

    ClassPathBeanDefinitionScannerTest() {
        scanner.addExcludeFilter(App.class.getName());
    }

    @Test
    void test_addIncludeFilter() throws URISyntaxException {
        String resourcePath = packageName.replace('.', '/');
        URL url = this.getClass().getClassLoader().getResource(resourcePath);
        try (Stream<Path> paths = Files.walk(Path.of(url.toURI()))) {
            paths.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void test_doScan() throws IOException {
        Set<BeanDefinition> beanDefinitions = scanner.doScan(Set.of(packageName));
        assertFalse(beanDefinitions.isEmpty());
        assertNotNull(registry.getBeanDefinition("appConfig"));
    }

    @Test
    void test_selectCandidates() throws IOException {
        Set<Resource> resources = scanner.findAllClassPathResources(packageName);
        Set<BeanDefinition> beanDefinitions = scanner.selectCandidates(resources);
        assertFalse(beanDefinitions.isEmpty());
    }

}