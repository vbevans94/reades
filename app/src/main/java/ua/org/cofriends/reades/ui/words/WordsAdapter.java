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
import ua.org.cofriends.reades.utils.BusUtils;

public class WordsAdapter extends ArrayAdapter<Word> {

    public WordsAdapter(Context context, List<Word> words) {
        super(context, R.layout.word_item, words);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.word_item, null);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.setWord(getItem(position));

        return view;
    }

    static class ViewHolder extends BaseViewHolder {

        @InjectView(R.id.text_word)
        TextView textName;

        private Word mWord;

        ViewHolder(View view) {
            super(view);
        }
        
        void setWord(Word word) {
            mWord = word;

            textName.setText(word.getWord());
        }

        @OnClick(R.id.image_remove)
        @SuppressWarnings("unused")
        void onRemoveClicked() {
            BusUtils.post(new Word.RemoveEvent(mWord));
        }
    }
}
