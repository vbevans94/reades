package ua.org.cofriends.reades.ui.activity;

import android.content.Context;
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
import android.widget.Toast;

import ua.org.cofriends.reades.R;


public class ReadActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        String sample = "Android is a Software stack";
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            text.append(sample + "\n");
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

    private static void addSpannable(final Context context, Spannable spannable, final CharSequence word) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Toast.makeText(context, word, Toast.LENGTH_SHORT).show();
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
