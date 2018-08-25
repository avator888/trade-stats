package foo.pac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class ApplicationMain {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationMain.class, args);
    }

    
    @EventListener(ApplicationReadyEvent.class)
    public void processStartUpOperations() {
        System.out.println("start up operations");
    }

}
