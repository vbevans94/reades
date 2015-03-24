package ua.org.cofriends.reades.ui.words;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.utils.BusUtils;

public class WordItemView extends LinearLayout {

    @InjectView(R.id.text_word)
    TextView textWord;

    private Word mWord;

    public WordItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    public void setWord(Word word) {
        mWord = word;

        textWord.setText(word.getWord());
    }

    @OnClick(R.id.image_remove)
    @SuppressWarnings("unused")
    void onRemoveClicked() {
        BusUtils.post(new Word.RemoveEvent(mWord));
    }
}
