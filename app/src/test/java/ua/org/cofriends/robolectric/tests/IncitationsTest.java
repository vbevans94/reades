package com.zoomlee.Zoomlee.robolectric.tests;

import com.zoomlee.Zoomlee.BuildConfig;
import com.zoomlee.Zoomlee.incitations.Incitation;
import com.zoomlee.Zoomlee.incitations.IncitationsController;
import com.zoomlee.Zoomlee.net.model.User;
import com.zoomlee.Zoomlee.robolectric.CustomRobolectricRunner;
import com.zoomlee.Zoomlee.ui.adapters.IncitationsAdapter;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

@RunWith(CustomRobolectricRunner.class)
@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
public class IncitationsTest {

    private IncitationsController controller = new IncitationsController();
    private User user = SharedPreferenceUtils.getUtils().getUserSettings();

    @Test
    public void testBigCount() {
        prepareNewUser();

        IncitationsAdapter.Incitated incitated = controller.createIncitated(10, IncitationsController.Screen.DOCUMENTS, 1, 3);
        assertTrue(incitated.getIncitation() == Incitation.SUBSCRIBE_TO_ZOOMLEE
                || incitated.getIncitation() == Incitation.INVITE_YOUR_FRIENDS);
        assertThat(incitated.getIncitationPosition(), is(both(greaterThan(0)).and(lessThan(4))));
    }

    @Test
    public void testSmallCount() {
        prepareNewUser();

        IncitationsAdapter.Incitated incitated = controller.createIncitated(3, IncitationsController.Screen.NOTIFICATIONS, 1, 4);
        assertThat(incitated.getIncitationPosition(), is(both(greaterThan(0)).and(lessThan(4))));
    }

    @Test
    public void testUserWithHome() {
        prepareUserWithHome();

        IncitationsAdapter.Incitated incitated = controller.createIncitated(10, IncitationsController.Screen.TAX_TRACKING, 1, 3);
        assertTrue(incitated.getIncitation() == null);

        incitated = controller.createIncitated(10, IncitationsController.Screen.NOTIFICATIONS, 1, 3);
        assertTrue(incitated.getIncitation() == Incitation.SUBSCRIBE_TO_ZOOMLEE
                || incitated.getIncitation() == Incitation.INVITE_YOUR_FRIENDS);
    }

    @Test
    public void testFriendlyUser() {
        prepareFriendlyUser();

        IncitationsAdapter.Incitated incitated = controller.createIncitated(10, IncitationsController.Screen.DOCUMENTS, 1, 3);
        assertTrue(incitated.getIncitation() == Incitation.SUBSCRIBE_TO_ZOOMLEE);

        incitated = controller.createIncitated(10, IncitationsController.Screen.TAX_TRACKING, 1, 3);
        assertTrue(incitated.getIncitation() == Incitation.SELECT_HOME_COUNTRY);
    }

    @Test
    public void testSubscribedUser() {
        prepareSubscribedUser();

        IncitationsAdapter.Incitated incitated = controller.createIncitated(10, IncitationsController.Screen.TAX_TRACKING, 1, 3);
        assertTrue(incitated.getIncitation() == Incitation.SELECT_HOME_COUNTRY);

        incitated = controller.createIncitated(10, IncitationsController.Screen.NOTIFICATIONS, 1, 3);
        assertTrue(incitated.getIncitation() == Incitation.INVITE_YOUR_FRIENDS);
    }

    private void saveUser() {
        SharedPreferenceUtils.getUtils().saveUserSettings(user);
    }

    private void prepareNewUser() {
        //TODO fix me
//        user.setPlan(0);
        user.setCountryId(-1);
        user.setInvitesCount(0);

        saveUser();
    }

    private void prepareSubscribedUser() {
        prepareNewUser();
        //TODO fix me
//        user.setPlan(1);

        saveUser();
    }

    private void prepareUserWithHome() {
        prepareNewUser();
        user.setCountryId(1);

        saveUser();
    }

    private void prepareFriendlyUser() {
        prepareNewUser();
        user.setInvitesCount(10);

        saveUser();
    }
}