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
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Title", title);
            jsonObject.put("Author", author);
            jsonObject.put("Publisher", publisher);
            jsonObject.put("Price", price);
            String jsonDocument = jsonObject.toString();
            messageView.setText(jsonDocument);
            PostBookTask task = new PostBookTask();
            task.execute("http://anbo-restserviceproviderbooks.azurewebsites.net/Service1.svc/books", jsonDocument);
        } catch (JSONException ex) {
            messageView.setText(ex.getMessage());
        }

    }

    private class PostBookTask extends AsyncTask<String, Void, CharSequence> {
        //private final String JsonDocument;

        //PostBookTask(String JsonDocument) {
        //    this.JsonDocument = JsonDocument;
        //}

        @Override
        protected CharSequence doInBackground(String... params) {
            String urlString = params[0];
            String jsonDocument = params[1];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(jsonDocument);
                osw.flush();
                osw.close();
                int responseCode = connection.getResponseCode();
                if (responseCode / 100 != 2) {
                    String responseMessage = connection.getResponseMessage();
                    throw new IOException("HTTP response code: " + responseCode + " " + responseMessage);
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                return line;
            } catch (MalformedURLException ex) {
                cancel(true);
                String message = ex.getMessage() + " " + urlString;
                Log.e("BOOK", message);
                return message;
            } catch (IOException ex) {
                cancel(true);
                Log.e("BOOK", ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(CharSequence charSequence) {
            super.onPostExecute(charSequence);
            TextView messageView = findViewById(R.id.add_book_message_textview);
            messageView.setText(charSequence);
            Log.d("MINE", charSequence.toString());
            finish();
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            TextView messageView = findViewById(R.id.add_book_message_textview);
            messageView.setText(charSequence);
            Log.d("MINE", charSequence.toString());
            finish();
        }
    }

}
