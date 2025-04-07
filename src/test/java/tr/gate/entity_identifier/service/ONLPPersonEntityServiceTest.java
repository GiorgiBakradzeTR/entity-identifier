package tr.gate.entity_identifier.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ONLPPersonEntityServiceTest {

    @InjectMocks
    private ONLPPersonEntityService onlpPersonEntityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIdentifyNames_withMiddleNameSingleLetterWithPeriod() {
        String input = "Plaintiff: Michael T. Welch, Wang & Wang, San Francisco. \n";
        List<String> result = onlpPersonEntityService.identifyPersonNames(input);

        assertTrue(result.contains("Michael T. Welch"));
    }

    @Test
    public void testIdentifyNames_withCompleteMiddleName() {
        String input = "Plaintiff: Eliot Lee Grossman, Law Offices of Eliot Lee Grossman, Los Angeles. \n";
        List<String> result = onlpPersonEntityService.identifyPersonNames(input);

        assertTrue(result.contains("Eliot Lee Grossman"));
    }
}