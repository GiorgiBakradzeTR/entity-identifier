package tr.gate.entity_identifier.service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SNLPPersonEntityService implements EntityService {

    private final StanfordCoreNLP stanfordCoreNLP;

    public SNLPPersonEntityService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        this.stanfordCoreNLP = new StanfordCoreNLP(props);
    }


    @Override
    public List<String> identifyPersonNames(String text) {
        log.info("Inside SNLP Entity Service");
        Set<String> personNames = new HashSet<>();
        CoreDocument document = new CoreDocument(text);
        stanfordCoreNLP.annotate(document);

        StringBuilder currentName = new StringBuilder();
        for (CoreMap sentence : document.annotation().get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if ("PERSON".equalsIgnoreCase(ner)) {
                    if (currentName.length() > 0) {
                        currentName.append(" ");
                    }
                    currentName.append(token.word());
                } else {
                    if (currentName.length() > 0) {
                        personNames.add(currentName.toString());
                        currentName.setLength(0); // Reset the StringBuilder
                    }
                }
            }
            if (currentName.length() > 0) {
                personNames.add(currentName.toString());
            }
        }
        return new ArrayList<>(personNames);
    }
}
