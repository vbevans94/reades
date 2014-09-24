package ua.org.cofriends.reades.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.dict.kernel.IAnswer;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.dict.DictService;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.tools.BaseToast;
import ua.org.cofriends.reades.utils.BundleUtils;

public class PageFragment extends BaseFragment {

    private final static String ARG_PAGE_TEXT = "arg_page_text";

    @InjectView(R.id.text_page)
    TextView textPage;

    private DictService mDictService;

    public static PageFragment newInstance(CharSequence pageText, Book book) {
        PageFragment fragment = new PageFragment();
        // TODO: see if we really need CharSequence
        fragment.setArguments(BundleUtils.putString(BundleUtils.writeObject(Book.class, book)
                , ARG_PAGE_TEXT, pageText.toString()));
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
            int end = text.indexOf(" ");
            if (end == -1) {
                break;
            }
            CharSequence word = text.subSequence(start, end);
            spanText.append(word).append(" ");
            addSpannable(spanText, word);
            text.delete(start, end + 1);
        }

        textPage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
        textPage.setText(text);
        textPage.setMovementMethod(LinkMovementMethod.getInstance());

        Book book = BundleUtils.fetchFromBundle(Book.class, getArguments());
        mDictService = DictService.getStartedService(book.getDictionary().getDbConfigPath());
    }

    private void addSpannable(Spannable spannable, final CharSequence word) {
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View textView) {
                mDictService.search(word);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setUnderlineText(false);
                ds.setColor(Color.BLACK);
            }
        };
        spannable.setSpan(clickableSpan, spannable.length() - word.length(), spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(DictService.AnswerEvent event) {
        IAnswer[] answers = event.getData();
        if (answers.length > 0) {
            BaseToast.show(getActivity(), answers[0].getDefinition());
        }
    }
}