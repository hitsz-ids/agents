import edu.cn.hitsz_ids.agents.client.io.InputStream;
import edu.cn.hitsz_ids.agents.client.io.OutputStream;
import edu.cn.hitsz_ids.agents.utils.BridgeType;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class TestClientException {

    @Test
    public void input() throws FileNotFoundException {
        byte[] bytes = new byte[1024];
        try (InputStream inputStream = new InputStream("./cert/cert.sh")) {
            int length = inputStream.read(bytes, 0, 1024);
            System.out.println(new String(bytes, 0, length));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uri() throws URISyntaxException {
        String uri = "DISK://asdkfhasdjfhkashdfhaskdf/asfsdafsdaf";
        URI uri1 = new URI(uri);
    }

    @Test
    public void output() {
        try (OutputStream outputStream = new OutputStream(BridgeType.DISK, "", "test.txt")) {
            outputStream.write("写入测试数据".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
