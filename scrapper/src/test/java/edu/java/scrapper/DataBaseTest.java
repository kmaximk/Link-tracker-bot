package edu.java.scrapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataBaseTest extends IntegrationTest{

    @Test
    public void connectionTest() {
        assertTrue(POSTGRES.isCreated());
        assertTrue(POSTGRES.isRunning());
    }
}
