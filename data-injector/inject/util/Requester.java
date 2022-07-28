package inject.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Requester {
    public String loadJson (String url) {

        StringBuilder json = new StringBuilder();

        try {

            URL urlObject = new URL(url);

            URLConnection uc = urlObject.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"utf-8"));

            String inputLine = null;

            while ( (inputLine = in.readLine()) != null) {

                json.append(inputLine);

            }

            in.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return json.toString();

    }
}
