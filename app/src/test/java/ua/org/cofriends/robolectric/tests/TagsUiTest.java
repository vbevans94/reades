package com.zoomlee.Zoomlee.robolectric.tests;

import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zoomlee.Zoomlee.BuildConfig;
import com.zoomlee.Zoomlee.dao.TagsDocDaoHelper;
import com.zoomlee.Zoomlee.robolectric.CustomRobolectricRunner;
import com.zoomlee.Zoomlee.scopes.tags.DaggerTagsComponent;
import com.zoomlee.Zoomlee.scopes.tags.TagsModule;
import com.zoomlee.Zoomlee.ui.view.tags.TagsView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Author vbevans94.
 */
@RunWith(CustomRobolectricRunner.class)
@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
public class TagsUiTest {

    private static final int NO_TAG_LOCATION = 1;

    private final Random random = new Random();
    private TagsView tagsView;

    @Before
    public void setUp() throws Exception {
        CustomRobolectricRunner.setupApp();

        tagsView = new TagsView(RuntimeEnvironment.application);
        DaggerTagsComponent.builder().tagsModule(new TagsModule(new TagsView.Presenter() {
            @Override
            public void createDocument() {
            }

            @Override
            public void selectTag(TagsDocDaoHelper.TagsDocAlerts tag) {
            }
        })).build()
                .injectView(tagsView);
    }

    @Test
    public void testNoTagWithOthers() {
        List<TagsDocDaoHelper.TagsDocAlerts> tags = prepareTagsWithNoTag();
        tagsView.setTags(tags);

        ListView listView = (ListView) tagsView.findViewById(android.R.id.list);
        ListAdapter adapter = listView.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }

        // tags count should be the same plus one for divider to "no tag"
        assertEquals(adapter.getCount(), tags.size() + 1);

        // last tag should be "no tag"
        TagsDocDaoHelper.TagsDocAlerts lastTag = (TagsDocDaoHelper.TagsDocAlerts) adapter.getItem(adapter.getCount() - 1);
        assertTrue(lastTag.getId() == TagsDocDaoHelper.TagsDocAlerts.NO_TAG_ID);

        // all other tags should persist order
        List<TagsDocDaoHelper.TagsDocAlerts> adapterTags = new ArrayList<>();
        for (int i = 0; i < adapter.getCount() - 2; i++) {
            adapterTags.add((TagsDocDaoHelper.TagsDocAlerts) adapter.getItem(i));
        }
        tags.remove(NO_TAG_LOCATION);
        assertEquals(adapterTags, tags);
    }

    @Test
    public void testNoTagOnly() {
        List<TagsDocDaoHelper.TagsDocAlerts> tags = prepareTagsNoTagOnly();
        tagsView.setTags(tags);

        ListView listView = (ListView) tagsView.findViewById(android.R.id.list);
        ListAdapter adapter = listView.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }

        // tags count should be the same(no empty item for divider)
        assertEquals(adapter.getCount(), tags.size());

        // the only tag should be "no tag"
        TagsDocDaoHelper.TagsDocAlerts firstTag = (TagsDocDaoHelper.TagsDocAlerts) adapter.getItem(0);
        assertTrue(firstTag.getId() == TagsDocDaoHelper.TagsDocAlerts.NO_TAG_ID);
    }

    @Test
    public void testNoNoTag() {
        List<TagsDocDaoHelper.TagsDocAlerts> tags = prepareTagsNoNoTag();
        tagsView.setTags(tags);

        ListView listView = (ListView) tagsView.findViewById(android.R.id.list);
        ListAdapter adapter = listView.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }

        // tags count should be the same(no empty item for divider)
        assertEquals(adapter.getCount(), tags.size());

        // all tags should persist order
        List<TagsDocDaoHelper.TagsDocAlerts> adapterTags = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            adapterTags.add((TagsDocDaoHelper.TagsDocAlerts) adapter.getItem(i));
        }
        assertEquals(adapterTags, tags);
    }

    private List<TagsDocDaoHelper.TagsDocAlerts> prepareTagsWithNoTag() {
        List<TagsDocDaoHelper.TagsDocAlerts> tags = new ArrayList<>();

        tags.add(newTag());
        tags.add(newTag());
        tags.add(NO_TAG_LOCATION, newNoTag());

        return tags;
    }

    private List<TagsDocDaoHelper.TagsDocAlerts> prepareTagsNoTagOnly() {
        List<TagsDocDaoHelper.TagsDocAlerts> tags = new ArrayList<>();

        tags.add(newNoTag());

        return tags;
    }

    private List<TagsDocDaoHelper.TagsDocAlerts> prepareTagsNoNoTag() {
        List<TagsDocDaoHelper.TagsDocAlerts> tags = new ArrayList<>();

        tags.add(newTag());
        tags.add(newTag());
        tags.add(newTag());
        tags.add(newTag());

        return tags;
    }

    private TagsDocDaoHelper.TagsDocAlerts newNoTag() {
        TagsDocDaoHelper.TagsDocAlerts tag = newTag();
        tag.setId(TagsDocDaoHelper.TagsDocAlerts.NO_TAG_ID);

        return tag;
    }

    private TagsDocDaoHelper.TagsDocAlerts newTag() {
        TagsDocDaoHelper.TagsDocAlerts tag = new TagsDocDaoHelper.TagsDocAlerts();
        tag.setName(randomString());
        tag.addDocTypeName(randomString());
        tag.addAlertsCount(random.nextInt(4));

        return tag;
    }

    private String randomString() {
        return "" + random.nextInt();
    }
}
