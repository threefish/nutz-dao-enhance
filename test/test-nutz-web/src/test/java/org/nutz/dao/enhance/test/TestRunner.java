package org.nutz.dao.enhance.test;

import org.junit.runners.model.InitializationError;
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
