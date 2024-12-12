// package tn.esprit.spring;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest
// class EventsProjectApplicationTests {

// 	@Test
// 	void contextLoads() {
// 	}

// }
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.services.EventService;

@SpringBootTest
class EventsProjectApplicationTests {

    @Autowired
    private EventService eventService;

    @Test
    void contextLoads() {
        assertThat(eventService).isNotNull();
    }
}

