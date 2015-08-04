package ua.org.cofriends.robolectric.tests;

import android.content.Context;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.plus.model.people.Person;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import ua.org.cofriends.reades.BuildConfig;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.entity.Language;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.tools.ContextMenuController;
import ua.org.cofriends.robolectric.CustomRobolectricRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(CustomRobolectricRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class MenuControllerTest {

    private Context context = RuntimeEnvironment.application;

    @Test
    public void testShouldBeNotEmpty() throws Exception {
        ContextMenuController controller = new ContextMenuController();
        ListView listView = new ListView(context);
        String[] samples = new String[]{"1", "2"};
        listView.setAdapter(new ArrayAdapter<>(context, R.layout.item_dictionary, samples));

        ContextMenuController.MenuTarget mockTarget = Mockito.mock(ContextMenuController.MenuTarget.class);
        Mockito.when(mockTarget.getMenuRes()).thenReturn(R.menu.menu_for_item);
        Mockito.when(mockTarget.getView()).thenReturn(listView);

        controller.registerForContextMenu(mockTarget);

        // menu must have been asked
        Mockito.verify(mockTarget).getMenuRes();

        // long press on any item
        listView.performLongClick();

        // should result in menu callback triggered
        Mockito.verify(mockTarget).onMenuItemSelected(Matchers.<MenuItem>any(), Matchers.<List<Integer>>any());
    }
}