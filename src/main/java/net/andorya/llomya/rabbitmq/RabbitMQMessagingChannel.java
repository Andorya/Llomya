package net.andorya.llomya.rabbitmq;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.Channel;
import net.andorya.llomya.common.channels.IDataCompressor;
import net.andorya.llomya.common.channels.impl.AbstractMessagingChannel;

public class RabbitMQMessagingChannel extends AbstractMessagingChannel<RabbitMQConnection> {
    private Channel channel;

    protected RabbitMQMessagingChannel(String name, IDataCompressor compressor, boolean canConsumeMessages, int compressionThreshold) {
        super(name, compressor, canConsumeMessages, compressionThreshold);
    }

    @Override
    public void connect(RabbitMQConnection connection) throws Exception {
        Preconditions.checkNotNull(connection.getConnection(), "The RabbitMQ connection is not established.");
        Preconditions.checkState(channel == null, name + " channel is already connected.");
        channel = connection.getConnection().createChannel();
        channel.exchangeDeclare(name, "fanout");

        if (canConsumeMessages) {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, name, "");
            channel.basicConsume(queueName, true, (tag, delivery) -> handleMessages(delivery.getBody()), tag -> {});
        }
    }

    @Override
    public void disconnect() throws Exception {
        Preconditions.checkNotNull(channel, name + " channel is not connected.");
        channel.close();

        channel = null;
    }

    @Override
    public void sendData(byte[] data) throws Exception {
        channel.basicPublish(name, "", null, data);
    }
}
