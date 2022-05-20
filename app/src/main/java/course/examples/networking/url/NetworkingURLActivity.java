package course.examples.networking.url;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class NetworkingURLActivity extends Activity {
    static final String TAG = "NetworkingURLActivity";

    private TextView mTextView;
	private ImageView mImageView;
    private final static String MTEXTVIEW_TEXT_KEY = "MTEXTVIEW_TEXT_KEY";
    List<Country> mCountries;
    private Button btn;
    Drawable mDraw = null;

    private static final String URL = "http://api.geonames.org/countryInfoJSON?username=dean";


	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mTextView = findViewById(R.id.textView);
        mImageView = findViewById(R.id.imageView);
        btn = findViewById(R.id.load_button_currency);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openExchange();
            }
        });
    }

    private void openExchange() {
        Intent intent = new Intent(getApplicationContext(), Exchange.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }


	public void onClick(View v) {
        onButtonPressed();
	}


    public void onDownloadfinished(String result, List<Country> iCountries) throws IOException {

        mCountries = iCountries;
        ListView yourListView = (ListView) findViewById(R.id.listView);

        ListAdapter customAdapter = new ListAdapter(this, R.layout.item_row, mCountries);

        yourListView .setAdapter(customAdapter);

    }

    private void onButtonPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Country> result = loadFromNetwork();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onDownloadfinished("", result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    static {
        disableSslVerification();
    }

    private static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }



    //private String loadFromNetwork() {
    private List<Country> loadFromNetwork() {
        String data = null;
        List<Country> result = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            // 1. Get connection. 2. Prepare request (URI)
            httpUrlConnection = (HttpURLConnection) new URL(URL)
                    .openConnection();

            // 3. This app does not use a request body
            // 4. Read the response
            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());

            data = readStream(in);

            // parse json string
            result = parseJsonString(data);

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
        } catch (IOException exception) {
            Log.e(TAG, "IOException");
        } finally {
            if (null != httpUrlConnection) {
                // 5. Disconnect
                httpUrlConnection.disconnect();
            }
        }

        //return String data;

        return result;
    }

    private List<Country> parseJsonString(String data) {
        List<Country> result = new ArrayList<>();

        try {
            // Get top-level JSON Object - a Map
            JSONObject responseObject = (JSONObject) new JSONTokener(data).nextValue();

            // Extract value of "earthquakes" key -- a List
            JSONArray earthquakes = responseObject.getJSONArray("geonames");

            // Iterate over earthquakes list
            for (int idx = 0; idx < earthquakes.length(); idx++) {

                // Get single earthquake mData - a Map
                JSONObject c = (JSONObject) earthquakes.get(idx);

                // Summarize earthquake mData as a string and add it to
                // result
                result.add(new Country(c.getString("countryName"),
                                    (c.getString("countryCode")),
                                    (c.getString("population")),
                                    (c.getString("areaInSqKm")),
                                    (c.getString("capital"))
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder data = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException");
                }
            }
        }
        return data.toString();
    }


}











