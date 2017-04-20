package se.kth.projectarbor.project_arbor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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
    public void loadPage() throws Exception {
        StringBuilder url = new StringBuilder();
        url.append(startURL);
        url.append("/api/category/" + CAT + "/version/" + VERSION + "/geotype/point/lon/" + LONG + "/lat/"+ LAT + "/data.json");
        System.out.println(url.toString()+"\n\n");
        InputStream inputStream = new URL(url.toString()).openStream();
        String result;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            result = stringBuilder.toString();
        } finally {
            inputStream.close();
        }

        System.out.println(result);
        JSONObject jsonObject = new JSONObject(result);
        System.out.println(jsonObject.toString());

//        //JSONObject JOA = jsonObject.getJSONObject("referenceTime");
//        System.out.println(jsonObject.getString("referenceTime"));
//
//        JSONArray timeSeries = jsonObject.getJSONArray("timeSeries");
//        System.out.println(timeSeries.length());
//        JSONObject JO;
//        JSONArray JA;
////        for (int i=0; i<timeSeries.length(); i++) {
////            JO = timeSeries.getJSONObject(i);
//            JA = JO.getJSONArray("paramters");
//            System.out.println(JA.getInt(JA.length()-1));
//        }


    }
}