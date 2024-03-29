package org.nutz.dao.enhance.method.proxy;


import org.nutz.dao.DaoException;
import org.nutz.dao.enhance.factory.EnhanceCoreFactory;
import org.nutz.dao.enhance.method.DaoMethodInvoke;
import org.nutz.dao.enhance.method.DaoMethodInvoker;
import org.nutz.dao.enhance.util.ExceptionUtil;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class DaoProxy<T> implements InvocationHandler, Serializable {


    private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
    private static final Constructor<MethodHandles.Lookup> LOOKUP_CONSTRUCTOR;
    private static final Method PRIVATE_LOOKUP_IN_METHOD;
    private static final long serialVersionUID = -2145536075779023989L;

    static {
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupIn = null;
        }
        PRIVATE_LOOKUP_IN_METHOD = privateLookupIn;

        Constructor<MethodHandles.Lookup> lookup = null;
        if (PRIVATE_LOOKUP_IN_METHOD == null) {
            // JDK 1.8
            try {
                lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                lookup.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.",
                        e);
            } catch (Exception e) {
                lookup = null;
            }
        }
        LOOKUP_CONSTRUCTOR = lookup;
    }

    private final Class<T> mapperInterface;
    private final Map<Method, DaoMethodInvoker> methodCache;
    private final EnhanceCoreFactory enhanceCoreFactory;
    private final String dataSource;

    public DaoProxy(EnhanceCoreFactory enhanceCoreFactory, String dataSource, Class<T> mapperInterface, Map<Method, DaoMethodInvoker> methodCache) {
        this.mapperInterface = mapperInterface;
        this.dataSource = dataSource;
        this.methodCache = methodCache;
        this.enhanceCoreFactory = enhanceCoreFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                return cachedInvoker(method).invoke(proxy, method, args, dataSource);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    private DaoMethodInvoker cachedInvoker(Method method) throws Throwable {
        try {
            // A workaround for https://bugs.openjdk.java.net/browse/JDK-8161372
            // It should be removed once the fix is backported to Java 8 or
            // MyBatis drops Java 8 support. See gh-1929
            DaoMethodInvoker invoker = methodCache.get(method);
            if (invoker != null) {
                return invoker;
            }

            return methodCache.computeIfAbsent(method, m -> {
                if (m.isDefault()) {
                    try {
                        if (PRIVATE_LOOKUP_IN_METHOD == null) {
                            return new DefaultMethodInvoker(getMethodHandleJava8(method));
                        } else {
                            return new DefaultMethodInvoker(getMethodHandleJava9(method));
                        }
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException
                            | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return new PlainMethodInvoker(new DaoMethodInvoke(enhanceCoreFactory, dataSource, mapperInterface, method));
                }
            });
        } catch (RuntimeException re) {
            Throwable cause = re.getCause();
            throw cause == null ? re : cause;
        }
    }

    private MethodHandle getMethodHandleJava9(Method method)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup) PRIVATE_LOOKUP_IN_METHOD.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
                declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                declaringClass);
    }

    private MethodHandle getMethodHandleJava8(Method method)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return LOOKUP_CONSTRUCTOR.newInstance(declaringClass, ALLOWED_MODES).unreflectSpecial(method, declaringClass);
    }

    private static class PlainMethodInvoker implements DaoMethodInvoker {
        private final DaoMethodInvoke mapperMethod;

        public PlainMethodInvoker(DaoMethodInvoke mapperMethod) {
            super();
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, String dataSource) throws Throwable {
            return mapperMethod.execute(proxy, dataSource, method, args);
        }
    }

    private static class DefaultMethodInvoker implements DaoMethodInvoker {
        private final MethodHandle methodHandle;

        public DefaultMethodInvoker(MethodHandle methodHandle) {
            super();
            this.methodHandle = methodHandle;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, String dataSource) throws Throwable {
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
        }
    }
}
