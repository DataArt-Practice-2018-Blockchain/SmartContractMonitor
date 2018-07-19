import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class RPCBenchmark {
    private static final String queryTemplate =
            "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\"," +
            "\"params\":[\"0x%s\", true],\"id\":1}";

    public static void main(String[] args) {
        RestTemplate template = new RestTemplate();
        String url = "http://35.228.59.11:8545";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        long startTime = System.currentTimeMillis();
        int range = 1000;
        for (int i = 1; i < range; i++) {
            String requestJson = String.format(queryTemplate, Integer.toHexString(239000 + i));
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            String result = template.postForObject(url, entity, String.class);

            System.out.println(result);
        }
        int time = (int)(System.currentTimeMillis() - startTime);

        System.out.println("" + time + "ms, " + range + " queries");
        System.out.print("est " + (double)time / range * 2662244 / 1000 / 60 / 60 + "hr");
    }
}
