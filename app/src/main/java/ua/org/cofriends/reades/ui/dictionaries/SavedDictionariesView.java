package ua.org.cofriends.reades.ui.dictionaries;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.ui.basic.tools.ContextDeleteController;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedDictionariesView extends AddListLayout implements ContextDeleteController.DeleteTarget<Dictionary> {

    @Inject
    Picasso picasso;

    @Inject
    ContextDeleteController deleteController;

    private DictionaryAdapter adapter;

    public SavedDictionariesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        adapter = new DictionaryAdapter(getContext(), R.string.title_open, picasso);
        listView().setAdapter(adapter);
        textTitle.setText(R.string.title_saved);

        deleteController.registerForDelete(this);
    }

    @Override
    public void onRefresh() {
        SavedDictionariesService.loadList(getContext());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) listView().getItemAtPosition(position);
        BusUtils.post(new Dictionary.SelectedEvent(dictionary));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDictionariesListLoaded(Dictionary.ListLoadedEvent event) {
        adapter.replaceWith(event.getData());

        refreshController.onStopRefresh();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDictionaryActionDone(Dictionary.SavedEvent event) {
        refreshController.refresh();
    }

    @Override
    public ListView getListView() {
        return listView();
    }

    @Override
    public void onActualRemove(Dictionary item) {
        SavedDictionariesService.actUpon(getContext(), item, SavedDictionariesService.DELETE);
    }
}
