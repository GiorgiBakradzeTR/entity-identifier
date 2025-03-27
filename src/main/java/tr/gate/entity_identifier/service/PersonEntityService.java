package tr.gate.entity_identifier.service;

import lombok.RequiredArgsConstructor;
import opennlp.tools.cmdline.tokenizer.SimpleTokenizerTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonEntityService implements EntityService {

    private final NameFinderME nameFinder;

    public PersonEntityService() throws IOException {
        try (InputStream modelIn = getClass().getResourceAsStream("/en-ner-person.bin")) {
            if (modelIn == null) {
                throw new IOException("Model file not found in resources");
            }
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            nameFinder = new NameFinderME(model);
        }
    }

    public List<String> identifyPersonNames(String text) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        Span[] nameSpans = nameFinder.find(tokens);

        List<String> personNames = new ArrayList<>();
        for (Span span : nameSpans) {
            StringBuilder name = new StringBuilder();
            for (int i = span.getStart(); i < span.getEnd(); i++) {
                name.append(tokens[i]).append(" ");
            }
            personNames.add(name.toString().trim());
        }
        return personNames;
    }
}
