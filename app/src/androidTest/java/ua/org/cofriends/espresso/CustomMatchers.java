package ua.org.cofriends.espresso;

import android.support.v4.view.ViewPager;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {

    /**
     * Matches {@link android.support.v4.view.ViewPager} on selected page.
     *
     * @param selectedPage to check
     * @return new instance of matcher
     */
    public static Matcher<View> selectedPage(final int selectedPage) {
        return new BaseMatcher<View>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof ViewPager) {
                    ViewPager pager = (ViewPager) o;
                    return pager.getCurrentItem() == selectedPage;
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ViewPager selected page should be " + selectedPage);
            }
        };
    }
}
