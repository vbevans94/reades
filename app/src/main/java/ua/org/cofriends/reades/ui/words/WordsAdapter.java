package ua.org.cofriends.reades.ui.words;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.ui.basic.tools.BaseViewHolder;
import ua.org.cofriends.reades.ui.basic.tools.BindableArrayAdapter;
import ua.org.cofriends.reades.utils.BusUtils;

public class WordsAdapter extends BindableArrayAdapter<Word> {

    public WordsAdapter(Context context) {
        super(context, R.layout.item_word);
    }

    @Override
    public void bindView(Word item, int position, View view) {
        WordItemView itemView = (WordItemView) view;
        itemView.setWord(item);
    }
}
