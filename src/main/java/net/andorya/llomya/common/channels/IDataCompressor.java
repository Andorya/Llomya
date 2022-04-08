package net.andorya.llomya.common.channels;

public interface IDataCompressor {
    /**
     *
     */
    byte[] compress(byte[] data);

    /**
     *
     */
    byte[] decompress(byte[] data);
}
