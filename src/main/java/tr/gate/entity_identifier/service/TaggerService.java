package tr.gate.entity_identifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tr.gate.entity_identifier.Accuracy;
import tr.gate.entity_identifier.data.ExcelDataLoader;
import tr.gate.entity_identifier.payload.DocumentResponse;
import tr.gate.entity_identifier.payload.Entity;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaggerService {

    public static final String SEPARATOR_REGEX = "\\|";

    @Value("${jwt.token}")
    private String jwtToken;

    private static final String PERSON_ENTITY_TYPE = "Person";

    private final WebClient webClient;

    private final ExcelDataLoader excelDataLoader;

    private final ExcelDocumentService excelDocumentService;
    private final ONLPPersonEntityService onlpPersonEntityService;


    public DocumentResponse processRow(String text, String expectedOutput, boolean isLastRow) {

        List<String> namesFromOpenNLP = onlpPersonEntityService.identifyPersonNames(text);
        List<String> namesInExpectedOutput = extractNamesFromExpectedOutput(expectedOutput);
        String difference = compareNamesWithExpectedOutputContent(namesFromOpenNLP,
                namesInExpectedOutput);

        // TODO: Calculate Match count and Precision %
        Accuracy accuracy = calculateAccuracy(namesFromOpenNLP, namesInExpectedOutput);

        excelDocumentService.writeDataToExcelFile(text, expectedOutput, namesFromOpenNLP,
                namesInExpectedOutput, difference, accuracy, isLastRow);

        return DocumentResponse.builder()
               // .entity(response.getEntity())
                .names(namesFromOpenNLP)
                .difference(difference)
                .expectedOutput(expectedOutput)
                .expectedNames(namesInExpectedOutput)
                .build();
    }


    private Accuracy calculateAccuracy(List<String> namesFromResponse, List<String> namesInExpectedOutput) {

        int matchCount = 0;
        for (String name : namesFromResponse) {
            if (namesInExpectedOutput.contains(name)) {
                matchCount++;
            }
        }

        double precision = (double) matchCount / namesInExpectedOutput.size() * 100;

        System.out.println("Match Count: " + matchCount);
        System.out.printf("Precision: %.2f%%\n", precision);

        return Accuracy.builder()
                .matchCount(matchCount)
                .precision(precision)
                .build();
    }


    private List<String> extractNamesFromResponse(DocumentResponse response) {
        return response.getEntity().stream()
                .filter(extractedName -> extractedName.getType().equals(PERSON_ENTITY_TYPE))
                .map(Entity::getName)
                .toList();
    }


    private List<String> extractNamesFromExpectedOutput(String expectedOutput) {
        if (expectedOutput.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(expectedOutput.split(SEPARATOR_REGEX))
                .map(String::trim)
                .toList();
    }

    private DocumentResponse callTaggerService(String text) {
        return webClient.post()
                .uri("https://api-uat.thomsonreuters.com/trnerr/api/ctrs/tagger/v1/tag?resolutions=all&language=en&class=generic&tags=person")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + jwtToken
                )
                .header(HttpHeaders.ACCEPT, "application/json")
                .bodyValue(text)
                .retrieve()
                .bodyToMono(DocumentResponse.class)
                .block();
    }

    private String compareNamesWithExpectedOutputContent(List<String> namesFromResponse,
                                                         List<String> namesInExpectedOutput) {
        Set<String> extractedNames = new HashSet<>(namesFromResponse);
        Set<String> expectedOutputNames = new HashSet<>(namesInExpectedOutput);

        if (extractedNames.equals(expectedOutputNames)) {
            return "No difference";
        } else {
            Set<String> onlyInExtractedNames = new HashSet<>(extractedNames);
            onlyInExtractedNames.removeAll(expectedOutputNames);

            Set<String> onlyInExpectedOutput = new HashSet<>(expectedOutputNames);
            onlyInExpectedOutput.removeAll(extractedNames);

            return "Elements in extracted names, but not in expected output text: " + onlyInExtractedNames +
                    " Elements only in expected output text, but not in extracted names " + onlyInExpectedOutput;
        }
    }


    public List<DocumentResponse> processExcelData() {
        Map<String, String> excelData = excelDataLoader.getExcelData();
        int totalRows = excelData.size();
        AtomicInteger currentRow = new AtomicInteger(0);

        List<DocumentResponse> responses = excelData.entrySet().stream()
                .map(entry -> {
                    boolean isLastRow = currentRow.incrementAndGet() == totalRows;
                    return processRow(entry.getKey(), entry.getValue(), isLastRow);
                })
                .toList();

        log.info("processExcelData() - response" + responses);
        return responses;
    }
}
