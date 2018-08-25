package foo.pac.db.mongo.iex;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface IexCompanyStockpricedailyRawRepository extends MongoRepository<IexCompanyStockpricedailyRaw, String> {

    @Query("{ 'symbol' : ?0, 'date' : ?1 }")
    List<IexCompanyStockpricedailyRaw> IexCompanyStockpricedailyRaw(String symbol, String date);

}
