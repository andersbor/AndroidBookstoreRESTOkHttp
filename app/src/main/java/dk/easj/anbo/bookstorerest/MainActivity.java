
package dk.easj.anbo.bookstorerest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URI = "http://anbo-restserviceproviderbooks.azurewebsites.net/Service1.svc/books";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView listHeader = new TextView(this);
        listHeader.setText("Books");
        listHeader.setTextAppearance(this, android.R.style.TextAppearance_Large); // deprecated
        // https://stackoverflow.com/questions/38200019/settextappearance-deprecated-android-how-to-use/38200053
        ListView listView = findViewById(R.id.main_books_listview);
        listView.addHeaderView(listHeader);
    }

    public void addBook(View view) {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //ReadTask task = new ReadTask();
        //task.execute(BASE_URI);

        getDataUsingOkHttpEnqueue();
    }

    private void getDataUsingOkHttpEnqueue() {
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(BASE_URI);
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() { // Alternative to AsyncTask
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonString = response.body().string();
                    runOnUiThread(new Runnable() {
                        // https://stackoverflow.com/questions/33418232/okhttp-update-ui-from-enqueue-callback
                        @Override
                        public void run() {
                            populateList(jsonString);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView messageView = findViewById(R.id.main_message_textview);
                            messageView.setText(BASE_URI + "\n" + response.code() + " " + response.message());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView messageView = findViewById(R.id.main_message_textview);
                        messageView.setText(ex.getMessage());
                    }
                });
            }
        });
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String uri = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(uri);
            Request request = requestBuilder.build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String jsonString = responseBody.string();
                    return jsonString;
                } else {
                    cancel(true);
                    return uri + "\n" + response.code() + " " + response.message();
                }
            } catch (IOException ex) {
                cancel(true);
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
/*            final List<Book> books = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String author = obj.getString("Author");
                    double price = obj.getDouble("Price");
                    String title = obj.getString("Title");
                    String publisher = obj.getString("Publisher");
                    int id = obj.getInt("Id");
                    Book book = new Book(id, author, title, publisher, price);
                    books.add(book);
                }
*/
            populateList(jsonString);
           /* } catch (JSONException ex) {
                messageTextView.setText(ex.getMessage());
                Log.e("BOOKS", ex.getMessage());
            }*/
        }

        @Override
        protected void onCancelled(String message) {
            TextView messageTextView = findViewById(R.id.main_message_textview);
            messageTextView.setText(message);
            Log.e("BOOKS", message);
        }
    }

    private void populateList(String jsonString) {
        Gson gson = new GsonBuilder().create();
        final Book[] books = gson.fromJson(jsonString, Book[].class);
        ListView listView = findViewById(R.id.main_books_listview);
        //ArrayAdapter<Book> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, books);
        BookListItemAdapter adapter = new BookListItemAdapter(getBaseContext(), R.layout.booklist_item, books);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), BookActivity.class);
                // Book book = books.get((int) id);
                // Book book = books[(int) id];
                Book book = (Book) parent.getItemAtPosition(position);
                intent.putExtra(BookActivity.BOOK, book);
                startActivity(intent);
            }
        });
    }
}
