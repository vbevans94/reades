package ua.org.cofriends.espresso.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ListView;

import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.entity.Language;
import ua.org.cofriends.reades.ui.dictionaries.DictionariesActivity;
import ua.org.cofriends.reades.ui.dictionaries.SavedDictionariesView;
import ua.org.cofriends.reades.utils.BusUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
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

    public void testEmptyView() throws Exception {
        BusUtils.postToUi(new Dictionary.ListLoadedEvent(new ArrayList<Dictionary>()));

        Thread.sleep(1000);
        assertEquals(0, ((ListView) getActivity().findViewById(R.id.list)).getCount());
    }

    public void testTest() throws Exception{
        Thread.sleep(1000);
        List<Dictionary> dictionaries = new ArrayList<>();
        dictionaries.add(getTestDictionary());
        BusUtils.postToUi(new Dictionary.ListLoadedEvent(dictionaries));

        Thread.sleep(1000);
        ListView listView = (ListView) getActivity().findViewById(R.id.list);
        assertEquals(1, listView.getCount());
    }

    private Dictionary getTestDictionary() {
        Dictionary dictionary = new Dictionary();
        dictionary.setFromLanguage(new Language());
        dictionary.setToLanguage(new Language());
        return dictionary;
    }
}