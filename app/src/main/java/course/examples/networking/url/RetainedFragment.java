package course.examples.networking.url;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RetainedFragment extends Fragment {
    static final String TAG = "RetainedFragment";
    private OnFragmentInteractionListener mListener;

//    private String mResult;

    // Get your own user name at http://www.geonames.org/login
//    private static final String USER_NAME = "caoth";
//    private static final String HOST = "api.geonames.org";
//    private static final String URL = "http://" + HOST + "/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
//            + USER_NAME;

    private static final String URL = "http://api.geonames.org/countryInfoJSON?username=dean";

    public RetainedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    void onButtonPressed() {
        //new HttpGetTask(this).execute();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // load from network
                //final String result = "";
                final List<Country> result = loadFromNetwork();

                // run on ui thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //onDownloadFinished(result);
                        onDownloadFinished("", result);
                    }
                });
            }
        }).start();
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
                //result.add(new Country(c.getString("countryName"),(c.getString("countryCode"))));
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

    private void onDownloadFinished(String result, List<Country> iCountries) {
        if (null != mListener) {
            mListener.onDownloadfinished(result, iCountries);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onDownloadfinished(String result, List<Country> result2);
    }


    /*static class HttpGetTask extends AsyncTask<Void, Void, String> {

        private static final String TAG = "HttpGetTask";

        // Get your own user name at http://www.geonames.org/login
        private static final String USER_NAME = "aporter";

        private static final String HOST = "api.geonames.org";
        private static final String URL = "http://" + HOST + "/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
                + USER_NAME;

        private final WeakReference<RetainedFragment> mListener;


        HttpGetTask(RetainedFragment retainedFragment) {
            mListener = new WeakReference<>(retainedFragment);
        }


        @Override
        protected String doInBackground(Void... params) {
            String data = null;
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
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null != mListener.get()) {
                mListener.get().onDownloadFinished(result);
            }
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
    }*/
}
