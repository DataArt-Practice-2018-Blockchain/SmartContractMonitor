package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DateTransactionInfo {
    private final LocalDate date;

    private final List<DayTransactionInfo> info;

    public LocalDate getDate() {
        return date;
    }

    public List<DayTransactionInfo> getInfo() {
        return info;
    }


    public DateTransactionInfo(String json, LocalDate date, boolean isMethod) {
        this.date = date;
        JSONArray array = new JSONObject(json).getJSONArray("result");
        this.info = new LinkedList<>();
        if (!isMethod) {
            array.forEach((object) ->
                    this.info.add(new DayTransactionInfo(
                            pretty(((JSONObject) object).getString("address")),
                            ((JSONObject) object).getInt("count")
                    ))
            );
        } else {
            array.forEach((object) ->
                    this.info.add(new DayTransactionInfo(
                            ((JSONObject) object).getString("method")
                             + "@"
                             + pretty(((JSONObject) object).getString("address")),
                            ((JSONObject) object).getInt("count")
                    ))
            );
        }
    }

    public static class DayTransactionInfo {
        public DayTransactionInfo(String address, int count) {
            this.address = address;
            this.count = count;
        }

        private final String address;

        public String getAddress() {
            return address;
        }

        public int getCount() {
            return count;
        }

        private final int count;
    }


    private static final String vow = "euioa";
    private static final String cons = "rtypsdfghklcvbnm";

    private String pretty(String seed) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random(seed.hashCode());
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                builder.append(cons.charAt(Math.abs(random.nextInt()) % cons.length()));
            } else {
                builder.append(vow.charAt(Math.abs(random.nextInt()) % vow.length()));
            }
        }
        return builder.toString();
    }

}
