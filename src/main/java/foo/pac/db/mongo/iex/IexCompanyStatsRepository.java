package foo.pac.db.mongo.iex;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface IexCompanyStatsRepository extends MongoRepository<IexCompanyStats, String> {

    @Query("{ 'symbol' : ?0, 'date' : ?1 }")
    List<IexCompanyStats> findIexCompanyStats(String symbol, String date);

}
