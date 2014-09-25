package ua.org.cofriends.reades.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;

public class PageSplitter {

    private final int pageWidth;
    private final int pageHeight;
    private final float lineSpacingMultiplier;
    private final int lineSpacingExtra;
    private final List<CharSequence> pages = new ArrayList<CharSequence>();
    private SpannableStringBuilder currentLine = new SpannableStringBuilder();
    private SpannableStringBuilder currentPage = new SpannableStringBuilder();
    private int currentLineHeight;
    private int pageContentHeight;
    private int currentLineWidth;
    private int textLineHeight;

    public PageSplitter(int pageWidth, int pageHeight, float lineSpacingMultiplier, int lineSpacingExtra) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        this.lineSpacingExtra = lineSpacingExtra;
    }

    public void append(String text, TextPaint textPaint) {
        textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null) * lineSpacingMultiplier + lineSpacingExtra);
        String[] paragraphs = text.split("\n", -1);
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
        BusUtils.post(new ProgressEvent(progress));
    }

    private void appendText(String text, TextPaint textPaint) {
        String[] words = text.split(" ", -1);
        int i;
        for (i = 0; i < words.length - 1; i++) {
            appendWord(words[i] + " ", textPaint);
        }
        appendWord(words[i], textPaint);
    }

    private void appendNewLine() {
        currentLine.append("\n");
        checkForPageEnd();
        appendLineToPage(textLineHeight);
    }

    private void checkForPageEnd() {
        if (pageContentHeight + currentLineHeight > pageHeight) {
            pages.add(currentPage);
            currentPage = new SpannableStringBuilder();
            pageContentHeight = 0;
        }
    }

    private void appendWord(String appendedText, TextPaint textPaint) {
        int textWidth = (int) Math.ceil(textPaint.measureText(appendedText));
        if (currentLineWidth + textWidth >= pageWidth) {
            checkForPageEnd();
            appendLineToPage(textLineHeight);
        }
        appendTextToLine(appendedText, textPaint, textWidth);
    }

    private void appendLineToPage(int textLineHeight) {
        currentPage.append(currentLine);
        pageContentHeight += currentLineHeight;

        currentLine = new SpannableStringBuilder();
        currentLineHeight = textLineHeight;
        currentLineWidth = 0;
    }

    private void appendTextToLine(String appendedText, TextPaint textPaint, int textWidth) {
        currentLineHeight = Math.max(currentLineHeight, textLineHeight);
        currentLine.append(renderToSpannable(appendedText, textPaint));
        currentLineWidth += textWidth;
    }

    public List<CharSequence> getPages() {
        List<CharSequence> copyPages = new ArrayList<CharSequence>(pages);
        SpannableStringBuilder lastPage = new SpannableStringBuilder(currentPage);
        if (pageContentHeight + currentLineHeight > pageHeight) {
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

    public static class ProgressEvent extends BusUtils.Event<Integer> {

        public ProgressEvent(Integer object) {
            super(object);
        }
    }
}