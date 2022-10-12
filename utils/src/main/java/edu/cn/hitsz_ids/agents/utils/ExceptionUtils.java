package edu.cn.hitsz_ids.agents.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class ExceptionUtils {
    public static Throwable toThrowable(byte[] bytes, byte[] errorMsg) {
        Object obj;
        if (bytes == null) {
            obj = new StackTraceElement[0];
        } else {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                    obj = ois.readObject();
                }
            } catch (Exception ex) {
                obj = new StackTraceElement[0];
            }
        }
        Throwable exception;
        if (errorMsg == null) {
            exception = new Throwable("NULL");
        } else {
            exception = new Throwable(new String(errorMsg, StandardCharsets.UTF_8));
        }
        exception.setStackTrace((StackTraceElement[]) obj);
        return exception;
    }

    public static byte[] toBytes(Throwable throwable) {
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
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
