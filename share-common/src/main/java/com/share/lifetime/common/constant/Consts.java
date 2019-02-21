package com.share.lifetime.common.constant;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.apache.http.client.HttpClient;

public class Consts {

    // 本地环境
    public static final String TEST = "test";
    // 测试环境
    public static final String DEV = "dev";
    // 生产环境
    public static final String PRO = "pro";

    // 文件路径分隔符
    public static final String FILE_PATH_SEPARATOR = File.separator;
    public static final char FILE_PATH_SEPARATOR_CHAR = File.separatorChar;
    public static final char WINDOWS_FILE_PATH_SEPARATOR_CHAR = '\\';
    public static final char LINUX_FILE_PATH_SEPARATOR_CHAR = '/';

    // ClassPath分隔符
    public static final String CLASS_PATH_SEPARATOR = File.pathSeparator;
    public static final char CLASS_PATH_SEPARATOR_CHAR = File.pathSeparatorChar;

    // 换行符
    public static final String LINE_SEPARATOR = System.lineSeparator();

    // 临时目录
    public static final String TMP_DIR = SystemUtils.JAVA_IO_TMPDIR;
    // 应用的工作目录
    public static final String WORKING_DIR = SystemUtils.USER_DIR;
    // 用户 HOME目录
    public static final String USER_HOME = SystemUtils.USER_HOME;
    // Java HOME目录
    public static final String JAVA_HOME = SystemUtils.JAVA_HOME;

    // Java版本
    public static final String JAVA_SPECIFICATION_VERSION = SystemUtils.JAVA_SPECIFICATION_VERSION; // e.g. 1.8
    public static final String JAVA_VERSION = SystemUtils.JAVA_VERSION; // e.g. 1.8.0_102
    public static final boolean IS_JAVA7 = SystemUtils.IS_JAVA_1_7;
    public static final boolean IS_JAVA8 = SystemUtils.IS_JAVA_1_8;
    public static final boolean IS_ATLEASET_JAVA7 = IS_JAVA7 || IS_JAVA8;
    public static final boolean IS_ATLEASET_JAVA8 = IS_JAVA8;

    // 操作系统类型及版本
    public static final String OS_NAME = SystemUtils.OS_NAME;
    public static final String OS_VERSION = SystemUtils.OS_VERSION;
    public static final String OS_ARCH = SystemUtils.OS_ARCH; // e.g. x86_64
    public static final boolean IS_LINUX = SystemUtils.IS_OS_LINUX;
    public static final boolean IS_UNIX = SystemUtils.IS_OS_UNIX;
    public static final boolean IS_WINDOWS = SystemUtils.IS_OS_WINDOWS;

    /** UTF-8字符集 **/
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String CLASSPATH_PREFIX = "classpath";

    public static final String URL_PROTOCOL_FILE = "file";

    public static final String SDK_VERSION = "SDK/1.0.0";

    public static final String USER_AGENT =
        SDK_VERSION + " (" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " "
            + System.getProperty("os.version") + ") Java/" + System.getProperty("java.version") + " HttpClient/"
            + HttpClient.class.getPackage().getImplementationVersion();

    /**
     * HTTP(S) 读数据超时时间，单位毫秒
     */
    public static final int HTTP_READ_TIMEOUT_MS = 8 * 1000;

    /**
     * HTTP(S) 连接超时时间，单位毫秒
     */
    public static final int HTTP_CONNECT_TIMEOUT_MS = 6 * 1000;

    public static final String FAIL = "FAIL";

    public static final String SUCCESS = "SUCCESS";

    /**
     * 换行符
     */
    public static final String LF = "\n";
    /**
     * 串联符
     */
    public static final String SPE1 = ",";
    /**
     * 示意符
     */
    public static final String SPE2 = ":";
    /**
     * 连接符
     */
    public static final String SPE3 = "&";
    /**
     * 赋值符
     */
    public static final String SPE4 = "=";
    /**
     * 问号符
     */
    public static final String SPE5 = "?";

    /**
     * 参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中
     */
    public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-Ca-";

    public static final String HTTP_HOST = "HTTP_HOST";

}
