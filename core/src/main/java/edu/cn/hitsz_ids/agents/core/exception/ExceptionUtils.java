package edu.cn.hitsz_ids.agents.core.exception;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ExceptionUtils {
    public static StackTraceElement[] stackTrace(byte[] bytes) {
        StackTraceElement[] obj;
        if (bytes == null) {
            obj = new StackTraceElement[0];
        } else {
            try (var bis = new ByteArrayInputStream(bytes)) {
                try (var ois = new ObjectInputStream(bis)) {
                    obj = (StackTraceElement[]) ois.readObject();
                }
            } catch (Exception ex) {
                obj = new StackTraceElement[0];
            }
        }
        return obj;
    }

    public static byte[] toBytes(Throwable throwable) {
        byte[] bytes;
        try (var bos = new ByteArrayOutputStream()) {
            try (var oos = new ObjectOutputStream(bos)) {
                oos.writeObject(throwable.getStackTrace());
                oos.flush();
                bytes = bos.toByteArray();
            }
        } catch (Exception ex) {
            bytes = new byte[0];
        }
        return bytes;
    }
}
