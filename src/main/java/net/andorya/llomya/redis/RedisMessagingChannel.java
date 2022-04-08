package net.andorya.llomya.redis;

import com.google.common.base.Preconditions;
import net.andorya.llomya.common.channels.IDataCompressor;
import net.andorya.llomya.common.channels.impl.AbstractMessagingChannel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Base64;

public class RedisMessagingChannel extends AbstractMessagingChannel<RedisConnection> {
    private JedisPool pool;
    private JedisPubSub listener;
    private Thread thread;

    protected RedisMessagingChannel(String name, IDataCompressor dataCompressor, boolean canConsumeMessages, int compressionThreshold) {
        super(name, dataCompressor, canConsumeMessages, compressionThreshold);
    }

    @Override
    public void connect(RedisConnection connection) {
        Preconditions.checkState(pool == null, name + " channel is already connected.");

        pool = connection.getConnection();
        Preconditions.checkNotNull(pool, "The Redis connection is not established.");

        if (canConsumeMessages) {
            listener = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    if (!channel.equals(name))
                        return;

                    byte[] data = Base64.getDecoder().decode(message);
                    handleMessages(data);
                }
            };

            thread = new Thread(() -> pool.getResource().subscribe(listener, name));
            thread.setName("MessagingChannel-" + name);
            thread.start();
        }
    }

    @Override
    public void disconnect() {
        Preconditions.checkNotNull(pool, name + " channel is not connected.");
        pool.close();
        listener.unsubscribe();
        thread.stop();

        pool = null;
        listener = null;
        thread = null;
    }

    @Override
    public void sendData(byte[] data) {
        try (Jedis connection = pool.getResource()) {
            connection.publish(name, Base64.getEncoder().encodeToString(data));
        }
    }
}
