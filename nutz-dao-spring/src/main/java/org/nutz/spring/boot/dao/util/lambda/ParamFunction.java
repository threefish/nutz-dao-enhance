package org.nutz.spring.boot.dao.util.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author 黄川 huchuc@vip.qq.com
 * <p>
 * serializedLambda：{
 * "capturingClass": "org/nutz/spring/boot/cnd/ConditionCall",
 * "functionalInterfaceClass": "org/nutz/spring/boot/cnd/ParamsFunction",
 * "functionalInterfaceMethodName": "apply",
 * "functionalInterfaceMethodSignature": "(Ljava/lang/Object;)Ljava/lang/Object;",
 * "implClass": "org/nutz/spring/boot/dao/test/entity/TestDO",
 * "implMethodName": "getId",
 * "implMethodSignature": "()Ljava/lang/Integer;",
 * "implMethodKind": 5,
 * "instantiatedMethodType": "(Lorg/nutz/spring/boot/dao/test/entity/TestDO;)Ljava/lang/Integer;",
 * "capturedArgs": []
 * }
 */
@FunctionalInterface
public interface ParamFunction<T, R> extends Function<T, R>, Serializable {

}
