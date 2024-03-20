package fireal.exception;

import fireal.proxy.InterceptorMode;
import fireal.proxy.ParamInjectRule;

public class ParamInjectException extends RuntimeException{

    public ParamInjectException(ParamInjectRule paramInjectRule, InterceptorMode interceptorMode) {
        super("Can't do " + paramInjectRule + " in InterceptMode: " + interceptorMode + ".");
    }

}
