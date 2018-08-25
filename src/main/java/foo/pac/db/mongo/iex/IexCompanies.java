package foo.pac.db.mongo.iex;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class IexCompanies {

    @Id
    public String id;

    @Indexed(unique = true)
    private String date;

    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return String.format("Customer[id=%s, date='%s', content='%s']", id, date, content);
    }

}
