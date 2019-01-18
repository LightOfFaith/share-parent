package com.share.lifetime.common.util;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.FastDateFormat;

import com.google.common.base.Joiner;

public class UniqueKeyGenerator {

    private static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");
    private static final AtomicInteger counter = new AtomicInteger(new SecureRandom().nextInt());
    private static final Joiner KEY_JOINER = Joiner.on("-");

    public static String generate(Object... args) {
        String hexIdString = ByteUtils.toHexString(
            toByteArray(Objects.hash(args), MachineUtils.getMachineIdentifier(), counter.incrementAndGet()));

        return KEY_JOINER.join(TIMESTAMP_FORMAT.format(new Date()), hexIdString);

    }

    /**
     * Concat machine id, counter and key to byte array Only retrieve lower 3 bytes of the id and counter and 2 bytes of
     * the keyHashCode
     */
    protected static byte[] toByteArray(int keyHashCode, int machineIdentifier, int counter) {
        byte[] bytes = new byte[8];
        bytes[0] = ByteUtils.int1(keyHashCode);
        bytes[1] = ByteUtils.int0(keyHashCode);
        bytes[2] = ByteUtils.int2(machineIdentifier);
        bytes[3] = ByteUtils.int1(machineIdentifier);
        bytes[4] = ByteUtils.int0(machineIdentifier);
        bytes[5] = ByteUtils.int2(counter);
        bytes[6] = ByteUtils.int1(counter);
        bytes[7] = ByteUtils.int0(counter);
        return bytes;
    }

}
