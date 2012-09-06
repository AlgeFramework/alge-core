package poc;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;

public class SessionIdReader {
    private String file_name;

    public SessionIdReader(String file_name) {
        this.file_name = file_name;
    }

    public String getOneSessionId() throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file_name));
        String[] nextLine = reader.readNext();
        return nextLine[6];
    }

}
