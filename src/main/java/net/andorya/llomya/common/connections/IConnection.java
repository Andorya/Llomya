package net.andorya.llomya.common.connections;

import net.andorya.llomya.common.channels.IDataCompressor;
import net.andorya.llomya.common.channels.IMessagingChannel;

public interface IConnection<T extends IMessagingChannel<?>, V> {
    /**
     *
     */
    void connect() throws Exception;

    /**
     *
     */
    void disconnect() throws Exception;

    /**
     *
     */
    T createChannel(String name, IDataCompressor compressor, boolean canConsumeMessages, int compressionThreshold) throws Exception;

    /**
     *
     */
    V getConnection();
}
