package net.andorya.llomya.common.channels;

import net.andorya.llomya.common.connections.IConnection;
import net.andorya.llomya.common.messages.IMessageHandler;
import net.andorya.llomya.common.messages.Message;

public interface IMessagingChannel<T extends IConnection<? extends IMessagingChannel<?>, ?>> {
    /**
     *
     */
    void connect(T connection) throws Exception;

    /**
     *
     */
    void disconnect() throws Exception;

    /**
     *
     */
    void sendMessage(Message message) throws Exception;

    /**
     *
     */
    <V extends Message> void registerHandler(Class<V> message, IMessageHandler<V> messageHandler);
}
