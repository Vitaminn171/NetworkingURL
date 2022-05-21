package course.examples.networking.url;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
Tên: Lý Quốc An
MSSV: 3119410002
 */


public class Exchange extends NetworkingURLActivity {


    private static final String TAG = "Exchange Currency";
    private TextView textViewAmount,textViewResult;
    private Spinner to,from;
    private Button button;
    private EditText currency_to_be_converted,currency_converted;
    private static final String URLxml = "https://usd.fxexchangerate.com/rss.xml";
    List<Currency> mCur;
    String[][] item;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange);
        currency_to_be_converted = findViewById(R.id.currency_to_be_converted);
        currency_converted = findViewById(R.id.currency_converted);
        button = findViewById(R.id.button);
        to = findViewById(R.id.to);
        from= findViewById(R.id.from);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        currency_to_be_converted.addTextChangedListener(onTextChangedListener());
        //String[] dropDownList;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Currency> result = loadFromNetworkXML();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onDownloadfinished(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }




    private void onDownloadfinished(List<Currency> iCur) throws IOException {
        String[] dropDownList = new String[161];
        item = new String[iCur.size()+1][2];
        // the value of 1 row is "name of currency" and the "exchange rate"
        String name = "United States Dollar(USD)";
        String rate = "1";
        mCur = iCur;
        int i = 0;
        while (i < iCur.size()){
            Currency c = mCur.get(i);
            if(i == 0){
                c.setName(name);
                c.setCurrency(rate);
                dropDownList[0] = name;
            }
            else {
                String str1 = c.getName().replace("United States Dollar(USD)/","");
                int index = str1.indexOf("(") + 1;

                char[] nameOfCurrency = new char[3];
                str1.getChars(index, index + 3, nameOfCurrency, 0);
                c.setName(str1);
                dropDownList[i] = str1;


                String str = c.getCurrency().replace("1 United States Dollar = ","");
                String numberOnly = str.replaceAll("[^0-9.]", "");
                c.setCurrency(numberOnly);
            }
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, dropDownList);
        to.setAdapter(adapter);
        from.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convert(iCur);
            }
        });
    }

    public void convert(List<Currency> iCur){

        String textTo = to.getSelectedItem().toString();
        String textFrom = from.getSelectedItem().toString();
        double rate1 = 0,rate2 = 0;
        for(int i = 0; i < iCur.size(); i++){
            Currency c = iCur.get(i);
            if(textTo.equals(c.getName())){
                rate1 = Double.parseDouble(c.getCurrency());
            }
            if(textFrom.equals(c.getName())){
                rate2 = Double.parseDouble(c.getCurrency());
            }
        }
        String get_amount = currency_to_be_converted.getText().toString().replace(",","");
        double amount = Double.parseDouble(get_amount);
        double convert_1 = amount * rate1;
        double convert_2 = convert_1 / rate2;
        //String result = String.valueOf(convert_2);
        @SuppressLint("DefaultLocale") String result = String.format("%.2f",convert_2);
        int index1 = result.indexOf(".");
        String temp;
        temp = result.substring(0,index1);



        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String result1 = formatter.format(Integer.parseInt(temp));
        String result2 = result.replace(temp,result1);
        currency_converted.setText(result2);
    }


    private List<Currency> loadFromNetworkXML() {
        String data = null;
        List<Currency> result = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(URLxml)
                    .openConnection();

            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());
            result = parsePullXML(in);

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
        } catch (IOException exception) {
            Log.e(TAG, "IOException");
        } finally {
            if (null != httpUrlConnection) {
                httpUrlConnection.disconnect();
            }
        }

        return result;
    }

    List<Currency> parsePullXML(InputStream in) {
        List<Currency> result = new ArrayList<>();

        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(in,null);
            String tag = "" , text = "";
            int event = parser.getEventType();
            int flag = 0;
            Currency cur = null;

            while (event!= XmlPullParser.END_DOCUMENT){
                tag = parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if(tag.equals("item")) {
                            cur = new Currency();
                            flag = 1;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text=parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(flag == 1) {
                            switch (tag) {
                                case "title":
                                    cur.setName(text);
                                    break;
                                case "description":
                                    cur.setCurrency(text);
                                    result.add(cur);
                                    break;
                            }
                        }
                        break;
                }
                event = parser.next();
            }

        }
        catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return result;
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currency_to_be_converted.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    currency_to_be_converted.setText(formattedString);
                    currency_to_be_converted.setSelection(currency_to_be_converted.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                currency_to_be_converted.addTextChangedListener(this);
            }
        };
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

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
