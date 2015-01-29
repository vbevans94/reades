package ua.org.cofriends.reades.ui.read;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.PageSplitter;

public class PageView extends TextView {

    @InjectView(R.id.text_page)
    TextView textPage;

    private String text;

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String textValue) {
        this.text = textValue;

        parseWords();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        textPage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void parseWords() {
        textPage.setText(PageSplitter.splitWords(text));
    }

    public static class WordRequestEvent extends BusUtils.Event<CharSequence> {

        public WordRequestEvent(CharSequence object) {
            super(object);
        }
    }
}