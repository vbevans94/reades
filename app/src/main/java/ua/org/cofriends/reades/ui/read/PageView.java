package ua.org.cofriends.reades.ui.read;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.basic.BaseFrameLayout;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class PageView extends BaseFrameLayout {

    @InjectView(R.id.text_page_info)
    TextView textPage;

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.page_view, this);
    }

    public void setText(String textValue) {
        StringBuilder text = new StringBuilder(textValue);

        int start = 0;
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        while (text.length() > 0) {
            int nextSpace = text.indexOf(" ");
            int nextBreak = text.indexOf("\n");
            String delimiter = " ";
            if (nextBreak != -1 && nextBreak < nextSpace) {
                nextSpace = nextBreak;
                delimiter = "\n";
            }
            if (nextSpace != -1) {
                CharSequence word = text.subSequence(start, nextSpace);
                spanText.append(Html.fromHtml(word.toString())).append(delimiter);
                addSpannable(spanText, word);
                text.delete(start, nextSpace + 1);
            } else {
                CharSequence word = text.substring(start);
                spanText.append(word);
                addSpannable(spanText, word);
                break;
            }
        }

        textPage.setText(spanText);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textPage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_normal));
        textPage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Dummy handler.
     */
    @SuppressWarnings("unused")
    public void onEvent(PageView fragment) {
        // nothing here
    }

    private void addSpannable(Spannable spannable, final CharSequence word) {
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View textView) {
                String escaped = word.toString().replaceAll("\\W", "");
                BusUtils.post(new WordRequestEvent(escaped));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setUnderlineText(false);
                ds.setColor(Color.BLACK);
            }
        };
        spannable.setSpan(clickableSpan, spannable.length() - word.length() - 1, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static class WordRequestEvent extends BusUtils.Event<CharSequence> {

        public WordRequestEvent(CharSequence object) {
            super(object);
        }
    }
}