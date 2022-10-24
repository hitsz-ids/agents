import edu.cn.hitsz_ids.agents.client.io.Agents;
import edu.cn.hitsz_ids.agents.client.io.InputStream;
import edu.cn.hitsz_ids.agents.client.io.OutputStream;
import edu.cn.hitsz_ids.agents.core.BridgeType;
import edu.cn.hitsz_ids.agents.grpc.AgentsFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestClientException {
    @Test
    public void testUnit() {
        String uri;
        String testMsg = "写入一些测试数据";
        try (OutputStream outputStream = Agents.create(BridgeType.DISK, "test.txt")) {
            outputStream.write(testMsg.getBytes(StandardCharsets.UTF_8));
            uri = outputStream.getUri();
            System.out.printf("存储文件的路径为：%s\n", uri);
            String result = readUri(uri);
            Assert.assertEquals(result, testMsg);
            boolean success = Agents.delete(uri);
            Assert.assertTrue(success);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readUri(String uri) {
        try (InputStream inputStream = Agents.open(uri)) {
            byte[] source = new byte[1024];
            int length = inputStream.read(source);
            byte[] bytes = new byte[length];
            System.arraycopy(source, 0, bytes, 0, length);
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void listFiles() {
        List<AgentsFile> list = Agents.listFiles("");
        System.out.println(list);
    }
}
