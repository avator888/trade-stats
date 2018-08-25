package foo.pac.domains;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CompaniesSingleton {

    private CompaniesSingleton() {
    }

    public static CompaniesSingleton getInstance() {
        return CompaniesSingletonHolder.INSTANCE;
    }

    private static class CompaniesSingletonHolder {

        private static final CompaniesSingleton INSTANCE = new CompaniesSingleton();
    }

    private HashMap<String, Company> companies = null;

    public HashMap<String, Company> getCompanies() {
        return companies;
    }

    public void setCompanies(HashMap<String, Company> companies) {
        this.companies = companies;
    }

    public void setCompanies(StringBuilder companies) {
        Gson gson = new Gson();
        Company[] c = gson.fromJson(companies.toString(), Company[].class);
        List<Company> cs = Arrays.asList(c);
        for (int i = 0; i < cs.size(); i++) {
            setCompany(cs.get(i).getSymbol(), cs.get(i));
        }
    }

    public void setCompanies(String companies) {
        Gson gson = new Gson();
        Company[] c = gson.fromJson(companies, Company[].class);
        List<Company> cs = Arrays.asList(c);
        for (int i = 0; i < cs.size(); i++) {
            setCompany(cs.get(i).getSymbol(), cs.get(i));
        }
    }

    public void setCompany(String companySymbol, Company company) {
        if (companies == null) {
            companies = new HashMap<>();
        }
        companies.put(companySymbol, company);
    }

    public Company getCompany(String companySymbol) {
        return companies.get(companySymbol);
    }

    private String date = "";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
