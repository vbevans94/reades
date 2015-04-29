package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.ui.basic.tools.ContextDeleteController;
import ua.org.cofriends.reades.utils.BusUtils;

public class DeviceBooksView extends AddListLayout implements View.OnClickListener, ContextDeleteController.DeleteTarget<Book> {

    @Inject
    ContextDeleteController deleteController;

    @Inject
    Picasso picasso;

    private BooksAdapter adapter;

    public DeviceBooksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // here we override callback assigned to common AddListLayout
        findViewById(R.id.image_add).setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        adapter = new BooksAdapter(getContext(), picasso);
        listView().setAdapter(adapter);
        textTitle.setText(R.string.title_opened);

        deleteController.registerForDelete(this);
    }

    @Override
    public void onClick(@NonNull View v) {
        BusUtils.post(new OpenBookEvent());
    }

    @Override
    public void onRefresh() {
        SavedBooksService.loadList(getContext(), SavedDictionariesService.getCurrent().getFromLanguage(), Book.SourceType.DEVICE);
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) listView().getItemAtPosition(position);
        BusUtils.post(new Book.SelectedEvent(book));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onBooksListLoaded(Book.DeviceListLoadedEvent event) {
        adapter.replaceWith(event.getData());

        refreshController.onStopRefresh();
    }

    /**
     * Saved or deleted. Need to refresh list.
     *
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onBookActionDone(Book.SavedEvent event) {
        refreshController.refresh();
    }

    @Override
    public ListView getListView() {
        return listView();
    }

    @Override
    public void onActualRemove(Book item) {
        SavedBooksService.actUpon(getContext(), item, SavedBooksService.DELETE);
    }

    public static class OpenBookEvent {
    }
}
