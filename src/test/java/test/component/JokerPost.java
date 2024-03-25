package test.component;

import fireal.processor.BeanPostProcessor;


public class JokerPost implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        if (beanName.equals("joker")) {
            ((Joker) bean).setName("Joker");
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
