package ru.greatbit.whoru.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session extends BaseSession {
    private final Map<String, Object> metainfo = new ConcurrentHashMap<>();

    public Map<String, Object> getMetainfo() {
        return metainfo;
    }
}
