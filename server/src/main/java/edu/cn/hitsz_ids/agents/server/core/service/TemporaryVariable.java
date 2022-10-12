package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;

class TemporaryVariable {
    private volatile static TemporaryVariable instance;

    private TemporaryVariable() {
    }

    public static TemporaryVariable getInstance() {
        if (instance == null) {
            synchronized (TemporaryVariable.class) {
                if (instance == null) {
                    instance = new TemporaryVariable();
                }
            }
        }
        return instance;
    }

    private final ThreadLocal<Bridge<?>> bridges = new ThreadLocal<>();
    private final ThreadLocal<String> identities = new ThreadLocal<>();
    private final ThreadLocal<String> schemes = new ThreadLocal<>();
    public void addIdentity(String bridge) {
        identities.set(bridge);
    }

    public String getIdentity() {
        return identities.get();
    }
    public void addScheme(String bridge) {
        schemes.set(bridge);
    }

    public String getScheme() {
        return schemes.get();
    }
    public void addBridge(Bridge<?> bridge) {
        bridges.set(bridge);
    }

    public Bridge<?> getBridge() {
        return bridges.get();
    }
}
