package config;

import com.mytech.machinemonitorsystem.config.CorsProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(properties = {
        "cors.allowed-origins=http://localhost:4200,http://localhost:8180"
    },
    classes={CorsProperties.class}
)
@EnableConfigurationProperties(CorsProperties.class)
//@SpringBootConfiguration
public class CorsPropertiesTest {
    @Autowired
    private CorsProperties corsProperties;

    @Test
    void shouldLoadPropertiesSuccessfully(){
        Assertions.assertTrue(corsProperties.getAllowedOrigins().containsAll(List.of("http://localhost:4200","http://localhost:8180")));
    }

}
