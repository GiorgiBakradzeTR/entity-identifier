package tr.gate.entity_identifier.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {
    List<Entity> entity;
    List<String> names;
    String difference;
    String expectedOutput;
    List<String> expectedNames;
}
