package foo.pac.db.mongo.iex;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface IexCompanyEarningsRepository extends MongoRepository<IexCompanyEarnings, String> {

    @Query("{ 'symbol' : ?0, 'date' : ?1 }")
    List<IexCompanyEarnings> findIexCompanyEarnings(String symbol, String date);

}
