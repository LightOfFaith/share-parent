package com.share.lifetime.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicDataSourceHolder {

	/**
	 * 写库对应的数据源key
	 */
	private static final String MASTER = "master";

	/**
	 * 读库对应的数据源key
	 */
	private static final String SLAVE = "slave";

	/**
	 * 使用ThreadLocal记录当前线程的数据源key
	 */
	private static final ThreadLocal<String> HOLDER = new ThreadLocal<String>();

	public static void putDataSourceKey(String key) {
		HOLDER.set(key);
	}

	public static String getDataSourceKey() {
		return HOLDER.get();
	}

	public static void markDBMaster() {
		putDataSourceKey(MASTER);
	}

	public static void markDBSlave() {
		putDataSourceKey(SLAVE);
	}

	public static void markDBClear() {
		HOLDER.remove();
	}

}
