package com.sisllc.instaiml;

import java.util.Arrays;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mock")
public class MyReactiveApplicationTests {
    @Autowired(required = false)
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        assertNotNull(context);
        Arrays.stream(context.getBeanDefinitionNames())
              .sorted()
              .forEach(System.out::println);
    }
}
