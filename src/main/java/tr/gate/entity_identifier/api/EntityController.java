package tr.gate.entity_identifier.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.gate.entity_identifier.service.EntityService;

import java.util.List;

@RestController
@RequestMapping("/api/entity")
@RequiredArgsConstructor
public class EntityController {

    private final EntityService entityService;

    @GetMapping("/names")
    public ResponseEntity<List<String>> identifyPersonNames(@RequestBody String text) {
        return new ResponseEntity<>(entityService.identifyPersonNames(text), HttpStatus.OK);
    }
}
