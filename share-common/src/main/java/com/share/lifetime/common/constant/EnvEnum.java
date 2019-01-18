package com.share.lifetime.common.constant;

import com.google.common.base.Preconditions;
import com.share.lifetime.common.util.EnvUtils;

/**
 * 
 * <ul>
 * <li>LOCAL: Local Development environment, assume you are working at the beach with no network access</li>
 * <li>DEV: Development environment</li>
 * <li>FWS: Feature Web Service Test environment</li>
 * <li>FAT: Feature Acceptance Test environment</li>
 * <li>UAT: User Acceptance Test environment</li>
 * <li>LPT: Load and Performance Test environment</li>
 * <li>PRO: Production environment</li>
 * <li>TOOLS: Tooling environment, a special area in production environment which allows access to test environment</li>
 * </ul>
 *
 * @author liaoxiang
 * @date 2019/01/18
 */
public enum EnvEnum {

    LOCAL, DEV, FWS, FAT, UAT, LPT, PRO, TOOLS, UNKNOWN;

    public static EnvEnum fromString(String env) {
        EnvEnum environment = EnvUtils.transformEnv(env);
        Preconditions.checkArgument(environment != UNKNOWN, String.format("Env %s is invalid", env));
        return environment;
    }
}
