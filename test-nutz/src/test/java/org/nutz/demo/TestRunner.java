package org.nutz.demo;

import org.junit.runners.model.InitializationError;
import org.nutz.demo.test.MainModule;
import org.nutz.mock.NutTestRunner;

public class TestRunner extends NutTestRunner {

    public TestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Class<?> getMainModule() {
        return MainModule.class;
    }
}
