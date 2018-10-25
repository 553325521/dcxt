package cn.wifiedu.ssm.util.redis.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.wifiedu.ssm.util.redis.JedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
public class JedisClientSingle implements JedisClient {
	
	private static Logger logger = Logger.getLogger(JedisClientSingle.class);

	@Autowired
	private JedisPool jedisPool; 
	
	@Override
	public String get(String key) {
		String string = null;
		try {
			Jedis jedis = jedisPool.getResource();
			string = jedis.get(key);
			jedis.close();
		} catch (Exception e) {
			logger.error("error", e);
		}
		return string;
	}
	
	@Override
	public boolean isExit(String key) {
		Jedis jedis = jedisPool.getResource();
        return jedis.exists(key);
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String string = jedis.set(key, value);
		jedis.close();
		return string;
	}

	@Override
	public String hget(String hkey, String key) {
		Jedis jedis = jedisPool.getResource();
		String string = jedis.hget(hkey, key);
		jedis.close();
		return string;
	}

	@Override
	public long hset(String hkey, String key, String value) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hset(hkey, key, value);
		jedis.close();
		return result;
	}

	@Override
	public long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.incr(key);
		jedis.close();
		return result;
	}

	@Override
	public long expire(String key, int second) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.expire(key, second);
		jedis.close();
		return result;
	}

	@Override
	public long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.ttl(key);
		jedis.close();
		return result;
	}

	@Override
	public long del(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.del(key);
		jedis.close();
		return result;
	}

	@Override
	public long hdel(String hkey, String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hdel(hkey, key);
		jedis.close();
		return result;
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = jedisPool.getResource();
		Map<String, String> map = jedis.hgetAll(key);
		jedis.close();
		return map;
	}

}
