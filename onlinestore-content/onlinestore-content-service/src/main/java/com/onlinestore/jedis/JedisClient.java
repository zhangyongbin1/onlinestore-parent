package com.onlinestore.jedis;

/**
 * 操作redis服务器实例方法的接口，方便其他子类实现
 */
public interface JedisClient {

	String set(String key, String value);
	String get(String key);
	Long del(String key);
	Boolean exists(String key);
	Long expire(String key, int seconds);
	Long ttl(String key);
	Long incr(String key);
	Long hset(String key, String field, String value);
	String hget(String key, String field);
	Long hdel(String key, String... field);
}
