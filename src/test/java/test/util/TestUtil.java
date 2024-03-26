package test.util;

import java.lang.reflect.Field;
import java.util.Collection;

import fireal.core.BaseContainer;
import fireal.definition.BeanDefinition;
import fireal.definition.BeanDefinitionHolder;

public class TestUtil {

    public static Collection<BeanDefinition> getBeanDefinitions(BaseContainer container) {
        try {
            Field beanDefHolderField = BaseContainer.class.getDeclaredField("beanDefinitionHolder");
            beanDefHolderField.setAccessible(true);
            BeanDefinitionHolder beanDefinitionHolder = (BeanDefinitionHolder) beanDefHolderField.get(container);
            return beanDefinitionHolder.values();
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
