package ua.org.cofriends.reades.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.dict.DictService;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.adapter.TextPagerAdapter;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.FileUtils;
import ua.org.cofriends.reades.utils.PageSplitter;


public class ReadActivity extends BaseActivity {

    @InjectView(R.id.pager)
    ViewPager mPager;

    public static void start(Book book, Context context) {
        context.startActivity(new Intent(context, ReadActivity.class)
                .putExtras(BundleUtils.writeObject(Book.class, book)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        ButterKnife.inject(this);

        // get book
        Book book = BundleUtils.fetchFromBundle(Book.class, getIntent().getExtras());
        // start dict service for this book
        DictService.getStartedService(book.getDictionary().getDbConfigPath());
        // read book text
        String text = FileUtils.readText(book.getFileUrl());
        // split text to pages
        PageSplitter splitter = new PageSplitter(mPager.getWidth(), mPager.getHeight(), 1, 0);
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
        splitter.append(text, textPaint);
        // add split text to pager
        mPager.setAdapter(new TextPagerAdapter(getSupportFragmentManager(), splitter.getPages(), book));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Book book = BundleUtils.fetchFromBundle(Book.class, getIntent().getExtras());
        DictService.stopByPath(book.getDictionary().getDbConfigPath());
    }
}
