package ua.org.cofriends.reades.ui.read;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.utils.Logger;

public class TextPagerAdapter extends PagerAdapter {

    private static final String TAG = Logger.makeLogTag(TextPagerAdapter.class);
    private final List<CharSequence> mPageTexts;
    private final Context mContext;

    public TextPagerAdapter(Context context, List<CharSequence> pageTexts) {
        mPageTexts = pageTexts;
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return View.inflate(mContext, R.layout.page, null);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        PageView page = (PageView) object;
        page.setText(mPageTexts.get(position).toString());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Logger.d(TAG, "destroyed at " + position);
    }

    @Override
    public int getCount() {
        return mPageTexts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}