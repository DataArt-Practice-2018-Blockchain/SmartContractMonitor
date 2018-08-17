package api;

import java.time.LocalDate;

public class UserTransactionInfo {
    private final long date;
    private final int count;

    public long getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }

    public UserTransactionInfo(long date, int count) {
        this.date = date;
        this.count = count;
    }
}

