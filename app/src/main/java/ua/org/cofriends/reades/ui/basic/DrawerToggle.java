package ua.org.cofriends.reades.ui.basic;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.R;

public class DrawerToggle extends ActionBarDrawerToggle {

    private DrawerToggle(Activity activity, DrawerLayout drawerLayout) {
        super(activity, drawerLayout, R.string.description_drawer_open, R.string.description_drawer_close);

        setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(this);
    }

    public static DrawerToggle fromActivity(Activity activity) {
        DrawerLayout drawerLayout = ButterKnife.findById(activity, R.id.drawer);
        return new DrawerToggle(activity, drawerLayout);
    }
}
