package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author guardWarm
 * @date 2021-01-15 22:28
 * 保存token的本地缓存，利用guava实现的
 */
public class TokenCache {

	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

	public static final String TOKEN_PREFIX = "token_";
	public static final String DEFAULT_VALUE = "null";


	//默认淘汰策略为：LRU算法
	private static LoadingCache<String, String> localCache
			= CacheBuilder.newBuilder()
			.initialCapacity(1000)
			.maximumSize(10000)
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.build(new CacheLoader<String, String>() {
				//默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
				@Override
				public String load(String s) throws Exception {
					return DEFAULT_VALUE;
				}
			});

	public static void setKey(String key, String value) {
		localCache.put(key, value);
	}

	public static String getKey(String key) {
		String value = null;
		try {
			value = localCache.get(key);
			if (DEFAULT_VALUE.equals(value)) {
				return null;
			}
			return value;
		} catch (Exception e) {
			logger.error("localCache get error", e);
		}
		return null;
	}
}
