import edu.cn.hitsz_ids.agents.client.io.Agents;
import edu.cn.hitsz_ids.agents.client.io.InputStream;
import edu.cn.hitsz_ids.agents.client.io.OutputStream;
import edu.cn.hitsz_ids.agents.core.BridgeType;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestClientException {
    @Test
    public void createFile() {
        String uri = null;
        try (OutputStream outputStream = Agents.create(BridgeType.DISK, "test.txt")) {
            outputStream.write("写入一些测试数据".getBytes(StandardCharsets.UTF_8));
            uri = outputStream.getUri();
            System.out.printf("存储文件的路径为：%s\n", uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(InputStream inputStream = Agents.open(uri)) {
            byte[] bytes = new byte[1024];
            int length = inputStream.read(bytes);
            byte[] result = new byte[length];
            System.arraycopy(bytes, 0, result, 0, length);
            System.out.printf("读取文件的数据为：%s", new String(result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readFile() {
        try(InputStream inputStream = Agents.open("DISK://5d042c62-1a70-4877-8c79-d4e9f982bd01")) {
            byte[] bytes = new byte[1024];
            int length = inputStream.read(bytes);
            byte[] result = new byte[length];
            System.arraycopy(bytes, 0, result, 0, length);
            System.out.printf("读取文件的数据为：%s", new String(result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
