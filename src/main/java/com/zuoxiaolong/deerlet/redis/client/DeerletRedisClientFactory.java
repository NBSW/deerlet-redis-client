/**
 * 
 */
package com.zuoxiaolong.deerlet.redis.client;

import java.util.ArrayList;
import java.util.List;

import com.zuoxiaolong.deerlet.redis.client.config.Configuration;
import com.zuoxiaolong.deerlet.redis.client.config.Server;
import com.zuoxiaolong.deerlet.redis.client.connection.ConnectionPool;
import com.zuoxiaolong.deerlet.redis.client.connection.impl.ConnectionPoolImpl;
import com.zuoxiaolong.deerlet.redis.client.strategy.ConsistencyHashStrategy;
import com.zuoxiaolong.deerlet.redis.client.strategy.SimpleNodeStrategy;

/**
 * @author zuoxiaolong
 *
 */
public enum DeerletRedisClientFactory {
	
	INSTANCE;
	
	private static final int INIT_SIZE = 10;

	private static final int MAX_SIZE = 100;

	private static final int MIN_IDLE_SIZE = 10;

	private static final int MAX_IDLE_SIZE = 20;
	
	private static final String INIT_SIZE_PROPERTY = "initSize";

	private static final String MAX_SIZE_PROPERTY = "maxSize";

	private static final String MIN_IDLE_SIZE_PROPERTY = "minIdleSize";

	private static final String MAX_IDLE_SIZE_PROPERTY = "maxIdleSize";
	
	public DeerletRedisClient createDeerletRedisClient() {
		return createDeerletRedisClient(new Configuration(null));
	}
	
	public DeerletRedisClient createDeerletRedisClient(String configFile) {
		return createDeerletRedisClient(new Configuration(configFile));
	}

	public DeerletRedisClient createDeerletRedisClient(Configuration configuration) {
		List<ConnectionPool> connectionPools = createConnectionPools(configuration);
		if (connectionPools.size() == 1) {
			return new SimpleNodeDeerletRedisClient(new SimpleNodeStrategy<ConnectionPool>(connectionPools));
		} else {
			return new ClusterDeerletRedisClient(new ConsistencyHashStrategy<ConnectionPool>(connectionPools));
		}
	}
	
	private final List<ConnectionPool> createConnectionPools(Configuration configuration) {
		List<ConnectionPool> connectionPools = new ArrayList<ConnectionPool>();
		
		List<Server> servers = configuration.getServerList();
		Integer initSize = configuration.getInteger(INIT_SIZE_PROPERTY, INIT_SIZE);
		Integer maxSize = configuration.getInteger(MAX_SIZE_PROPERTY, MAX_SIZE);
		Integer minIdleSize = configuration.getInteger(MIN_IDLE_SIZE_PROPERTY, MIN_IDLE_SIZE);
		Integer maxIdleSize = configuration.getInteger(MAX_IDLE_SIZE_PROPERTY, MAX_IDLE_SIZE);
		for (int i = 0; i < servers.size(); i++) {
			connectionPools.add(new ConnectionPoolImpl(servers.get(i), initSize, maxSize, minIdleSize, maxIdleSize));
		}
		return connectionPools;
	}
	
}
