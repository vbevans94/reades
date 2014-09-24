package ua.org.cofriends.reades.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.fragment.PageFragment;

public class TextPagerAdapter extends FragmentStatePagerAdapter {

    private final List<CharSequence> mPageTexts;
    private final Book mBook;

    public TextPagerAdapter(FragmentManager fragmentManager, List<CharSequence> pageTexts, Book book) {
        super(fragmentManager);
        mPageTexts = pageTexts;
        mBook = book;
    }

    @Override
    public Fragment getItem(int i) {
        return PageFragment.newInstance(mPageTexts.get(i), mBook);
    }

    @Override
    public int getCount() {
        return mPageTexts.size();
    }
}