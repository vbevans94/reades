package com.zoomlee.Zoomlee.robolectric.tests;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zoomlee.Zoomlee.BuildConfig;
import com.zoomlee.Zoomlee.robolectric.CustomRobolectricRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.zoomlee.Zoomlee.provider.helpers.DocumentsHelper.DocumentsContract;
import static com.zoomlee.Zoomlee.provider.helpers.FieldsHelper.FieldsContract;
import static com.zoomlee.Zoomlee.provider.helpers.FilesProviderHelper.FilesContract;
import static com.zoomlee.Zoomlee.provider.helpers.PersonsProviderHelper.PersonsContract;
import static com.zoomlee.Zoomlee.provider.helpers.RestTasksHelper.RestTasksContract;
import static com.zoomlee.Zoomlee.provider.helpers.Tags2DocumentsProviderHelper.Tags2DocumentsContract;
import static com.zoomlee.Zoomlee.provider.helpers.TagsProviderHelper.TagsContract;
import static junit.framework.Assert.assertEquals;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @since 22.04.15.
 */
@RunWith(CustomRobolectricRunner.class)
@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
public class DBTest {

    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testShouldBeEmpty() {
        checkEmpty(DocumentsContract.CONTENT_URI);
        checkEmpty(FieldsContract.CONTENT_URI);
        checkEmpty(FilesContract.CONTENT_URI);
        checkEmpty(PersonsContract.CONTENT_URI);
        checkEmpty(TagsContract.CONTENT_URI);
        checkEmpty(Tags2DocumentsContract.CONTENT_URI);

        Cursor cursor = context.getContentResolver()
                .query(RestTasksContract.CONTENT_URI, null, null, null, null);
        assertEquals(3, cursor.getCount()); //by starting app, 3 tasks will be added in ZoomleeApp.onCreate()
    }

    private void checkEmpty(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }
}
