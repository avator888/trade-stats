package foo.pac.db.mongo.iex;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IexCompaniesRepository extends MongoRepository<IexCompanies, String> {

    public IexCompanies findFirstByDate(String date);

}