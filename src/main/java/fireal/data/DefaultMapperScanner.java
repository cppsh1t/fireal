package fireal.data;

import java.util.Collection;

import org.apache.ibatis.annotations.Mapper;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

public class DefaultMapperScanner implements MapperScanner {

    @Override
    public Collection<Class<?>> scanMapperClasses(String... packNames) {
        ClassGraph classGraph = new ClassGraph();
        classGraph.acceptPackages(packNames);
        classGraph.enableAnnotationInfo();
        ScanResult scanResult = classGraph.scan();
        ClassInfoList  classInfoList = scanResult.getClassesWithAnnotation(Mapper.class);
        return classInfoList.loadClasses();
    }

    @Override
    public Collection<Class<?>> scanMapperClasses(Collection<String> packNames) {
        ClassGraph classGraph = new ClassGraph();
        for(String packName : packNames) {
            classGraph.acceptPackages(packName);
        }
        classGraph.enableAnnotationInfo();
        ScanResult scanResult = classGraph.scan();
        ClassInfoList  classInfoList = scanResult.getClassesWithAnnotation(Mapper.class);
        return classInfoList.loadClasses();
    }

}
