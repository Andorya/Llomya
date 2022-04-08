package net.andorya.llomya.redis;

import net.andorya.llomya.common.channels.IDataCompressor;
import net.andorya.llomya.common.connections.IConnection;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisConnection implements IConnection<RedisMessagingChannel, JedisPool> {
    private final RedisCredentials credentials;
    private JedisPool pool;

    public RedisConnection(RedisCredentials credentials) {
        this.credentials = credentials;
    }

    public static RedisConnection create(RedisCredentials credentials) {
        return new RedisConnection(credentials);
    }

    @Override
    public void connect() {
        pool = credentials.password().isEmpty() ?
                new JedisPool(new JedisPoolConfig(), credentials.hostname(), credentials.port(), Protocol.DEFAULT_TIMEOUT) : new JedisPool(new JedisPoolConfig(), credentials.hostname(), credentials.port(), Protocol.DEFAULT_TIMEOUT, credentials.password());
    }

    @Override
    public void disconnect() {
        pool.close();
    }

    @Override
    public RedisMessagingChannel createChannel(String name, IDataCompressor compressor, boolean canConsumeMessages, int compressionThreshold) {
        RedisMessagingChannel channel = new RedisMessagingChannel(name, compressor, canConsumeMessages, compressionThreshold);
        channel.connect(this);
        return channel;
    }

    @Override
    public JedisPool getConnection() {
        return pool;
    }
}
