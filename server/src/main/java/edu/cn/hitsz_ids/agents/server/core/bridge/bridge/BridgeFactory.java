package edu.cn.hitsz_ids.agents.server.core.bridge.bridge;

import edu.cn.hitsz_ids.agents.utils.BridgeType;
import edu.cn.hitsz_ids.agents.utils.IBridgeType;
import edu.cn.hitsz_ids.agents.utils.ServerException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class BridgeFactory {
    private final Map<IBridgeType, Class<?>> typed = new ConcurrentHashMap<>();
    private final Map<String, IBridgeType> bridgeTypes = new ConcurrentHashMap<>();
    private volatile static BridgeFactory instance;

    private BridgeFactory() {
    }

    public static BridgeFactory getInstance() {
        if (instance == null) {
            synchronized (BridgeFactory.class) {
                if (instance == null) {
                    instance = new BridgeFactory();
                }
            }
        }
        return instance;
    }

    public Bridge<?> create(String type) throws ServerException {
        Bridge<?> bridge;
        IBridgeType bridgeType = bridgeTypes.get(type);
        Class<?> clazz = typed.get(bridgeType);
        if (Objects.isNull(clazz)) {
            throw new ServerException("未找到对应的存储桥接器");
        }
        try {
            Constructor<?> constructor = clazz.getConstructor(IBridgeType.class);
            bridge = (Bridge<?>) constructor.newInstance(bridgeType);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new ServerException(e);
        }
        return bridge;
    }

    public <T extends Bridge<?>> void addBridge(IBridgeType bridgeType, Class<T> clazz) {
        bridgeTypes.put(bridgeType.getName(), bridgeType);
        typed.put(bridgeType, clazz);
    }
}
