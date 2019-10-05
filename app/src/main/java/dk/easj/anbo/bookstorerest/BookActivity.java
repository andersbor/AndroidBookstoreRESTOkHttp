package dk.easj.anbo.bookstorerest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookActivity extends AppCompatActivity {
    public static final String BOOK = "BOOK";
    private Book book;
    private EditText titleView, authorView, publisherView, priceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra(BOOK);

        TextView headingView = findViewById(R.id.book_heading_textview);
        headingView.setText("Book Id=" + book.getId());

        titleView = findViewById(R.id.book_title_edittext);
        titleView.setText(book.getTitle());

        authorView = findViewById(R.id.book_author_edittext);
        authorView.setText(book.getAuthor());

        publisherView = findViewById(R.id.book_publisher_edittext);
        publisherView.setText(book.getPublisher());

        priceView = findViewById(R.id.book_price_edittext);
        priceView.setText(book.getPrice() + " ");
    }

    public void deleteBook(View view) {
        final String url = "http://anbo-restserviceproviderbooks.azurewebsites.net/Service1.svc/books/" + book.getId();
        //DeleteTask task = new DeleteTask();
        //task.execute(url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).delete().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView messageView = findViewById(R.id.book_message_textview);
                        messageView.setText(ex.getMessage());

                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView messageView = findViewById(R.id.book_message_textview);
                        if (response.isSuccessful()) {
                            messageView.setText("Book deleted");
                            finish();
                        } else {
                            messageView.setText(url + "\n" + response.code() + " " + response.message());
                        }
                    }
                });
            }
        });
    }

    public void updateBook(View view) {
        // code missing: Left as an exercise
        Toast.makeText(this, "Update: Code missing. Left as an exercise", Toast.LENGTH_LONG).show();
    }


    public void back(View view) {
        finish();
    }

    private class DeleteTask extends AsyncTask<String, Void, CharSequence> {
        @Override
        protected CharSequence doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                int responseCode = connection.getResponseCode();
                if (responseCode % 100 != 2) {
                    throw new IOException("Response code: " + responseCode);
                }
                return "Nothing";
            } catch (MalformedURLException e) {
                return e.getMessage() + " " + urlString;
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            TextView messageView = findViewById(R.id.book_message_textview);
            messageView.setText(charSequence);
        }
    }
}
