package ua.org.cofriends.reades.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class PageFragment extends BaseFragment {

    private final static String ARG_PAGE_TEXT = "arg_page_text";

    @InjectView(R.id.text_page_info)
    TextView textPage;

    public static PageFragment newInstance(CharSequence pageText) {
        PageFragment fragment = new PageFragment();
        fragment.setArguments(BundleUtils.putString(null, ARG_PAGE_TEXT, pageText.toString()));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StringBuilder text = new StringBuilder(BundleUtils.getString(getArguments(), ARG_PAGE_TEXT));

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

        textPage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_normal));
        textPage.setText(spanText);
        textPage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Dummy handler.
     */
    @SuppressWarnings("unused")
    public void onEvent(PageFragment fragment) {
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