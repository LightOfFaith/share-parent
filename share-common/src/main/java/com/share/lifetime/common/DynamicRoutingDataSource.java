package com.share.lifetime.common;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		// 使用DynamicDataSourceHolder保证线程安全，并且得到当前线程中的数据源key
		return DynamicDataSourceHolder.getDataSourceKey();
	}

}
