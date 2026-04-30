package com.fitfind.fitfind;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({TestContainerConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FitFindApplicationTests {

    @Test
    void contextLoads() {
    }

}
