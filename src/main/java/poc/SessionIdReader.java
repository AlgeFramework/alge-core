package poc;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * So, we dont know if the CSVReader class is thread safe.  It shouldn't matter for now
 * given that we have only one producer, but it could become an issue later.
 * TODO:  investigate thread safety in CSVReader.
 */
public class SessionIdReader {
    private String file_name;
    private final CSVReader reader;

    public SessionIdReader(String file_name) throws FileNotFoundException {
        this.file_name = file_name;
        reader = new CSVReader(new FileReader(file_name));
    }

    public String getOneSessionId() throws IOException {
        String[] nextLine = reader.readNext();
        if (nextLine == null) {
            return null;
        }
        return nextLine[6];
    }

}
