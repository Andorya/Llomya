package net.andorya.llomya.common.messages;

public interface IMessageHandler<T extends Message> {
    void handle(T message);
}
