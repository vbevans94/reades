package ua.org.cofriends.reades.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.tools.BaseToast;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.Logger;


public class ReadActivity extends BaseActivity {

    public static void start(Book book, Context context) {
        context.startActivity(new Intent(context, ReadActivity.class)
                .putExtras(BundleUtils.writeObject(Book.class, book)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Book book = BundleUtils.fetchFromBundle(Book.class, getIntent().getExtras());
        File file = new File(book.getFileUrl());

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            Logger.e(mTag, "Error reading from book file", e);
            BaseToast.show(this, R.string.error_reading_book_file);
        }

        int start = 0;
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        while (text.length() > 0) {
            int end = text.indexOf(" ");
            if (end == -1) {
                break;
            }
            CharSequence word = text.subSequence(start, end);
            spanText.append(word + " ");
            addSpannable(this, spanText, word);
            text.delete(start, end + 1);
        }

        TextView textView = (TextView) findViewById(R.id.text_content);
        textView.setText(spanText);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onEvent(ReadActivity event) {

    }

    private static void addSpannable(final Context context, Spannable spannable, final CharSequence word) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                BaseToast.show(context, word);
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
}
