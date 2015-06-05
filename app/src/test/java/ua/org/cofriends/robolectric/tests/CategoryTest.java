package com.zoomlee.Zoomlee.robolectric.tests;

import android.content.Context;

import com.zoomlee.Zoomlee.BuildConfig;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Category;
import com.zoomlee.Zoomlee.robolectric.CustomRobolectricRunner;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

import java.util.List;

@RunWith(CustomRobolectricRunner.class)
@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
public class CategoryTest {

    private static final String CUSTOM_CATEGORY_ORDER_KEY = "custom_category_order";

    private DaoHelper<Category> categoryDaoHelper;
    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception {
        categoryDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Category.class);
    }

    @Test
    public void testNotCustomOrderPrefs() {
        boolean customOrder = SharedPreferenceUtils.getUtils().getBooleanSettings(CUSTOM_CATEGORY_ORDER_KEY);
        assertFalse(customOrder);
    }

    @Test
    public void testNotEmpty() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        assertFalse(categoryList.isEmpty());
    }

    @Test
    public void testBaseTableWithRemoteId() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        for (Category category: categoryList)
            assertTrue(category.getRemoteId() >= 0);
    }

    @Test
    public void testSizeNotChanged() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        categoryDaoHelper.saveRemoteChanges(context, categoryList);
        List<Category> newCategoryList = categoryDaoHelper.getAllItems(context);
        assertEquals(categoryList.size(), newCategoryList.size());
    }

    @Test
    public void testSaveRemoteChangesList() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        Category category = categoryList.get(0);
        category.setWeight(category.getWeight() + 1);
        category.setName(category.getName() + "_new");

        categoryDaoHelper.saveRemoteChanges(context, categoryList);

        Category newCategory = categoryDaoHelper.getItemByLocalId(context, category.getId());
        assertEquals(category.getWeight(), newCategory.getWeight());
        assertEquals(category.getName(), newCategory.getName());
    }

    @Test
    public void testSaveRemoteChangesItem() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        Category category = categoryList.get(0);
        category.setWeight(category.getWeight() + 1);
        category.setName(category.getName() + "_new");

        categoryDaoHelper.saveRemoteChanges(context, category);

        Category newCategory = categoryDaoHelper.getItemByLocalId(context, category.getId());
        assertEquals(category.getWeight(), newCategory.getWeight());
        assertEquals(category.getName(), newCategory.getName());
    }

    @Test
    public void testSaveLocalChangedWeight() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        Category category = categoryList.get(0);
        category.setWeight(category.getWeight() + 1);
        category.setName(category.getName() + "_new");

        categoryDaoHelper.saveLocalChanges(context, category);

        Category newCategory = categoryDaoHelper.getItemByLocalId(context, category.getId());
        assertEquals(category.getWeight(), newCategory.getWeight());
        assertEquals(category.getName(), newCategory.getName());

        boolean customOrder = SharedPreferenceUtils.getUtils().getBooleanSettings(CUSTOM_CATEGORY_ORDER_KEY);
        assertTrue(customOrder);
    }

    @Test
    public void testSaveRemoteChangesListWithoutWeight() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        Category category = categoryList.get(0);

        categoryDaoHelper.saveLocalChanges(context, categoryList.get(1));

        category.setWeight(category.getWeight() + 1);
        category.setName(category.getName() + "_new");

        categoryDaoHelper.saveRemoteChanges(context, category);

        Category newCategory = categoryDaoHelper.getItemByLocalId(context, category.getId());
        assertEquals(category.getWeight() - 1, newCategory.getWeight());
        assertEquals(category.getName(), newCategory.getName());
    }

    @Test
    public void testSaveRemoteChangesItemWithoutWeight() {
        List<Category> categoryList = categoryDaoHelper.getAllItems(context);
        Category category = categoryList.get(0);

        categoryDaoHelper.saveLocalChanges(context, categoryList.get(1));

        category.setWeight(category.getWeight() + 1);
        category.setName(category.getName() + "_new");

        categoryDaoHelper.saveRemoteChanges(context, categoryList);

        Category newCategory = categoryDaoHelper.getItemByLocalId(context, category.getId());
        assertEquals(category.getWeight() - 1, newCategory.getWeight());
        assertEquals(category.getName(), newCategory.getName());
    }
}