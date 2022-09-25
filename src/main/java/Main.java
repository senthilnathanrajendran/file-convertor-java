import convertor.FileConvertor;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException, XMLStreamException {
        FileConvertor fileConvertor = new FileConvertor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println(" Please input the source file location !");
            String path = reader.readLine();
            if (path != null) {
                fileConvertor.generateXMLAndCSV(path);
            }
            System.out.println(" Do you want continue? If so please enter 'Y' or else click any other key !");
            String enteredString = reader.readLine();
            if (!("Y".equalsIgnoreCase(enteredString))) {
                break;
            }
        }
    }
}