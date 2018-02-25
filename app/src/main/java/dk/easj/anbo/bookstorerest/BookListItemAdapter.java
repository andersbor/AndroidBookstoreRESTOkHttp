package dk.easj.anbo.bookstorerest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class BookListItemAdapter extends ArrayAdapter<Book> {
    private final int resource;

    public BookListItemAdapter(Context context, int resource, List<Book> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Book book = getItem(position);
        String title = book.getTitle();
        String author = book.getAuthor();
        LinearLayout bookView;
        if (convertView == null) {
            bookView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, bookView, true);
        } else {
            bookView = (LinearLayout) convertView;
        }
        TextView titleView = bookView.findViewById(R.id.booklist_item_title);
        TextView authorView = bookView.findViewById(R.id.booklist_item_author);
        titleView.setText(title);
        authorView.setText(" by " + author);
        return bookView;
    }
}

