package dk.easj.anbo.bookstorerest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
    }

    public void addBook2(View view) {
        String title = ((EditText) findViewById(R.id.add_book_title_edittext)).getText().toString();
        String author = ((EditText) findViewById(R.id.add_book_author_edittext)).getText().toString();
        String publisher = ((EditText) findViewById(R.id.add_book_publisher_edittext)).getText().toString();
        String priceString = ((EditText) findViewById(R.id.add_book_price_edittext)).getText().toString();
        double price = Double.parseDouble(priceString);
        //String jsonDocument =
        //        "{\"Title\":\"" + title + "\", \"Author\":\"" + author + "\", \"Publisher\":\"" + publisher + "\", \"Price\":" + price + "}";

        TextView messageView = findViewById(R.id.add_book_message_textview);
        try { // Alternative: make a Book object + use Gson
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Title", title);
            jsonObject.put("Author", author);
            jsonObject.put("Publisher", publisher);
            jsonObject.put("Price", price);
            String jsonDocument = jsonObject.toString();
            messageView.setText(jsonDocument);
            PostBookOkHttpTask task = new PostBookOkHttpTask();
            task.execute("http://anbo-restserviceproviderbooks.azurewebsites.net/Service1.svc/books", jsonDocument);
        } catch (JSONException ex) {
            messageView.setText(ex.getMessage());
        }

    }

    private class PostBookOkHttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String postdata = strings[1];
            MediaType MEDIA_TYPE = MediaType.parse("application/json");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MEDIA_TYPE, postdata);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                     return response.body().string();
                } else {
                    String message = url + "\n" + response.code() + " " + response.message();
                    return message;
                }
            } catch (IOException ex) {
                Log.e("BOOKS", ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            TextView messageView = findViewById(R.id.add_book_message_textview);
            messageView.setText(jsonString);
            Log.d("MINE", jsonString.toString());
           //  finish();
        }

        @Override
        protected void onCancelled(String message) {
            super.onCancelled(message);
            TextView messageView = findViewById(R.id.add_book_message_textview);
            messageView.setText(message);
            Log.d("MINE", message.toString());
            //finish();
        }
    }
}
