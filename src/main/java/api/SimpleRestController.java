package api;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SimpleRestController {

    @Value("${api.address}")
    private String apiAddress;

    @RequestMapping("/")
    public String returnOK() {
        return "";
    }

    @RequestMapping("/search")
    public String search() {
        return "[\"contracts\", \"methods\"]";
    }

    @RequestMapping(value = "/query", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public String query(HttpEntity<String> httpEntity) {
        String json = httpEntity.getBody();
        if (json == null)
            return null;

        JSONObject object = new JSONObject(json);
        String fromDateString = object.getJSONObject("range").getString("from").split("T")[0];
        String toDateString = object.getJSONObject("range").getString("to").split("T")[0];

        String target = ((JSONObject)object.getJSONArray("targets").get(0)).getString("target");

        return getDataForDateRange(fromDateString, toDateString, target.equals("methods"));
    }

    private  String getDataForDateRange(String fromDateString, String toDateString, boolean isMethod) {
        LocalDate start = LocalDate.parse(fromDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(toDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<DateTransactionInfo> data = new LinkedList<>();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            String result;
            if (isMethod)
                result = getMethodsForDate(date.toString());
            else
                result = getContractsForDate(date.toString());
            data.add(new DateTransactionInfo(result, date, isMethod));
        }

        Map<String, List<UserTransactionInfo>> userData = new HashMap<>();
        data.forEach(dataTxnInfo -> dataTxnInfo.getInfo().forEach(dayTxnInfo -> {
            String address = dayTxnInfo.getAddress();
            if (!userData.containsKey(address)) {
                userData.put(address, new LinkedList<>());
            }

            userData.get(address).add(new UserTransactionInfo(
                    dataTxnInfo.getDate().atStartOfDay(ZoneId.of("+3")).toEpochSecond() * 1000,
                    dayTxnInfo.getCount()
            ));
        }));

        return toJSON(userData);
    }

    private String toJSON(Map<String, List<UserTransactionInfo>> data) {
        StringJoiner joiner = new StringJoiner(",");
        data.forEach((name, infos) -> {
            StringJoiner inner = new StringJoiner(",");
            infos.forEach(info -> inner.add(
                    "[" + info.getCount() + ", " + info.getDate() + "]"
            ));
            joiner.add("{ \"target\": \"" + name + "\", \"datapoints\": [" + inner.toString() + "]}");
        });
        return "[" + joiner.toString() + "]";
    }

    private String getContractsForDate(String date) {
        return requestForDate(date, "contracts");
    }

    private String getMethodsForDate(String date) {
        return requestForDate(date, "methods");
    }

    private  String requestForDate(String date, String type) {
        String query = "http://" + apiAddress + "/" + type + "?num=5&date=" + date;
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        HttpEntity<String> response = template.exchange(
                query,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }
}
