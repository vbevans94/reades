package ua.org.cofriends.reades.ui.read;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.dict.kernel.IAnswer;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.dict.DictService;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.entity.Page;
import ua.org.cofriends.reades.ui.basic.BaseActivity;
import ua.org.cofriends.reades.ui.words.DefinitionDialogFactory;
import ua.org.cofriends.reades.ui.basic.tools.UiUtils;
import ua.org.cofriends.reades.ui.basic.BaseViewPager;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.FileUtils;
import ua.org.cofriends.reades.utils.LocalStorage;
import ua.org.cofriends.reades.utils.PageSplitter;
import ua.org.cofriends.reades.utils.TaskUtils;


public class ReadActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @Optional
    @InjectView(R.id.pager)
    ViewPager pager;

    @Optional
    @InjectView(R.id.progress)
    ProgressBar progressBar;

    @Optional
    @InjectView(R.id.text_page_info)
    TextView textPageInfo;

    private DictService dictService;

    public static void start(Book book, Dictionary dictionary, Context context) {
        Bundle extras = BundleUtils.writeObject(Book.class, book
                , BundleUtils.writeObject(Dictionary.class, dictionary));

        context.startActivity(new Intent(context, ReadActivity.class)
                .putExtras(extras));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);

        ButterKnife.inject(this);

        dictService = DictService.getStartedService(getDictionary().getDbConfigPath());

        setTitle(getBook().getName());

        pager.setOnPageChangeListener(this);
    }

    private Book getBook() {
        return BundleUtils.fetchFromBundle(Book.class, getIntent().getExtras());
    }

    private Dictionary getDictionary() {
        return BundleUtils.fetchFromBundle(Dictionary.class, getIntent().getExtras());
    }

    @Override
    protected void onStop() {
        super.onStop();

        int page = pager.getCurrentItem();
        LocalStorage.INSTANCE.setInt(getString(R.string.key_book, getBook().getBookId()), page);
        LocalStorage.INSTANCE.setInt(getString(R.string.key_book_id), getBook().getBookId());
        LocalStorage.INSTANCE.setInt(getString(R.string.key_dictionary_id), getDictionary().getDictionaryId());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSizeChanged(BaseViewPager.SizeChangedEvent event) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.text_normal));
        int marginRoot = getResources().getDimensionPixelSize(R.dimen.root_margin);
        int height = pager.getHeight() - 2 * marginRoot;
        int width = pager.getWidth() - 2 * marginRoot;
        TaskUtils.execute(new PagingTask(), new PagingTask.Params(getBook(), height, width, textPaint));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPagingDone(PagingTask.DoneEvent event) {
        UiUtils.hide(progressBar);
        UiUtils.show(textPageInfo);

        // set pages and go to previously finished-on page
        pager.setAdapter(new TextPagerAdapter(this, event.getData()));
        int page = LocalStorage.INSTANCE.getInt(getString(R.string.key_book, getBook().getBookId()));
        pager.setCurrentItem(page);
        displayPage(page);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onProgress(ProgressEvent event) {
        int progress = event.getData();
        progressBar.setProgress(progress);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onWordRequested(PageView.WordRequestEvent event) {
        dictService.search(event.getData());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAnswerReady(DictService.AnswerEvent event) {
        IAnswer[] answers = event.getData();
        if (answers.length > 0) {
            DefinitionDialogFactory.show(this, answers[0]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // in case it wasn't stopped:)
        DictService.stopByPath(getDictionary().getDbConfigPath());
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int page) {
        displayPage(page);
    }

    private void displayPage(int page) {
        textPageInfo.setText(getString(R.string.text_page_info, page + 1, pager.getAdapter().getCount()));
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @OnClick(R.id.text_page_info)
    @SuppressWarnings("unused")
    void onPageClicked() {
        final int count = pager.getAdapter().getCount();
        final EditText editText = (EditText) View.inflate(this, R.layout.dialog_page, null);
        editText.setHint(getString(R.string.hint_enter_page, count));

        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int page = Integer.parseInt(editText.getText().toString());

                        if (page < 1 || page > count) {
                            Toast.makeText(getApplicationContext(), R.string.message_invalid_page, Toast.LENGTH_SHORT).show();
                        } else {
                            pager.setCurrentItem(page - 1);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private static class PagingTask extends AsyncTask<PagingTask.Params, Integer, List<CharSequence>> {

        @Override
        protected List<CharSequence> doInBackground(Params... paramsArray) {
            Params params = paramsArray[0];
            Book book = params.mBook.meFromDb();
            // fetch from database
            List<Page> pages = Page.find(Page.class, "book = ?", Long.toString(book.getId()));
            List<CharSequence> contents;
            int pageNumber = 0;
            if (!pages.isEmpty()) {
                // extract content from pages
                contents = new ArrayList<>();
                for (Page page : pages) {
                    contents.add(page.getContent());
                    publishProgress(pageNumber++, pages.size());
                }
            } else {
                // read book text
                String text = FileUtils.readText(params.mBook.getFileUrl());
                // split text to pages
                PageSplitter splitter = new PageSplitter(params.mWidth, params.mHeight);
                splitter.append(text, params.mTextPaint);
                contents = splitter.getPages();
                // save to database for next fetches
                for (CharSequence content : contents) {
                    Page page = new Page(content.toString(), pageNumber, book);
                    page.save();
                    publishProgress(pageNumber++, contents.size());
                }
            }
            return contents;
        }

        private void publishProgress(int i, int total) {
            int progress = (int) ((1.0f * i / total) * 100);
            BusUtils.post(new ProgressEvent(progress));
        }

        @Override
        protected void onPostExecute(List<CharSequence> charSequences) {
            BusUtils.post(new DoneEvent(charSequences));
        }

        static class Params {

            final Book mBook;
            final int mHeight;
            final int mWidth;
            private final TextPaint mTextPaint;

            Params(Book book, int height, int width, TextPaint textPaint) {
                mBook = book;
                mHeight = height;
                mWidth = width;
                mTextPaint = textPaint;
            }
        }

        static class DoneEvent extends BusUtils.Event<List<CharSequence>> {

            DoneEvent(List<CharSequence> object) {
                super(object);
            }
        }
    }

    public static class ProgressEvent extends BusUtils.Event<Integer> {

        public ProgressEvent(Integer object) {
            super(object);
        }
    }
}
