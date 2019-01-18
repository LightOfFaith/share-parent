package com.share.lifetime.common.util;

import java.io.File;
import java.io.FileNotFoundException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtils {

    /**
     * 
     * @param resourceLocation
     *            support "classpath:";"file:";"D:"(资源文件全路径)
     * @return
     * @throws FileNotFoundException
     */
    public static File getFile(String resourceLocation) throws FileNotFoundException {
        return org.springframework.util.ResourceUtils.getFile(resourceLocation);
    }

    public static String getPath(String resourceLocation) throws FileNotFoundException {
        File file = getFile(resourceLocation);
        return file != null ? file.getPath() : null;
    }

}
