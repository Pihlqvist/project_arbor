package se.kth.projectarbor.project_arbor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private String CAT = "pmp2g"; // "mesan1g";
    private int VERSION = 2;
    private String startURL = "http://opendata-download-metfcst.smhi.se";

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

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            System.out.println("JSON ERROR: " + e.toString());
        }

        JSONArray timeSeries = jsonObject.getJSONArray("timeSeries");
        JSONObject JO;
        JSONArray JA;
        for (int i=0; i<timeSeries.length(); i++) {
            JO = timeSeries.getJSONObject(i);
            JA = JO.getJSONArray("parameters");
            JSONObject object = JA.getJSONObject(JA.length()-1);
            JSONArray array = object.getJSONArray("values");
            System.out.println(array.getInt(0));
        }


    }

    @Test
    public void trink() throws ParseException {
        String jsonTime = "2017-04-20T15:00:00Z";
        DateFormat format = new SimpleDateFormat("YYY-MM-DD'T'HH:mm:ss.SSSZ");
        Date date = format.parse(jsonTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar cal = Calendar.getInstance();
        cal.set(2017, 4, 20, 15, 00, 00);

        System.out.println(calendar.toString());
    }

    @Test
    public void /* Calendar */ jsonTimeParser(/*String jsonTime*/) {
        String jsonTime = "2017-04-20T15:00:00Z";
        Calendar calendar = Calendar.getInstance();
        String[] strings = jsonTime.split("-|:|T|Z");
        for (String s : strings) { System.out.println(s); }
    }
}