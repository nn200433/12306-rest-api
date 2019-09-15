package com.sinosun.train.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 * <p>
 * Created on 2019-03-11 09:35:31
 *
 * @author 猎隼丶止戈
 */
public class RedisUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static final String LOCK_PREFIX = "lock:";
    private static final Integer LOCK_EXPIRE = 300;

    private static RedisTemplate<String, Object> redisTemplate;

    private static StringRedisTemplate stringRedisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redis, StringRedisTemplate stringRedis) {
        redisTemplate = redis;
        stringRedisTemplate = stringRedis;
    }

    /**
     * 根据key设置缓存有效时间
     *
     * @param key
     * @param time
     * @return boolean
     * @author Archer
     * @date 2019/3/1 16:07
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取过期时间
     *
     * @param key
     * @return long
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断是否存在
     *
     * @param key
     * @return boolean
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {

            return false;
        }
    }

    /**
     * 根据key删除redis数据
     *
     * @param key
     * @return void
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return boolean
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return
     */
    public static boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return
     */
    public static boolean setString(final String key, String value, Long expireTime, TimeUnit timeUnit) {
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取键值
     *
     * @param key
     * @return java.lang.Object
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }


    /**
     * 设置字符串键值
     *
     * @param key
     * @return java.lang.Object
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static boolean setString(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取字符串类型键值
     *
     * @param key
     * @return java.lang.Object
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static String getString(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取数值型键值
     *
     * @param key 键
     * @return java.lang.Object
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static Integer getInt(String key) {
        try {
            Object v = get(key);
            if (v == null || "".equals(v)) {
                return null;
            }

            return (Integer) v;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param key 键值
     * @return boolean
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static boolean incr(String key) {
        try {
            redisTemplate.opsForValue().increment(key);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param key
     * @return boolean
     * @author Archer
     * @date 2019/3/1 16:19
     */
    public static int incr(String key, int initValue) throws Exception {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(initValue));
        } else {
            int intValue = Integer.parseInt(value);
            if (intValue < initValue) {
                stringRedisTemplate.opsForValue().set(key, String.valueOf(initValue));
            } else {
                stringRedisTemplate.opsForValue().increment(key);
            }
        }
        return Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key)));
    }

    /**
     * 获取分布式锁
     *
     * @param key    键
     * @param expire 过期时间（ms）
     * @return
     */
    public static boolean tryGetDistributedLock(String key, int expire) {
        return (Boolean) redisTemplate.execute((RedisCallback) connection -> {

            long expireAt = System.currentTimeMillis() + expire + 1;
            Boolean acquire = connection.setNX(key.getBytes(), String.valueOf(expireAt).getBytes());

            if (acquire) {
                return true;
            } else {

                byte[] value = connection.get(key.getBytes());

                if (Objects.nonNull(value) && value.length > 0) {

                    long expireTime = Long.parseLong(new String(value));

                    if (expireTime < System.currentTimeMillis()) {
                        // 如果锁已经过期
                        byte[] oldValue = connection.getSet(key.getBytes(), String.valueOf(System.currentTimeMillis() + expire + 1).getBytes());
                        // 防止死锁
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
    }

    /**
     * 释放分布式锁
     *
     * @param key 键
     * @return
     */
    public static boolean releaseDistributedLock(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 分布式锁（会给key加默认前缀 lock:）
     *
     * @param key key值
     * @return 是否获取到
     */
    public static boolean lock(String key) {
        String lock = LOCK_PREFIX + key;
        // 利用lambda表达式
        return (Boolean) redisTemplate.execute((RedisCallback) connection -> {

            long expireAt = System.currentTimeMillis() + LOCK_EXPIRE + 1;
            Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());


            if (acquire) {
                return true;
            } else {

                byte[] value = connection.get(lock.getBytes());

                if (Objects.nonNull(value) && value.length > 0) {

                    long expireTime = Long.parseLong(new String(value));

                    if (expireTime < System.currentTimeMillis()) {
                        // 如果锁已经过期
                        byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + LOCK_EXPIRE + 1).getBytes());
                        // 防止死锁
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
    }

    /**
     * 删除分布式锁
     *
     * @param key
     * @return
     */
    public static boolean deleteLock(String key) {
        String lock = LOCK_PREFIX + key;
        return redisTemplate.delete(lock);
    }

    /**
     * 设置有序集合
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public static boolean zsetAdd(String key, String value, double score) {
        ZSetOperations operations = redisTemplate.opsForZSet();
        boolean result = operations.add(key, value, score);
        return result;
    }

    /**
     * 设置有序集合
     *
     * @param key
     * @return
     */
    public static Long zsetSize(String key) {
        ZSetOperations operations = redisTemplate.opsForZSet();
        Long result = operations.size(key);
        return result;
    }

    /**
     * 设置有序集合，增加分数
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public static Double zsetIncrement(String key, String value, Double score) {
        ZSetOperations operations = redisTemplate.opsForZSet();
        score = operations.incrementScore(key, value, score);
        return score;
    }

    /**
     * 获取有序集合分数
     *
     * @param key
     * @param value
     * @return
     */
    public static Double zsetGetScore(String key, String value) {

        ZSetOperations operations = redisTemplate.opsForZSet();

        Double score = operations.score(key, value);
        return score;
    }


    /**
     * 获取有序集合 前 num 位
     *
     * @param key   集合名
     * @param isAsc 是否升序
     * @param page  第几页 从1开始
     * @param num   每页个数
     * @return
     */
    public static Set<String> zsetRange(String key, boolean isAsc, int page, int num) {
        Set<String> rangeSet;
        ZSetOperations operations = redisTemplate.opsForZSet();
        if (isAsc) {
            rangeSet = operations.rangeByScore(key, 0, Double.MAX_VALUE, (page - 1) * num, num);
        } else {
            rangeSet = operations.reverseRangeByScore(key, 0, Double.MAX_VALUE, (page - 1) * num, num);
        }
        return rangeSet;
    }


    /**
     * 获取当前key在集合中的排行
     *
     * @param key
     * @param value
     * @param isAsc
     * @return
     */
    public static Long zsetRank(String key, String value, boolean isAsc) {
        Long rank;
        ZSetOperations operations = redisTemplate.opsForZSet();
        if (isAsc) {
            rank = operations.rank(key, value);
        } else {
            rank = operations.reverseRank(key, value);
        }
        return rank;
    }


    /**
     * 删除集合内元素
     *
     * @param key
     * @param value
     * @return
     */
    public static Long zsetRemove(String key, String value) {
        ZSetOperations operations = redisTemplate.opsForZSet();
        Long removeNum = operations.remove(key, value);
        return removeNum;
    }

    /**
     * 获取集合内元素
     *
     * @param key
     * @return
     */
    public static Map<String, Double> zsetValues(String key) {
        ZSetOperations operations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> set = operations.reverseRangeWithScores(key, 0, operations.size(key));
        Map<String, Double> map = new LinkedHashMap<>();
        Iterator<ZSetOperations.TypedTuple<String>> iterator = set.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<String> valueInfo = iterator.next();
            map.put(valueInfo.getValue(), valueInfo.getScore());
        }
        return map;
    }
}