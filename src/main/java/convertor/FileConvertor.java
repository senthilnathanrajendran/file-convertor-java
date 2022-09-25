package convertor;


import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import model.Sentence;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class FileConvertor {

    private final String REGEX_PATTERN_TO_SPLIT_TEXT_FILE = "(?<!Mr|Ms|Mrs|Dr|Jr)[.?!]";
    private final String REGEX_PATTERN_TO_SPLIT_SENTENCE = "[\\-_*#%&()|\\[\\]{}:+, @\\t\\\"\\r\\n]+";
    private final Pattern PATTERN_TO_SPLIT_TEXT_FILE = Pattern.compile(REGEX_PATTERN_TO_SPLIT_TEXT_FILE);
    private final Pattern PATTERN_TO_SPLIT_SENTENCE = Pattern.compile(REGEX_PATTERN_TO_SPLIT_SENTENCE);

    public void generateXMLAndCSV(String path) throws XMLStreamException, IOException {
        File file = new File(path);
        String parentPath = file.getParent();
        String fileName = file.getName().split("\\.")[0];
        Pair<XMLEventFactory, XMLEventWriter> pair = createXML(parentPath, fileName);
        XMLEventFactory xef = pair.getLeft();
        XMLEventWriter xew = pair.getRight();

        CSVWriter csvWriter = createCSV(parentPath, fileName, path);

        int sentenceCount = 1;
        Scanner sc = new Scanner(new BufferedReader(new FileReader(path))).useDelimiter(PATTERN_TO_SPLIT_TEXT_FILE);
        while (sc.hasNext()) {
            String line = sc.next().trim();
            if (StringUtils.isNotBlank(line)) {
                List<String> words = Arrays.stream(PATTERN_TO_SPLIT_SENTENCE.split(line)).sorted(String.CASE_INSENSITIVE_ORDER).toList();
                Sentence sentence = new Sentence();
                sentence.setWords(words);
                generateCSV(csvWriter, sentenceCount, sentence);
                generateXML(xef, xew, sentence);
                sentenceCount++;
            }
        }
        closeXML(xef, xew);
        closeCSV(csvWriter);
        System.out.println("Generated CSV and XML files are stored in " + parentPath + " location");
    }

    private CSVWriter createCSV(String parentPath, String fileName, String path) throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(parentPath + "\\" + fileName + ".csv"), ICSVWriter.DEFAULT_SEPARATOR, ICSVWriter.NO_QUOTE_CHARACTER, ICSVWriter.NO_ESCAPE_CHARACTER, ICSVWriter.DEFAULT_LINE_END);
        int maxLength = 0;
        Scanner sc = new Scanner(new BufferedReader(new FileReader(path))).useDelimiter(PATTERN_TO_SPLIT_TEXT_FILE);
        while (sc.hasNext()) {
            String line = sc.next().trim();
            if (StringUtils.isNotBlank(line)) {
                List<String> words = Arrays.stream(PATTERN_TO_SPLIT_SENTENCE.split(line)).toList();
                if (maxLength < words.size()) {
                    maxLength = words.size();
                }
            }
        }
        List<String> csvHeaders = new ArrayList<>();
        csvHeaders.add(0, " ");
        csvHeaders.addAll(IntStream.range(0, maxLength).mapToObj(i -> "Word" + (i + 1)).toList());
        csvWriter.writeNext(csvHeaders.toArray(String[]::new));
        return csvWriter;
    }

    private void generateCSV(CSVWriter csvWriter, int sentenceCount, Sentence sentence) {
        List<String> words = new ArrayList<>();
        words.add(0, "Sentence " + sentenceCount);
        words.addAll(sentence.getWords());
        csvWriter.writeNext(words.toArray(String[]::new));
    }

    private void closeCSV(CSVWriter csvWriter) throws IOException {
        csvWriter.close();
    }

    private Pair<XMLEventFactory, XMLEventWriter> createXML(String parentPath, String fileName) throws IOException, XMLStreamException {
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLEventFactory xef = XMLEventFactory.newInstance();
        XMLEventWriter xew = xof.createXMLEventWriter(new FileWriter(parentPath + "\\" + fileName + ".xml"));
        StartDocument xeo = xef.createStartDocument("UTF-8", "1.0", true);
        xew.add(xeo);
        StartElement textStartElement = xef.createStartElement("", "", "text");
        xew.add(textStartElement);
        return Pair.of(xef, xew);
    }

    private void generateXML(XMLEventFactory xef, XMLEventWriter xew, Sentence sentence) throws XMLStreamException {
        StartElement sentenceStartElement = xef.createStartElement("", "", "sentence");
        xew.add(sentenceStartElement);
        for (String word : sentence.getWords()) {
            StartElement wordStartElement = xef.createStartElement("", "", "word");
            xew.add(wordStartElement);
            Characters content = xef.createCharacters(word);
            xew.add(content);
            EndElement wordEndElement = xef.createEndElement("", "", "word");
            xew.add(wordEndElement);
        }
        EndElement sentenceEndElement = xef.createEndElement("", "", "sentence");
        xew.add(sentenceEndElement);
    }

    private void closeXML(XMLEventFactory xef, XMLEventWriter xew) throws XMLStreamException {
        EndElement textEndElement = xef.createEndElement("", "", "text");
        xew.add(textEndElement);
        EndDocument ed = xef.createEndDocument();
        xew.add(ed);
        xew.close();
    }
}
