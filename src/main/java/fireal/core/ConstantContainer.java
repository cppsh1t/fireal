package fireal.core;

import fireal.anno.Profile;
import fireal.util.DebugUtil;
import nonapi.io.github.classgraph.utils.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ConstantContainer extends BaseContainer{

    protected Collection<String> profiles;

    /**
     * 构造一个容器
     */
    public ConstantContainer() {
        super();
    }

    /**
     * 构造一个容器
     * @param configClass 配置类
     * @param autoStart 是否自启动
     */
    public ConstantContainer(Class<?> configClass, boolean autoStart) {
        super(configClass, autoStart);
    }

    /**
     * 构造一个容器，并自启动
     * @param configClass 配置类
     */
    public ConstantContainer(Class<?> configClass) {
        super(configClass);
    }

    @Override
    protected void readConfig(Class<?> clazz) {
        super.readConfig(clazz);

        profiles = new HashSet<>();
        profiles.add("/application.yaml");

        if (clazz.isAnnotationPresent(Profile.class)) {
            String[] profileArray = clazz.getAnnotation(Profile.class).value();
            profiles.addAll(Arrays.asList(profileArray));
            profiles.remove("");
        }
    }

    @Override
    protected void init() {
        super.init();
        ObjectFactory oldObjectFactory = this.objectFactory;
        ConstantObjectFactory newObjectFactory = new ConstantObjectFactory(oldObjectFactory);
        newObjectFactory.addConstant(profiles);
        this.objectFactory = newObjectFactory;
    }
}
