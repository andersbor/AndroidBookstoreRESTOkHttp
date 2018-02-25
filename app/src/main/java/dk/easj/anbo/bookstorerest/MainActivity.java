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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

        TextView listHeader = new TextView(this);
        listHeader.setText("Books");
        listHeader.setTextAppearance(this, android.R.style.TextAppearance_Large);
        ListView listView = findViewById(R.id.main_books_listview);
        listView.addHeaderView(listHeader);
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
        protected void onPostExecute(CharSequence jsonString) {
            TextView messageTextView = findViewById(R.id.main_message_textview);
            /*
            final List<Book> books = new ArrayList<>();
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
            Gson gson = new GsonBuilder().create();
            final Book[] books = gson.fromJson(jsonString.toString(), Book[].class);

            ListView listView = findViewById(R.id.main_books_listview);
            //ArrayAdapter<Book> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, books);
            BookListItemAdapter adapter = new BookListItemAdapter(getBaseContext(), R.layout.booklist_item, books);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getBaseContext(), BookActivity.class);
                    //Book book = books.get((int) id);
                    Book book = books[(int) id];
                    intent.putExtra("BOOK", book);
                    startActivity(intent);
                }
            });
           /* } catch (JSONException ex) {
                messageTextView.setText(ex.getMessage());
                Log.e("BOOKS", ex.getMessage());
            }*/
        }

        @Override
        protected void onCancelled(CharSequence message) {
            TextView messageTextView = findViewById(R.id.main_message_textview);
            messageTextView.setText(message);
            Log.e("BOOKS", message.toString());
        }
    }
}
