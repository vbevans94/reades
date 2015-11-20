package ua.org.cofriends.reades.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ua.org.cofriends.reades.ui.read.PageView;
import ua.org.cofriends.reades.ui.read.ReadActivity;

public class PageSplitter {

    private static final String TAG = Logger.makeLogTag(PageSplitter.class);

    private final int pageWidth;
    private final int pageHeight;
    private final List<CharSequence> pages = new ArrayList<CharSequence>();
    private SpannableStringBuilder currentLine = new SpannableStringBuilder();
    private SpannableStringBuilder currentPage = new SpannableStringBuilder();
    private int pageContentHeight;
    private int currentLineWidth;
    private int textLineHeight;

    public PageSplitter(int pageWidth, int pageHeight) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }

    public void append(String text, TextPaint textPaint) {
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        textLineHeight = fontMetrics.bottom - fontMetrics.top;
        String[] paragraphs = text.split("\n\n", -1);

        int i;
        int total = paragraphs.length;
        for (i = 0; i < paragraphs.length - 1; i++) {
            appendText(paragraphs[i], textPaint);
            appendNewLine();
            publishProgress(i, total);
        }
        appendText(paragraphs[i], textPaint);
        publishProgress(i, total);
    }

    private void publishProgress(int i, int total) {
        int progress = (int) ((1.0f * i / total) * 100);
        BusUtils.post(new ReadActivity.ProgressEvent(progress));
    }

    private void appendText(String text, TextPaint textPaint) {
        String[] words = text.replace('\n', ' ').split(" ", -1);
        int i;
        for (i = 0; i < words.length - 1; i++) {
            appendWord(words[i] + " ", textPaint);
        }
        appendWord(words[i], textPaint);
    }

    private void appendNewLine() {
        currentLine.append("\n");
        checkForPageEnd();
        appendLineToPage();
    }

    private void checkForPageEnd() {
        if (pageContentHeight + textLineHeight > pageHeight) {
            pages.add(currentPage);
            currentPage = new SpannableStringBuilder();
            pageContentHeight = 0;
        }
    }

    private void appendWord(String appendedText, TextPaint textPaint) {
        int textWidth = (int) Math.ceil(textPaint.measureText(appendedText));
        if (currentLineWidth + textWidth >= pageWidth) {
            checkForPageEnd();
            appendLineToPage();
        }
        appendTextToLine(appendedText, textPaint, textWidth);
    }

    private void appendLineToPage() {
        currentPage.append(currentLine);
        pageContentHeight += textLineHeight;

        currentLine = new SpannableStringBuilder();
        currentLineWidth = 0;
    }

    private void appendTextToLine(String appendedText, TextPaint textPaint, int textWidth) {
        currentLine.append(renderToSpannable(appendedText, textPaint));
        currentLineWidth += textWidth;
    }

    public List<CharSequence> getPages() {
        List<CharSequence> copyPages = new ArrayList<CharSequence>(pages);
        SpannableStringBuilder lastPage = new SpannableStringBuilder(currentPage);
        if (pageContentHeight + textLineHeight > pageHeight) {
            copyPages.add(lastPage);
            lastPage = new SpannableStringBuilder();
        }
        lastPage.append(currentLine);
        copyPages.add(lastPage);
        return copyPages;
    }

    private SpannableString renderToSpannable(String text, TextPaint textPaint) {
        SpannableString spannable = new SpannableString(text);

        if (textPaint.isFakeBoldText()) {
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, spannable.length(), 0);
        }
        return spannable;
    }

    public static Spannable splitWords(String textInput, WordClickedListener listener) {
        StringBuilder text = new StringBuilder(textInput);
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
                addSpannable(spanText, word, listener);
                text.delete(start, nextSpace + 1);
            } else {
                CharSequence word = text.substring(start);
                spanText.append(word);
                addSpannable(spanText, word, listener);
                break;
            }
        }

        return spanText;
    }

    private static void addSpannable(Spannable spannable, final CharSequence word, final WordClickedListener listener) {
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View view) {
                if (view instanceof PageView) {
                    PageView pageView = (PageView) view;
                    if (!pageView.hasMoved()) {
                        String escaped = word.toString().replaceAll("\\W", "");
                        listener.onWordClicked(escaped);
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

                ds.setUnderlineText(false);
                ds.setColor(Color.BLACK);
            }
        };
        Logger.d(TAG, "Spannable: %s, word: %s", spannable, word);
        int start = spannable.length() - word.length() - 1;
        if (start < 0) {
            start = 0;
        }
        spannable.setSpan(clickableSpan, start, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public interface WordClickedListener {

        void onWordClicked(CharSequence word);
    }
}