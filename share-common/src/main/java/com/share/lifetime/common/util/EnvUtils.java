package com.share.lifetime.common.util;

import com.share.lifetime.common.constant.EnvEnum;

public final class EnvUtils {

    public static EnvEnum transformEnv(String envName) {
        if (StringUtils.isBlank(envName)) {
            return EnvEnum.UNKNOWN;
        }
        switch (envName.trim().toUpperCase()) {
            case "LPT":
                return EnvEnum.LPT;
            case "FAT":
            case "FWS":
                return EnvEnum.FAT;
            case "UAT":
                return EnvEnum.UAT;
            case "PRO":
            case "PROD": // just in case
                return EnvEnum.PRO;
            case "DEV":
                return EnvEnum.DEV;
            case "LOCAL":
                return EnvEnum.LOCAL;
            case "TOOLS":
                return EnvEnum.TOOLS;
            default:
                return EnvEnum.UNKNOWN;
        }
    }

}
