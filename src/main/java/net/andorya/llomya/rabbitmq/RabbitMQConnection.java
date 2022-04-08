package net.andorya.llomya.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.andorya.llomya.common.channels.IDataCompressor;
import net.andorya.llomya.common.connections.IConnection;

public class RabbitMQConnection implements IConnection<RabbitMQMessagingChannel, Connection> {
    private final RabbitMQCredentials credentials;
    private Connection connection;

    public RabbitMQConnection(RabbitMQCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public void connect() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(credentials.hostname());
        factory.setUsername(credentials.username());
        factory.setPassword(credentials.password());
        factory.setPort(credentials.port());

        connection = factory.newConnection();
    }

    @Override
    public void disconnect() throws Exception {
        connection.close();
    }

    @Override
    public RabbitMQMessagingChannel createChannel(String name, IDataCompressor compressor, boolean canConsumeMessages, int compressionThreshold) throws Exception {
        RabbitMQMessagingChannel channel = new RabbitMQMessagingChannel(name, compressor, canConsumeMessages, compressionThreshold);
        channel.connect(this);
        return channel;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public static RabbitMQConnection create(RabbitMQCredentials credentials) {
        return new RabbitMQConnection(credentials);
    }
}
