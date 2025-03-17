package tr.gate.entity_identifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tr.gate.entity_identifier.ExcelDataLoader;
import tr.gate.entity_identifier.payload.DocumentResponse;
import tr.gate.entity_identifier.payload.Entity;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaggerService {

    @Value("${jwt.token}")
    private String jwtToken;

    private final WebClient webClient;

    private final ExcelDataLoader excelDataLoader;

    private final ExcelDocumentService excelDocumentService;


    private List<String> extractNamesFromResponse(DocumentResponse response) {
        return response.getEntity().stream()
                .map(Entity::getName)
                .toList();
    }

    public DocumentResponse processRow(String text, String expectedOutput) {
        DocumentResponse response = callTaggerService(text);

        List<String> namesFromResponse = extractNamesFromResponse(response);
        List<String> namesInExpectedOutput = extractNamesFromExpectedOutput(expectedOutput);
        String difference = compareNamesWithExpectedOutputContent(namesFromResponse,
                namesInExpectedOutput);



        return DocumentResponse.builder()
                .entity(response.getEntity())
                .names(namesFromResponse)
                .difference(difference)
                .expectedOutput(expectedOutput)
                .expectedNames(namesInExpectedOutput)
                .build();
    }

    private List<String> extractNamesFromExpectedOutput(String expectedOutput) {
        if (expectedOutput.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(expectedOutput.split("\\|"))
                .map(String::trim)
                .toList();
    }

    private DocumentResponse callTaggerService(String text) {
       return webClient.post()
                .uri("https://api-uat.thomsonreuters.com/trnerr/api/ctrs/tagger/v1/tag?tags=company&resolutions=all&language=en&class=generic&tags=person")
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
        List<DocumentResponse> responses = excelData.entrySet().stream()
                .map(entry -> processRow(entry.getKey(), entry.getValue()))
                .toList();

        responses.forEach(entry -> excelDocumentService.writeDataToExcelFile(entry));

        log.info("processExcelData() - response" + responses);
        return responses;
    }
}
