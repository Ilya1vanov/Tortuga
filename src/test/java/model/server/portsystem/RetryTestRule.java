package model.server.portsystem;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RetryTestRule implements TestRule {
    @Override
    public Statement apply(final Statement base,
                           final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                    return;
                } catch (AssertionError ae) {
                    base.evaluate();
                    return;
                }
            }
        };
    }
}