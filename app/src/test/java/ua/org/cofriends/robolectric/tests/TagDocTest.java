package com.zoomlee.Zoomlee.robolectric.tests;

import android.content.Context;

import com.zoomlee.Zoomlee.BuildConfig;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.dao.TagsDocDaoHelper;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.net.model.DocumentsType;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.net.model.Tag;
import com.zoomlee.Zoomlee.robolectric.CustomRobolectricRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static com.zoomlee.Zoomlee.dao.TagsDocDaoHelper.TagsDocAlerts;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(CustomRobolectricRunner.class)
@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
public class TagDocTest {

    private TagsDocDaoHelper tagsDocDaoHelper;
    private DaoHelper<Document> docDaoHelper;
    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception {
        tagsDocDaoHelper = new TagsDocDaoHelper(context);
        docDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Document.class);
    }

    @Test
    public void testGetDocsByTag() {
        Tag tag = new Tag();
        tag.setName("tag1");
        addTagWithNewDoc(tag);

        List<Document> docs = tagsDocDaoHelper.getDocumentsByTag(tag.getId());
        assertEquals(1, docs.size());

        addTagWithNewDoc(tag);
        docs = tagsDocDaoHelper.getDocumentsByTag(tag.getId());
        assertEquals(2, docs.size());

        Tag tag2 = new Tag();
        tag2.setName("tag2");
        addTagWithNewDoc(tag2);

        docs = tagsDocDaoHelper.getDocumentsByTag(tag2.getId());
        assertEquals(1, docs.size());

        docs = tagsDocDaoHelper.getDocumentsByTag(tag2.getId() + 1);
        assertEquals(0, docs.size());
    }

    @Test
    public void testGetDocsWithoutTag() {
        Document doc = new Document();
        docDaoHelper.saveLocalChanges(context, doc);

        List<Document> docs = tagsDocDaoHelper.getDocumentsByTag(TagsDocAlerts.NO_TAG_ID);
        assertEquals(1, docs.size());
        assertEquals(doc, docs.get(0));

        Tag tag = new Tag();
        tag.setName("tag1");
        addTagWithNewDoc(tag);

        docs = tagsDocDaoHelper.getDocumentsByTag(tag.getId());
        assertEquals(1, docs.size());

        docs = tagsDocDaoHelper.getDocumentsByTag(TagsDocAlerts.NO_TAG_ID);
        assertEquals(1, docs.size());
    }

    @Test
    public void testGetDocsByTagForPerson() {
        Document doc = new Document();
        doc.setLocalPersonId(1);
        docDaoHelper.saveLocalChanges(context, doc);

        Document doc2 = new Document();
        doc.setLocalPersonId(1);
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("tag1");
        tags.add(tag);
        doc2.setTagsList(tags);
        doc2.setLocalPersonId(1);
        docDaoHelper.saveLocalChanges(context, doc2);

        List<Document> docs = tagsDocDaoHelper.getDocumentsByTag(TagsDocAlerts.NO_TAG_ID, doc.getLocalPersonId(), null);
        assertEquals(1, docs.size());
        assertEquals(doc, docs.get(0));

        docs = tagsDocDaoHelper.getDocumentsByTag(tag.getId(), doc2.getLocalPersonId(), null);
        assertEquals(1, docs.size());
        assertEquals(doc2, docs.get(0));
    }

    @Test
    public void testGetTagsDocAlerts() {
        Document otherDoc1 = new Document();
        otherDoc1.setTypeId(DocumentsType.OTHER_DOC_TYPE.getRemoteId());
        Document passDoc = new Document();
        passDoc.setTypeId(DocumentsType.PASSPORT_DOC_TYPE.getRemoteId());
        Document otherDoc2 = new Document();
        otherDoc2.setTypeId(DocumentsType.OTHER_DOC_TYPE.getRemoteId());
        Document passDocNoTag = new Document();
        passDocNoTag.setTypeId(DocumentsType.PASSPORT_DOC_TYPE.getRemoteId());
        Document otherDoc3 = new Document();
        otherDoc3.setTypeId(DocumentsType.OTHER_DOC_TYPE.getRemoteId());

        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        tag1.setName("tag1");
        Tag tag2 = new Tag();
        tag2.setName("tag2");

        tags.add(tag1);
        tags.add(tag2);
        otherDoc1.setTagsList(tags);

        tags = new ArrayList<>();
        tags.add(tag1);
        passDoc.setTagsList(tags);

        tags = new ArrayList<>();
        tags.add(tag1);
        otherDoc2.setTagsList(tags);

        docDaoHelper.saveLocalChanges(context, otherDoc1);
        docDaoHelper.saveLocalChanges(context, passDoc);
        docDaoHelper.saveLocalChanges(context, otherDoc2);
        docDaoHelper.saveLocalChanges(context, passDocNoTag);
        docDaoHelper.saveLocalChanges(context, otherDoc3);

        List<TagsDocAlerts> tagsDocAlertsList = tagsDocDaoHelper.getTagsDocAlerts(Person.ALL_ID);
        assertEquals(3, tagsDocAlertsList.size());

        for (TagsDocAlerts tagsDocAlerts: tagsDocAlertsList) {
            if (tagsDocAlerts.getId() == tag1.getId()) {
                assertEquals(2, tagsDocAlerts.getDocsTypesNames().size());
            } else if (tagsDocAlerts.getId() == tag2.getId()) {
                assertEquals(1, tagsDocAlerts.getDocsTypesNames().size());
            } else if (tagsDocAlerts.getId() == TagsDocAlerts.NO_TAG_ID) {
                assertEquals(2, tagsDocAlerts.getDocsTypesNames().size());
            } else {
                assertTrue(false);
            }
        }
    }

    private void addTagWithNewDoc(Tag tag) {
        Document doc = new Document();
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);
        doc.setTagsList(tags);
        docDaoHelper.saveLocalChanges(context, doc);
    }
}