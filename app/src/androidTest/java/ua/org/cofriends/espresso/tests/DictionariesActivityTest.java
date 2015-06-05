package ua.org.cofriends.espresso.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.dictionaries.DictionariesActivity;
import ua.org.cofriends.reades.utils.BusUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
public class DictionariesActivityTest extends ActivityInstrumentationTestCase2<DictionariesActivity> {

    public DictionariesActivityTest() {
        super(DictionariesActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        getActivity();
    }

    public void testAddClick() {
        onView(withId(R.id.image_add)).perform(click());
        onView(withText(R.string.title_store)).check(matches(isDisplayed()));
    }

    public void testEmptyView() {
        onView(withText(R.string.message_no_items)).check(matches(isDisplayed()));
    }

    public void testTest() {
        List<Dictionary> dictionaries = new ArrayList<>();
        dictionaries.add(new Dictionary());
        BusUtils.post(new Dictionary.ListLoadedEvent(dictionaries));

        ListView listView = (ListView) getActivity().findViewById(R.id.list);
        assertEquals(listView.getCount(), 1);
    }
}