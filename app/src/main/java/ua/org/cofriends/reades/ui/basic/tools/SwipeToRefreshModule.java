package ua.org.cofriends.reades.ui.basic.tools;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.basic.ActivityModule;
import ua.org.cofriends.reades.ui.books.DownloadBooksView;
import ua.org.cofriends.reades.ui.books.SavedBooksView;
import ua.org.cofriends.reades.ui.dictionaries.SavedDictionariesView;

@Module(injects = {
        SavedDictionariesView.class,
        SavedBooksView.class,
        DownloadBooksView.class,
        DownloadBooksView.class
}, addsTo = ActivityModule.class, complete = false, library = true)
public class SwipeToRefreshModule {

    private final SwipeRefreshLayout swipeToRefresh;
    private final SwipeRefreshLayout.OnRefreshListener swipeListener;

    public SwipeToRefreshModule(SwipeRefreshLayout swipeToRefresh, SwipeRefreshLayout.OnRefreshListener listener) {
        this.swipeToRefresh = swipeToRefresh;
        this.swipeListener = listener;
        this.swipeToRefresh.setOnRefreshListener(listener);
        this.swipeToRefresh.setColorSchemeResources(R.color.indigo, R.color.light_indigo);
    }

    @Singleton
    @Provides
    @ForSwipe
    public AbsListView.OnScrollListener provideListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (view == null || view.getChildCount() == 0) ? 0 : view.getChildAt(0).getTop();
                swipeToRefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        };
    }

    @Singleton
    @Provides
    public RefreshController provideSwipeController() {
        return new RefreshController() {
            @Override
            public void onStartRefresh() {
                swipeToRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeToRefresh.setRefreshing(true);
                    }
                });
            }

            @Override
            public void onStopRefresh() {
                swipeToRefresh.setRefreshing(false);
            }

            @Override
            public void refresh() {
                onStartRefresh();
                // call after because in logic onStopRefresh can be called
                swipeListener.onRefresh();
            }
        };
    }

    public interface RefreshController {

        void onStartRefresh();

        void onStopRefresh();

        void refresh();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public static @interface ForSwipe {
    }
}
