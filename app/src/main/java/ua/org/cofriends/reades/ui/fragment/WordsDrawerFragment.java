package ua.org.cofriends.reades.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.InjectView;
import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.service.SavedWordsService;
import ua.org.cofriends.reades.ui.adapter.WordsAdapter;

public class WordsDrawerFragment extends BaseFragment {

    @InjectView(R.id.list_words)
    ListView mListWords;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_words_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListWords.setEmptyView(view.findViewById(R.id.text_empty));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragmentContainerView = getActivity().findViewById(R.id.layout_drawer);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity()
                , mDrawerLayout
                , android.R.color.transparent
                , R.string.description_drawer_open
                , R.string.description_drawer_close
        ) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                ActivityCompat.invalidateOptionsMenu(getActivity());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                ActivityCompat.invalidateOptionsMenu(getActivity());
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * @return true if drawer is not null and opened
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public void openDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    public void toggleDrawer() {
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListWords.getAdapter() == null) {
            SavedWordsService.loadList(getActivity());
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Word.ListLoadedEvent event) {
        mListWords.setAdapter(new WordsAdapter(getActivity(), event.getData()));
    }

    @OnItemClick(R.id.list_words)
    @SuppressWarnings("unused")
    public void onWordClicked(int position) {
        Word word = (Word) mListWords.getItemAtPosition(position);
        DefinitionFragment.show(getActivity().getSupportFragmentManager(), word);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Word.RemoveEvent event) {
        SavedWordsService.delete(getActivity(), event.getData());
    }
}