package foo.pac;
import foo.pac.stock.endpoints.IexEndpoints;
import foo.pac.endpoints.PingEndpoint;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/ts")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(PingEndpoint.class);
        register(IexEndpoints.class);
        register(LoggingFilter.class);
    }
}
