package foo.pac.db.mongo.iex;

import org.springframework.data.annotation.Id;

public class IexCompanyStats {

    @Id
    public String id;

    private String symbol;

    private String date;

    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%s, symbol='%s', date='%s', content='%s']", id, symbol, date, content);
    }

}
