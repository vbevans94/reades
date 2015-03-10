package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.read.PageView;
import ua.org.cofriends.reades.utils.Logger;

public class BookPagerAdapter extends PagerAdapter {

    private static final String TAG = Logger.makeLogTag(BookPagerAdapter.class);
    private final Context mContext;

    public BookPagerAdapter(Context context) {
        mContext = context;
    }

    public Book.SourceType getItem(int position) {
        return Book.SourceType.values()[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(mContext, getItem(position).getViewId(), null);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Logger.d(TAG, "destroyed at " + position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(getItem(position).getTitleString());
    }

    @Override
    public int getCount() {
        return Book.SourceType.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}