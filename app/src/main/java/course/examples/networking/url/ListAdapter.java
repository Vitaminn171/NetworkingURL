package course.examples.networking.url;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class ListAdapter extends ArrayAdapter<Country> {

    private final int resourceLayout;
    private final Context mContext;


    public ListAdapter(Context context, int resource, List<Country> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }
        Country c = getItem(position);

        if (c != null) {
            TextView textView = v.findViewById(R.id.textView);
            ImageView img = v.findViewById(R.id.imageView);
            View view = v.findViewById(R.id.view);
            textView.setText(c.getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDetail(c.getName(),c.getPopulation(),c.getArea(),c.getCapital(),c.getURLMap(),c.getURLFlag());
                }
            });

            Glide.with(mContext)
                    .load(c.getURLFlag())
                    .into(img);

        }
        return v;
    }

    private void openDetail(String iName,String iPop,String iArea,String iCap,String urlMapImage,String urlFlagImage) {
        Intent intent = new Intent(mContext, Detail.class);
        intent.putExtra("countryName", iName);
        intent.putExtra("population", iPop);
        intent.putExtra("areaInSqKm", iArea);
        intent.putExtra("capital", iCap);
        intent.putExtra("urlMapImage", urlMapImage);
        intent.putExtra("urlFlagImage", urlFlagImage);
        mContext.startActivity(intent);
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

}
