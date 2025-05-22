package config;

import com.mytech.machinemonitorsystem.config.CorsProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
/*
 * thess annotation code commented, as it will trigger springboot to load dataSrouce, which is not available in springboot but in JBoss from now on.
 * */
/*@SpringBootTest(properties = {
        "cors.allowed-origins=http://localhost:4200,http://localhost:8180"
    },
    classes={CorsProperties.class}
)
@EnableConfigurationProperties(CorsProperties.class)*/
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "cors.allowed-origins=http://localhost:4200,http://localhost:8180"
})
@EnableConfigurationProperties(CorsProperties.class)
@ConfigurationPropertiesScan


public class CorsPropertiesTest {
    @Autowired
    private CorsProperties corsProperties;

    @Test
    void shouldLoadPropertiesSuccessfully(){
        Assertions.assertTrue(corsProperties.getAllowedOrigins().containsAll(List.of("http://localhost:4200","http://localhost:8180")));
    }

}
