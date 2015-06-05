package com.zoomlee.Zoomlee.robolectric.tests;

import android.content.Context;

import com.zoomlee.Zoomlee.BuildConfig;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.dao.TaxDaoHelper;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.net.model.Tax;
import com.zoomlee.Zoomlee.robolectric.CustomRobolectricRunner;
import com.zoomlee.Zoomlee.utils.TimeUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(CustomRobolectricRunner.class)
@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
public class TaxTest {

    private TaxDaoHelper taxDapHelper;
    private DaoHelper<Country> countryDaoHelper;
    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() throws Exception {
        taxDapHelper = new TaxDaoHelper();
        countryDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Country.class);
    }

    @Test
    public void testShouldBeNotEmpty() {
        Country country = countryDaoHelper.getItemByRemoteId(context,1);
        Tax tax = new Tax();
        tax.setCountryId(country.getRemoteId());
        tax.setArrival(12);
        tax.setDeparture(13);

        taxDapHelper.saveLocalChanges(context, tax);
        taxDapHelper.saveLocalChanges(context, tax);
        List<Tax> taxList = taxDapHelper.getAllItems(context);

        assertEquals(1, taxList.size());
        Tax savedTag = taxList.get(0);
        assertEquals(country.getRemoteId(), savedTag.getCountryId());
        assertEquals(country.getName(), savedTag.getCountryName());
        assertEquals(tax.getArrival(), savedTag.getArrival());
        assertEquals(tax.getDeparture(), savedTag.getDeparture());
    }

    @Test
    public void testShouldBeEmpty() {
        assertTrue(taxDapHelper.getAllItems(context).isEmpty());
    }

    @Test
    public void testShouldBeTwoTax() {
        Tax tax = new Tax();
        tax.setCountryId(1);
        taxDapHelper.saveLocalChanges(context, tax);
        tax.setId(-1);
        taxDapHelper.saveLocalChanges(context, tax);
        assertEquals(2, taxDapHelper.getAllItems(context).size());
    }

    @Test
    public void testDelete() {
        Tax tax = new Tax();
        tax.setCountryId(1);
        taxDapHelper.saveLocalChanges(context, tax);
        tax.setId(-1);
        taxDapHelper.saveLocalChanges(context, tax);
        assertEquals(2, taxDapHelper.getAllItems(context).size());

        taxDapHelper.deleteItem(context, tax);
        assertEquals(1, taxDapHelper.getAllItems(context).size());
    }

    @Test
    public void testSaveDeleted() {
        Tax tax = new Tax();
        tax.setCountryId(1);
        taxDapHelper.saveLocalChanges(context, tax);
        tax.setId(-1);
        taxDapHelper.saveLocalChanges(context, tax);
        assertEquals(2, taxDapHelper.getAllItems(context).size());

        tax.setStatus(Tax.STATUS_DELETED);
        taxDapHelper.saveLocalChanges(context, tax);
        assertEquals(1, taxDapHelper.getAllItems(context).size());
    }

    @Test
    public void testSaveRemoteDeleted() {
        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setRemoteId(1);
        taxDapHelper.saveLocalChanges(context, tax);
        assertEquals(1, taxDapHelper.getAllItems(context).size());
        tax.setId(-1);
        tax.setStatus(Tax.STATUS_DELETED);
        taxDapHelper.saveRemoteChanges(context, tax);

        assertEquals(0, taxDapHelper.getAllItems(context).size());
    }

    @Test
    public void testMarkDeleted() {
        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setRemoteId(1);
        taxDapHelper.saveLocalChanges(context, tax);
        assertEquals(1, taxDapHelper.getAllItems(context).size());
        tax.setStatus(Tax.STATUS_DELETED);
        taxDapHelper.saveLocalChanges(context, tax);

        assertEquals(0, taxDapHelper.getAllItems(context).size());
        assertNotNull(taxDapHelper.getItemByLocalId(context, tax.getId(), true));
    }

    @Test
    public void testSaveRemoteChanges() {
        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setRemoteId(1);
        taxDapHelper.saveRemoteChanges(context, tax);
        assertEquals(1, taxDapHelper.getAllItems(context).size());
    }

    @Test
    public void testUpdateRemoteChanges() {
        Country country = countryDaoHelper.getItemByRemoteId(context, 1);
        Tax tax = new Tax();
        tax.setCountryId(country.getRemoteId());
        tax.setArrival(12);
        tax.setDeparture(13);
        tax.setRemoteId(1);

        taxDapHelper.saveLocalChanges(context, tax);
        assertEquals(1, taxDapHelper.getAllItems(context).size());

        country = countryDaoHelper.getItemByRemoteId(context, 2);
        tax.setId(-1);
        tax.setCountryId(country.getRemoteId());
        tax.setArrival(tax.getArrival() + 1);
        tax.setDeparture(tax.getDeparture() + 1);

        taxDapHelper.saveRemoteChanges(context, tax);
        List<Tax> taxList = taxDapHelper.getAllItems(context);

        assertEquals(1, taxList.size());
        Tax savedTag = taxList.get(0);
        assertEquals(country.getRemoteId(), savedTag.getCountryId());
        assertEquals(country.getName(), savedTag.getCountryName());
        assertEquals(tax.getArrival(), savedTag.getArrival());
        assertEquals(tax.getDeparture(), savedTag.getDeparture());
    }

    @Test
    public void testIntervalInclude() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 2);
        long fromDate = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 5);
        long toDate = calendar.getTimeInMillis() / 1000L;

        calendar.set(Calendar.MONTH, 3);
        long arrival = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 4);
        long departure = calendar.getTimeInMillis() / 1000L;

        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setArrival(arrival);
        tax.setDeparture(departure);

        taxDapHelper.saveLocalChanges(context, tax);

        List<Tax> taxList = taxDapHelper.getTaxInInterval(context, fromDate, toDate);

        assertEquals(1, taxList.size());
        Tax savedTag = taxList.get(0);
        assertEquals(tax.getId(), savedTag.getId());
        assertEquals(tax.getCountryId(), savedTag.getCountryId());
        assertEquals(tax.getArrival(), savedTag.getArrival());
        assertEquals(tax.getDeparture(), savedTag.getDeparture());
        assertEquals(tax.getArrival(), savedTag.getDisplayArrival());
        assertEquals(tax.getDeparture(), savedTag.getDisplayDeparture());
    }

    @Test
    public void testIntervalInterceptDeparture() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 2);
        long fromDate = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 5);
        long toDate = calendar.getTimeInMillis() / 1000L;

        calendar.set(Calendar.MONTH, 1);
        long arrival = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 4);
        long departure = calendar.getTimeInMillis() / 1000L;

        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setArrival(arrival);
        tax.setDeparture(departure);

        taxDapHelper.saveLocalChanges(context, tax);

        List<Tax> taxList = taxDapHelper.getTaxInInterval(context, fromDate, toDate);

        assertEquals(1, taxList.size());
        Tax savedTag = taxList.get(0);
        assertEquals(tax.getId(), savedTag.getId());
        assertEquals(tax.getCountryId(), savedTag.getCountryId());
        assertEquals(tax.getArrival(), savedTag.getArrival());
        assertEquals(tax.getDeparture(), savedTag.getDeparture());
        assertEquals(fromDate, savedTag.getDisplayArrival());
        assertEquals(tax.getDeparture(), savedTag.getDisplayDeparture());
    }

    @Test
    public void testIntervalInterceptArrival() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 2);
        long fromDate = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 5);
        long toDate = calendar.getTimeInMillis() / 1000L;

        calendar.set(Calendar.MONTH, 3);
        long arrival = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 6);
        long departure = calendar.getTimeInMillis() / 1000L;

        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setArrival(arrival);
        tax.setDeparture(departure);

        taxDapHelper.saveLocalChanges(context, tax);

        List<Tax> taxList = taxDapHelper.getTaxInInterval(context, fromDate, toDate);

        assertEquals(1, taxList.size());
        Tax savedTag = taxList.get(0);
        assertEquals(tax.getId(), savedTag.getId());
        assertEquals(tax.getCountryId(), savedTag.getCountryId());
        assertEquals(tax.getArrival(), savedTag.getArrival());
        assertEquals(tax.getDeparture(), savedTag.getDeparture());
        assertEquals(tax.getArrival(), savedTag.getDisplayArrival());
        assertEquals(toDate, savedTag.getDisplayDeparture());
    }

    @Test
    public void testIntervalIntercept() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 2);
        long fromDate = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 5);
        long toDate = calendar.getTimeInMillis() / 1000L;

        calendar.set(Calendar.MONTH, 1);
        long arrival = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.MONTH, 6);
        long departure = calendar.getTimeInMillis() / 1000L;

        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setArrival(arrival);
        tax.setDeparture(departure);

        taxDapHelper.saveLocalChanges(context, tax);

        List<Tax> taxList = taxDapHelper.getTaxInInterval(context, fromDate, toDate);

        assertEquals(1, taxList.size());
        Tax savedTag = taxList.get(0);
        assertEquals(tax.getId(), savedTag.getId());
        assertEquals(tax.getCountryId(), savedTag.getCountryId());
        assertEquals(tax.getArrival(), savedTag.getArrival());
        assertEquals(tax.getDeparture(), savedTag.getDeparture());
        assertEquals(fromDate, savedTag.getDisplayArrival());
        assertEquals(toDate, savedTag.getDisplayDeparture());
    }

    @Test
    public void testYearSplit() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 2);
        long fromDate = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.YEAR, 2009);
        calendar.set(Calendar.MONTH, 5);
        long toDate = calendar.getTimeInMillis() / 1000L;

        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 3);
        long arrival = calendar.getTimeInMillis() / 1000L;
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, 3);
        long departure = calendar.getTimeInMillis() / 1000L;

        long firstTaxBegin = arrival;
        long firstTaxEnd = TimeUtil.getYearsEnd(fromDate);
        long secondTaxBegin = TimeUtil.getNextYearsBegin(fromDate);
        long secondTaxEnd = TimeUtil.getYearsEnd(secondTaxBegin);
        long thirdTaxBegin = TimeUtil.getNextYearsBegin(secondTaxBegin);
        long thirdTaxEnd = toDate;

        Tax tax = new Tax();
        tax.setCountryId(1);
        tax.setArrival(arrival);
        tax.setDeparture(departure);

        taxDapHelper.saveLocalChanges(context, tax);

        List<Tax> taxList = taxDapHelper.getTaxInInterval(context, fromDate, toDate);

        assertEquals(3, taxList.size());
        Tax firstTag = taxList.get(0);
        Tax secondTag = taxList.get(1);
        Tax thirdTag = taxList.get(2);
        
        assertEquals(tax.getId(), firstTag.getId());
        assertEquals(tax.getCountryId(), firstTag.getCountryId());
        assertEquals(tax.getArrival(), firstTag.getArrival());
        assertEquals(tax.getDeparture(), firstTag.getDeparture());
        assertEquals(firstTaxBegin, firstTag.getDisplayArrival());
        assertEquals(firstTaxEnd, firstTag.getDisplayDeparture());

        assertEquals(tax.getId(), secondTag.getId());
        assertEquals(tax.getCountryId(), secondTag.getCountryId());
        assertEquals(tax.getArrival(), secondTag.getArrival());
        assertEquals(tax.getDeparture(), secondTag.getDeparture());
        assertEquals(secondTaxBegin, secondTag.getDisplayArrival());
        assertEquals(secondTaxEnd, secondTag.getDisplayDeparture());

        assertEquals(tax.getId(), thirdTag.getId());
        assertEquals(tax.getCountryId(), thirdTag.getCountryId());
        assertEquals(tax.getArrival(), thirdTag.getArrival());
        assertEquals(tax.getDeparture(), thirdTag.getDeparture());
        assertEquals(thirdTaxBegin, thirdTag.getDisplayArrival());
        assertEquals(thirdTaxEnd, thirdTag.getDisplayDeparture());
    }

    @Test
    public void testTimeUtilGetYearsEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 2);
        long fromDate = calendar.getTimeInMillis() / 1000L;

        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long expectedYearsEnd = calendar.getTimeInMillis() / 1000L;

        assertEquals(expectedYearsEnd, TimeUtil.getYearsEnd(fromDate));
    }

    @Test
    public void testTimeUtilGetNextYearsBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 2);
        long fromDate = calendar.getTimeInMillis() / 1000L;

        calendar.set(Calendar.YEAR, 2008);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long expectedYearsEnd = calendar.getTimeInMillis() / 1000L;

        assertEquals(expectedYearsEnd, TimeUtil.getNextYearsBegin(fromDate));
    }
}