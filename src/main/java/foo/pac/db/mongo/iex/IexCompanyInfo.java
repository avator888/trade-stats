package foo.pac.db.mongo.iex;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class IexCompanyInfo {

    @Id
    public String id;

    @Indexed(unique = true)
    private String symbol;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%s, symbol='%s', content='%s']", id, symbol, content);
    }

}
