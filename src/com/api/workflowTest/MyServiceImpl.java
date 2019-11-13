package com.api.workflowTest;

import com.engine.core.impl.Service;

import java.util.Map;

public class MyServiceImpl extends Service implements MyService {
    @Override
    public Map<String, Object> getTestData(Map<String, Object> params) {
        DemoTest demoTest = new DemoTest(user, params);
        return commandExecutor.execute(demoTest);
    }
}
