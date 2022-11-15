package prediction.model;

import org.junit.Test;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import au.com.bytecode.opencsv.CSVReader;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.IOException;

public class SinrPredictionModelTest {
    @Test
    public void appHasAGreeting() throws ModelNotFoundException, MalformedModelException, IOException {
        // SinrPredictionModel classUnderTest = new SinrPredictionModel();
        // assertNotNull("app should have a greeting", classUnderTest.getGreeting());
        // assertNotNull("app should have a greeting", null);
        // CSVReader reader = new CSVReader(new FileReader("/home/rzuo02/work/repast/app/src/test/resources/sinr-model-testing.csv"));
    }

    @Test
    public void testInputGenerator() {
    }
}
