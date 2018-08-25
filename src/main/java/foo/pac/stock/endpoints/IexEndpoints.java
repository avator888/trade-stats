package foo.pac.stock.endpoints;

import foo.pac.db.mongo.iex.IexCompanies;
import foo.pac.domains.CompaniesSingleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import foo.pac.db.mongo.iex.IexCompaniesRepository;
import foo.pac.db.mongo.iex.IexCompanyEarnings;
import foo.pac.db.mongo.iex.IexCompanyEarningsRepository;
import foo.pac.db.mongo.iex.IexCompanyInfo;
import foo.pac.db.mongo.iex.IexCompanyInfoRepository;
import foo.pac.db.mongo.iex.IexCompanyStats;
import foo.pac.db.mongo.iex.IexCompanyStatsRepository;
import foo.pac.db.mongo.iex.IexCompanyStockpricedailyRaw;
import foo.pac.db.mongo.iex.IexCompanyStockpricedailyRawRepository;
import java.util.List;

/**
 *
 * get price for a specific stock
 *
 * https://api.iextrading.com/1.0/ref-data/symbols
 * https://api.iextrading.com/1.0/stock/sbux/company
 * https://api.iextrading.com/1.0/stock/sbux/stats
 * https://api.iextrading.com/1.0/stock/sbux/earnings
 * https://api.iextrading.com/1.0/stock/sbux/chart/5y
 *
 */
@Path("/")
public class IexEndpoints {

    @Autowired
    private IexCompaniesRepository mongoCompanies;

    @Autowired
    private IexCompanyInfoRepository mongoCompanyInfo;

    @Autowired
    private IexCompanyStatsRepository mongoCompanyStats;

    @Autowired
    private IexCompanyEarningsRepository mongoCompanyEarnings;

    @Autowired
    private IexCompanyStockpricedailyRawRepository mongoCompanyStock;

