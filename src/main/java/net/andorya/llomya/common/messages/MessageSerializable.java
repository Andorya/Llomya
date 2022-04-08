package net.andorya.llomya.common.messages;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public interface MessageSerializable {
    void write(ByteArrayDataOutput out);

    void read(ByteArrayDataInput in);
}
