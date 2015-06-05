package com.zoomlee.Zoomlee.robolectric.tests;

import android.content.ContentResolver;
import android.content.Context;

import com.zoomlee.Zoomlee.BuildConfig;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.net.model.Tag;
import com.zoomlee.Zoomlee.robolectric.CustomRobolectricRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static com.zoomlee.Zoomlee.provider.helpers.TagsProviderHelper.TagsContract;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(CustomRobolectricRunner.class)
@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
public class TagTest {

    private DaoHelper<Tag> tagDaoHelper;
    private DaoHelper<Document> docDaoHelper;
    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception {
        tagDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Tag.class);
        docDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Document.class);
    }

    @Test
    public void testShouldBeNotEmpty() {
        Tag tag = new Tag();
        tag.setName("tag1");
        addTagWithNewDoc(tag);
        assertEquals(1, tagDaoHelper.getAllItems(context).size());
    }

    @Test
    public void testDocumentsCount() {
        Tag tag = new Tag();
        tag.setName("tag1");
        addTagWithNewDoc(tag);
        assertEquals(1, tagDaoHelper.getItemByLocalId(context, tag.getId()).getDocsCount());
        addTagWithNewDoc(tag);
        assertEquals(2, tagDaoHelper.getItemByLocalId(context, tag.getId()).getDocsCount());
    }

    @Test
    public void testShouldBeEmpty() {
        assertTrue(tagDaoHelper.getAllItems(context).isEmpty());
    }

    @Test
    public void testLocalTagRename() {
        Tag tag = new Tag();
        tag.setName("tag1");
        addTagWithNewDoc(tag);

        tag.setName("tag2");
        tagDaoHelper.saveLocalChanges(context, tag);
        assertEquals(1, tagDaoHelper.getAllItems(context).size());
        assertEquals("tag2", tagDaoHelper.getAllItems(context).get(0).getName());
    }

    @Test
    public void testRemoteTagRename() {
        Tag tag = new Tag();
        tag.setName("tag1");
        tag.setRemoteId(1);
        addTagWithNewDoc(tag);

        tag.setId(-1);
        tag.setName("tag2");
        tagDaoHelper.saveRemoteChanges(context, tag);
        assertEquals(1, tagDaoHelper.getAllItems(context).size());
        assertEquals("tag2", tagDaoHelper.getAllItems(context).get(0).getName());
    }

    @Test
    public void testClearTags() {
        Document doc = new Document();
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("tag1");
        tags.add(tag);
        doc.setTagsList(tags);
        docDaoHelper.saveLocalChanges(context, doc);

        doc.setTagsList(new ArrayList<Tag>());
        docDaoHelper.saveLocalChanges(context, doc);
        assertEquals(0, tagDaoHelper.getAllItems(context).size());

        ContentResolver contentResolver = context.getContentResolver();
        assertEquals(0, contentResolver.delete(TagsContract.CONTENT_URI, null, null));
    }

    @Test
    public void testGetByLocalId() {
        Tag tag = new Tag();
        tag.setName("tag1");
        addTagWithNewDoc(tag);

        assertEquals("tag1", tagDaoHelper.getItemByLocalId(context, tag.getId()).getName());
    }

    @Test
    public void testGetByRemoteId() {
        Tag tag = new Tag();
        tag.setName("tag1");
        tag.setRemoteId(2);
        addTagWithNewDoc(tag);

        assertEquals("tag1", tagDaoHelper.getItemByRemoteId(context, tag.getRemoteId()).getName());
    }

    private void addTagWithNewDoc(Tag tag) {
        Document doc = new Document();
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);
        doc.setTagsList(tags);
        docDaoHelper.saveLocalChanges(context, doc);
    }
}