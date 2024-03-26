package fireal.definition;

import fireal.core.BeanScanner;
import io.github.classgraph.ClassGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultBeanScanner implements BeanScanner {

    @Override
    public Collection<BeanDefinition> scanBeanDefinitions(BeanDefinitionBuilder builder, String... allPackageNames) {
        var defList = new ArrayList<BeanDefinition>();
        if (allPackageNames == null || allPackageNames.length == 0) return defList;
        ClassGraph classGraph = new ClassGraph();
        //FIXME: 这里的扫包有问题，等着看看API
        classGraph.acceptPackages(allPackageNames);
        var classInfos = classGraph.scan().getAllClasses();
        var classes = classInfos.loadClasses(true);
        for(var clazz : classes) {
            var def = builder.createFromClass(clazz);
            if (def != null) defList.add(def);
        }
        return defList;
    }

    @Override
    public Collection<BeanDefinition> scanBeanDefinitions(BeanDefinitionBuilder builder, Class<?> configClass, Object invoker) {
        return Arrays.stream(configClass.getDeclaredMethods()).parallel()
                .map(m -> builder.createFromMethod(m, invoker))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
