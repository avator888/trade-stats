package foo.pac.db.mongo.iex;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IexCompanyInfoRepository extends MongoRepository<IexCompanyInfo, String> {

    public IexCompanyInfo findFirstBySymbol(String symbol);

}
