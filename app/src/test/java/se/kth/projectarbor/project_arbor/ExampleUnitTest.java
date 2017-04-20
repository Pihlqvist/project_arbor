package se.kth.projectarbor.project_arbor;

import android.util.Log;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final String TAG = "TEST";
    private double LONG = 18.068581;
    private double LAT = 59.329323;
    private String CAT = "mesan1g"; //"pmp2g";
    private int VERSION = 1;
    private String startURL = "http://opendata-download-metanalys.smhi.se";


    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void loadPage() throws Exception {
        StringBuilder url = new StringBuilder();
        url.append(startURL);
        url.append("/api/category/" + CAT + "/version/" + VERSION + "/geotype/point/lon/" + LONG + "/lat/"+ LAT + "/data.json");
        System.out.println(url.toString()+"\n\n");
        InputStream inputStream = new URL(url.toString()).openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            System.out.println(stringBuilder.toString());
        } finally {
            inputStream.close();
        }
    }
}