package test;

import main.*;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GedcomReadParseTest {
    Family f = new Family();
    Individual i = new Individual();

    @Test
    public void checkName() throws Exception {
        assertEquals("Kandasamy", f.name());
    }
}