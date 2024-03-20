package fireal.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import fireal.definition.BeanDefinition;
import fireal.exception.InterceptException;
import fireal.exception.ParamInjectException;
import fireal.structure.Tuple;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;


public class GeneralInterceptor {

    private final Function<BeanDefinition, Object> objectGetter;
    private final List<AspectChunk> beforeAspectChunks;
    private final List<AspectChunk> afterAspectChunks;
    private final List<AspectChunk> aroundAspectChunks;
    private final List<AspectChunk> afterReturnAspectChunks;

    public GeneralInterceptor(Function<BeanDefinition, Object> objectGetter, Collection<AspectChunk> aspectChunks) {
        this.objectGetter = objectGetter;
        var map = aspectChunks.stream().collect(Collectors.groupingBy(AspectChunk::getInterceptorMode));
        beforeAspectChunks = map.get(InterceptorMode.BEFORE);
        afterAspectChunks = map.get(InterceptorMode.AFTER);
        aroundAspectChunks = map.get(InterceptorMode.AROUND);
        afterReturnAspectChunks = map.get(InterceptorMode.AFTER_RETURN);
    }

    @RuntimeType
    public Object intercept(@AllArguments Object[] args, @SuperMethod Method method, @This Object self) {
        Object result = null;

        if (beforeAspectChunks != null) {
            interceptOnBefore(args, self, beforeAspectChunks);
        }

        if (aroundAspectChunks != null) {
            result = interceptOnAround(args, self, method, aroundAspectChunks);
        } else {
            try {
                result = method.invoke(self, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (afterReturnAspectChunks != null) {
            result = interceptOnAfterReturn(args, self, result, afterReturnAspectChunks);
        }

        if (afterAspectChunks != null) {
            interceptOnAfter(args, self, result, afterAspectChunks);
        }

        return result;
    }

    private void interceptOnBefore(Object[] args, Object self, List<AspectChunk> aspectChunks) {
        for (var aspectChunk : aspectChunks) {
            Collection<Tuple<ParamInjectRule, Object>> paramTuples = aspectChunk.getParamInjectTuples();
            Object[] paramArray = paramTuples != null ? new Object[paramTuples.size()] : null;

            if (paramArray != null) {
                int index = 0;
                for (Tuple<ParamInjectRule, Object> tuple : paramTuples) {
                    if (tuple.getFirstKey() == ParamInjectRule.BY_ARGS) {
                        paramArray[index] = args;
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_NUM) {
                        paramArray[index] = args[(int) tuple.getSecondKey() - 1];
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_SELF) {
                        paramArray[index] = self;
                    } else {
                        throw new ParamInjectException(tuple.getFirstKey(), InterceptorMode.BEFORE);
                    }
                    index++;
                }
            }

            Method interceptMethod = aspectChunk.getInterceptorMethod();
            Object invoker = objectGetter.apply(aspectChunk.getAspectDef());
            try {
                interceptMethod.invoke(invoker, paramArray);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new InterceptException(interceptMethod, invoker, paramArray);
            }
        }
    }

    private Object interceptOnAround(Object[] args, Object self, Method method, List<AspectChunk> aspectChunks) {
        Object result = null;
        
        for (var aspectChunk : aspectChunks) {
            Collection<Tuple<ParamInjectRule, Object>> paramTuples = aspectChunk.getParamInjectTuples();
            Object[] paramArray = paramTuples != null ? new Object[paramTuples.size()] : null;

            if (paramArray != null) {
                int index = 0;
                for (Tuple<ParamInjectRule, Object> tuple : paramTuples) {
                    if (tuple.getFirstKey() == ParamInjectRule.BY_ARGS) {
                        paramArray[index] = args;
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_NUM) {
                        paramArray[index] = args[(int) tuple.getSecondKey() - 1];
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_SELF) {
                        paramArray[index] = self;
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_JOIN) {
                        JoinPoint joinPoint = new JoinPoint(self, method, args);
                        paramArray[index] = joinPoint;
                    }
                    index++;
                }
            }

            Method interceptMethod = aspectChunk.getInterceptorMethod();
            Object invoker = objectGetter.apply(aspectChunk.getAspectDef());
            try {
                result = interceptMethod.invoke(invoker, paramArray);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new InterceptException(interceptMethod, invoker, paramArray);
            }
        }
        return result;
    }

    private Object interceptOnAfterReturn(Object[] args, Object self, Object result, List<AspectChunk> aspectChunks) {
        for (var aspectChunk : aspectChunks) {
            Collection<Tuple<ParamInjectRule, Object>> paramTuples = aspectChunk.getParamInjectTuples();
            Object[] paramArray = paramTuples != null ? new Object[paramTuples.size()] : null;

            if (paramArray != null) {
                int index = 0;
                for (Tuple<ParamInjectRule, Object> tuple : paramTuples) {
                    if (tuple.getFirstKey() == ParamInjectRule.BY_ARGS) {
                        paramArray[index] = args;
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_NUM) {
                        paramArray[index] = args[(int) tuple.getSecondKey()];
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_RETURN) {
                        paramArray[index] = result;
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_SELF) {
                        paramArray[index] = self;
                    } else {
                        throw new ParamInjectException(tuple.getFirstKey(), InterceptorMode.AFTER_RETURN);
                    }
                    index++;
                }
            }

            Method interceptMethod = aspectChunk.getInterceptorMethod();
            Object invoker = objectGetter.apply(aspectChunk.getAspectDef());
            try {
                result = interceptMethod.invoke(invoker, paramArray);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new InterceptException(interceptMethod, invoker, paramArray);
            }
        }

        return result;
    }

    private void interceptOnAfter(Object[] args, Object self, Object result, List<AspectChunk> aspectChunks) {
        for (var aspectChunk : aspectChunks) {
            Collection<Tuple<ParamInjectRule, Object>> paramTuples = aspectChunk.getParamInjectTuples();
            Object[] paramArray = paramTuples != null ? new Object[paramTuples.size()] : null;

            if (paramArray != null) {
                int index = 0;
                for (Tuple<ParamInjectRule, Object> tuple : paramTuples) {
                    if (tuple.getFirstKey() == ParamInjectRule.BY_ARGS) {
                        paramArray[index] = args;
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_NUM) {
                        paramArray[index] = args[(int) tuple.getSecondKey()];
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_RETURN) {
                        paramArray[index] = result;
                    } else if (tuple.getFirstKey() == ParamInjectRule.BY_SELF) {
                        paramArray[index] = self;
                    } else {
                        throw new ParamInjectException(tuple.getFirstKey(), InterceptorMode.AFTER);
                    }
                    index++;
                }
            }

            Method interceptMethod = aspectChunk.getInterceptorMethod();
            Object invoker = objectGetter.apply(aspectChunk.getAspectDef());
            try {
                interceptMethod.invoke(invoker, paramArray);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new InterceptException(interceptMethod, invoker, paramArray);
            }
        }
    }
}
