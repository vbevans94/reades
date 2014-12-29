package ua.org.cofriends.reades.ui.tools.refreshable;

import android.widget.ListView;

public interface Refreshable {

    void refreshList();

    ListView listView();

    void refreshed();
}