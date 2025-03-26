package tr.gate.entity_identifier.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tr.gate.entity_identifier.payload.DocumentResponse;
import tr.gate.entity_identifier.service.TaggerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Value("${jwt.token}")
    private String jwtToken;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaggerService taggerService;

    private List<DocumentResponse> mockJsonResponse;

    @BeforeEach
    void setUp() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        String json = new String(
                Files.readAllBytes(Paths.get("src/test/resources/response.json"))
        );
        mockJsonResponse = objectMapper.readValue(json, new TypeReference<List<DocumentResponse>>() {
        });
    }

    @Test
    void testGetDocuments() throws Exception {
        when(taggerService.processExcelData()).thenReturn(mockJsonResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(mockJsonResponse);

        mockMvc.perform(
                get("http://localhost:8080/api/document"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