    // -------------------------------------------------------------------------
    // get list of all avaliable companies
    // https://api.iextrading.com/1.0/ref-data/symbols
    // -------------------------------------------------------------------------    
    @GET
    @Path("/companies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompaniesList() {

        // vars
        CompaniesSingleton cs = CompaniesSingleton.getInstance();
        String currentDate = getCurrentLocalDate();

        // ---------------------------------------------------------------------
        // handle case when data exists in memory for current date
        // ---------------------------------------------------------------------
        if (cs.getCompanies() != null && cs.getDate().equals(currentDate)) {
            return Response.ok(cs.getCompanies(), MediaType.APPLICATION_JSON).build();
        }

        // ---------------------------------------------------------------------
        // handle case when data is coming from db
        // ---------------------------------------------------------------------
        try {
            IexCompanies c = mongoCompanies.findFirstByDate(currentDate);
            if (c != null && c.getDate().equals(currentDate)) {
                cs.setDate(currentDate);
                cs.setCompanies(c.getContent());
                return Response.ok(cs.getCompanies(), MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            System.out.println("problem with getting data from mongo");
        }

        // ---------------------------------------------------------------------
        // handle case when no companies records in memory or in mongo
        // ---------------------------------------------------------------------
        // STEP#01: get data from iex system
        StringBuilder content = new StringBuilder("");
        try {
            URL url = new URL("https://api.iextrading.com/1.0/ref-data/symbols");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        } catch (IOException ex) {
            System.out.println("problem with request");
        }

        // STEP#02: save result into singleton
        cs.setDate(currentDate);
        cs.setCompanies(content);

        // STEP#03: save data in db       
        IexCompanies comp = new IexCompanies();
        comp.setDate(currentDate);
        comp.setContent(content.toString());
        mongoCompanies.save(comp);

        // STEP#04: reply
        Response response = Response.ok(cs.getCompanies(), MediaType.APPLICATION_JSON).build();
        return response;
    }

    // -------------------------------------------------------------------------
    // get basic info about company
    // https://api.iextrading.com/1.0/stock/sbux/company
    // -------------------------------------------------------------------------    
    @GET
    @Path("/company/{company-symbol}/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyInfo(@PathParam("company-symbol") String symbol) {

        //vars
        symbol = symbol.toUpperCase();

        // try to get data from mongo
        try {
            IexCompanyInfo ci = mongoCompanyInfo.findFirstBySymbol(symbol);
            if (ci != null && ci.getSymbol().equals(symbol)) {
                return Response.ok(ci, MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            System.out.println("problem with getting data from mongo");
        }

        // request to iex service
        StringBuilder content = new StringBuilder("");
        try {
            URL url = new URL("https://api.iextrading.com/1.0/stock/" + symbol + "/company");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        } catch (IOException ex) {
            System.out.println("problem with request");
        }

        // save data in mongo
        IexCompanyInfo comp = new IexCompanyInfo();
        comp.setSymbol(symbol);
        comp.setContent(content.toString());
        mongoCompanyInfo.save(comp);

        // reply
        Response response = Response.ok(comp, MediaType.APPLICATION_JSON).build();
        return response;
    }

    // -------------------------------------------------------------------------
    // get basic statistics
    // https://api.iextrading.com/1.0/stock/sbux/stats
    // -------------------------------------------------------------------------    
    @GET
    @Path("/company/{company-symbol}/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyStats(@PathParam("company-symbol") String symbol) {

        //vars
        String currentDate = this.getCurrentLocalDate();
        symbol = symbol.toUpperCase();

        // check if same data existed in mongo
        try {
            List<IexCompanyStats> cs = mongoCompanyStats.findIexCompanyStats(symbol, currentDate);

            if (cs != null && cs.get(0).getSymbol().equals(symbol) && cs.get(0).getDate().equals(currentDate)) {
                return Response.ok(cs.get(0), MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            System.out.println("problem with getting data from mongo");
        }

        // get data from iex
        StringBuilder content = new StringBuilder("");
        try {
            URL url = new URL("https://api.iextrading.com/1.0/stock/" + symbol + "/stats");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        } catch (IOException ex) {
            System.out.println("problem with request");
        }

        // save in mongo
        IexCompanyStats cs = new IexCompanyStats();
        cs.setSymbol(symbol);
        cs.setDate(currentDate);
        cs.setContent(content.toString());
        mongoCompanyStats.save(cs);

        // reply
        Response response = Response.ok(cs, MediaType.APPLICATION_JSON).build();
        return response;
    }

    // -------------------------------------------------------------------------
    // get info about earnings
    // https://api.iextrading.com/1.0/stock/sbux/earnings
    // -------------------------------------------------------------------------    
    @GET
    @Path("/company/{company-symbol}/earnings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyEarnings(@PathParam("company-symbol") String symbol) {

        //vars
        String currentDate = this.getCurrentLocalDate();
        symbol = symbol.toUpperCase();

        // check if same data existed in mongo
        try {
            List<IexCompanyEarnings> ce = mongoCompanyEarnings.findIexCompanyEarnings(symbol, currentDate);

            if (ce != null && ce.get(0).getSymbol().equals(symbol) && ce.get(0).getDate().equals(currentDate)) {
                return Response.ok(ce.get(0), MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            System.out.println("problem with getting data from mongo");
        }

        // get data from iex
        StringBuilder content = new StringBuilder("");
        try {
            URL url = new URL("https://api.iextrading.com/1.0/stock/" + symbol + "/earnings");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        } catch (IOException ex) {
            System.out.println("problem with request");
        }

        // save in mongo
        IexCompanyEarnings ce = new IexCompanyEarnings();
        ce.setSymbol(symbol);
        ce.setDate(currentDate);
        ce.setContent(content.toString());
        mongoCompanyEarnings.save(ce);

        // reply
        Response response = Response.ok(ce, MediaType.APPLICATION_JSON).build();
        return response;
    }

    // -------------------------------------------------------------------------
    // get stock price for extended amount of time
    // https://api.iextrading.com/1.0/stock/sbux/chart/5y
    // -------------------------------------------------------------------------    
    @GET
    @Path("/company/{company-symbol}/stock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyDailyStockPrice(@PathParam("company-symbol") String symbol) {

        //vars
        String currentDate = this.getCurrentLocalDate();
        symbol = symbol.toUpperCase();

        // check if same data existed in mongo
        try {
            List<IexCompanyStockpricedailyRaw> ce = mongoCompanyStock.IexCompanyStockpricedailyRaw(symbol, currentDate);

            if (ce != null && ce.get(0).getSymbol().equals(symbol) && ce.get(0).getDate().equals(currentDate)) {
                return Response.ok(ce.get(0), MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            System.out.println("problem with getting data from mongo");
        }

        // get data from iex
        StringBuilder content = new StringBuilder("");
        try {
            URL url = new URL("https://api.iextrading.com/1.0/stock/" + symbol + "/chart/5y");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        } catch (IOException ex) {
            System.out.println("problem with request");
        }

        // save in mongo
        IexCompanyStockpricedailyRaw ce = new IexCompanyStockpricedailyRaw();
        ce.setSymbol(symbol);
        ce.setDate(currentDate);
        ce.setContent(content.toString());
        mongoCompanyStock.save(ce);

        // reply
        Response response = Response.ok(ce, MediaType.APPLICATION_JSON).build();
        return response;

    }

    public String getCurrentLocalDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
