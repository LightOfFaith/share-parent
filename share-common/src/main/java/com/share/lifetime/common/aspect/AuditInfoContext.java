package com.share.lifetime.common.aspect;

import java.util.Map;

import com.google.common.collect.Maps;

public class AuditInfoContext {

    private static ThreadLocal<Map<String, String>> auditInfoHolder = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return Maps.newLinkedHashMap();
        }
    };

    public AuditInfoContext() {}

    public static void reset() {
        auditInfoHolder.remove();
    }

    public static void put(String key, String value) {
        auditInfoHolder.get().put(key, value);
    }

    public static String get(String key) {
        return auditInfoHolder.get().get(key);
    }

    public static Map<String, String> currentAuditInfo() {
        return auditInfoHolder.get();
    }
}
