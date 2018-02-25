package dk.easj.anbo.bookstorerest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReadTask task = new ReadTask();
        task.execute("http://anbo-restserviceproviderbooks.azurewebsites.net/Service1.svc/books");
    }

    public void addBook(View view) {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }

    private class ReadTask extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence charSequence) {
            TextView messageTextView = findViewById(R.id.main_message_textview);
            //messageTextView.setText(charSequence);
            final List<Book> books = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(charSequence.toString());
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
                ListView listView = findViewById(R.id.main_books_listview);
                ArrayAdapter<Book> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, books);
                //BookListItemAdapter adapter = new BookListItemAdapter(getBaseContext(), R.layout.booklist_item, books);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), BookActivity.class);
                        intent.putExtra("BOOK", books.get((int) id));
                        startActivity(intent);
                    }
                });
            } catch (JSONException ex) {
                messageTextView.setText(ex.getMessage());
                Log.e("BOOKS", ex.getMessage());
            }
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            TextView messageTextView = findViewById(R.id.main_message_textview);
            messageTextView.setText(charSequence);
            Log.e("BOOKS", charSequence.toString());
        }
    }
}
