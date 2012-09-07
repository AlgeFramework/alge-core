package poc;

import junit.framework.TestCase;

/**
 * @author psrinivasan
 *         Date: 9/6/12
 *         Time: 2:22 PM
 */
public class SessionIdReaderTest extends TestCase {

    private SessionIdReader sessionIdReader;

    public void setUp() throws Exception {
        //todo: need better way to specify resources
        sessionIdReader = new SessionIdReader("src/test/java/poc/dummy_session_ids.txt");

    }

    public void tearDown() throws Exception {

    }

    public void testGetOneSessionId() throws Exception {
        String sessionId = sessionIdReader.getOneSessionId();
        assertEquals("00DD00000018pnK!AQIAQO9ha7K5QSeDPOfbLl6MMTo17n5H0gby0lt6kSCiE2m7U_kt_VXCzsa5k.8dw4TWlLLJ01rtjvXI9U7r2QMAS2ntMo62", sessionId);
    }
}
