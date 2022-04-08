package net.andorya.llomya.common.messages;

import com.google.common.io.ByteArrayDataInput;

import java.util.HashMap;
import java.util.Map;

public class MessageRegistry {
    private static final Map<Integer, Class<? extends Message>> ID_TO_MESSAGE = new HashMap<>();
    private static final Map<Class<? extends Message>, Integer> MESSAGE_TO_ID = new HashMap<>();

    public static int getMessageID(Class<? extends Message> clazz) {
        return MESSAGE_TO_ID.get(clazz);
    }

    public static Class<? extends Message> getMessageClass(int id) {
        return ID_TO_MESSAGE.get(id);
    }

    @SuppressWarnings("All")
    public static <T extends Message> T createMessage(ByteArrayDataInput input) throws Exception {
        int id = input.readInt();
        if (!ID_TO_MESSAGE.containsKey(id)) return null;

        Class<T> clazz = (Class<T>) getMessageClass(id);
        T message = clazz.getConstructor().newInstance();
        message.read(input);

        return message;
    }

    public static void registerMessage(Class<? extends Message> clazz) {
        int id = clazz.getName().hashCode();
        if (ID_TO_MESSAGE.containsKey(id))
            throw new RuntimeException("There is already a Message with ID: " + id + ", " + clazz.getName());

        ID_TO_MESSAGE.put(id, clazz);
        MESSAGE_TO_ID.put(clazz, id);
    }
}
