package net.andorya.llomya.common.channels.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.andorya.llomya.common.channels.IDataCompressor;
import net.andorya.llomya.common.channels.IMessagingChannel;
import net.andorya.llomya.common.connections.IConnection;
import net.andorya.llomya.common.messages.IMessageHandler;
import net.andorya.llomya.common.messages.Message;
import net.andorya.llomya.common.messages.MessageRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractMessagingChannel<T extends IConnection<? extends IMessagingChannel<?>, ?>> implements IMessagingChannel<T> {
    protected final Logger logger;
    protected final String name;
    protected final IDataCompressor compressor;
    protected final boolean canConsumeMessages;
    protected final int compressionThreshold;
    protected final Map<Class<? extends Message>, List<IMessageHandler<? extends Message>>> messageHandlers = new HashMap<>();

    protected AbstractMessagingChannel(String name, IDataCompressor compressor, boolean canConsumeMessages, int compressionThreshold) {
        this.logger = Logger.getLogger("MessagingChannel-" + name);

        this.name = name;
        this.compressor = compressor;
        this.canConsumeMessages = canConsumeMessages;
        this.compressionThreshold = compressionThreshold;
    }

    public abstract void sendData(byte[] data) throws Exception;

    @Override @SuppressWarnings("UnstableApiUsage")
    public void sendMessage(Message message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        try {
            output.write(message.getClass().getName().hashCode());
            message.write(output);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while writing a message: " + message.getClass().getSimpleName(), e);
        }

        byte[] data = output.toByteArray();
        if (compressionThreshold != -1 && data.length > compressionThreshold && compressor != null) {
            sendData(compressor.compress(data));
            return;
        }

        ByteArrayDataOutput finalOutput = ByteStreams.newDataOutput();
        finalOutput.write(0);
        finalOutput.write(data);

        sendData(finalOutput.toByteArray());
    }

    @Override
    public <V extends Message> void registerHandler(Class<V> messageClazz, IMessageHandler<V> handler) {
        messageHandlers.computeIfAbsent(messageClazz, c -> new ArrayList<>()).add(handler);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void handleMessages(byte[] data) {
        ByteArrayDataInput input = ByteStreams.newDataInput(compressor != null ? compressor.decompress(data) : data);
        boolean read = true;

        while (read) {
            try {
                Message message = MessageRegistry.createMessage(input);
                if (message == null) {
                    // TODO: ?
                    return;
                }

                handleMessage(message);
            } catch (Exception e) {
                if (e instanceof IllegalStateException && e.getMessage().contains("EOFException"))
                    read = false;
                else
                    logger.log(Level.WARNING, "An error occurred while reading messages", e);
            }
        }
    }

    @SuppressWarnings("All")
    private <V extends Message> void handleMessage(V message) {
        Class<? extends Message> clazz = message.getClass();
        if (!messageHandlers.containsKey(clazz))
            return;

        List<IMessageHandler<? extends Message>> handlers = messageHandlers.get(clazz);
        handlers.forEach(handler -> ((IMessageHandler<V>) handler).handle(message));
    }
}
