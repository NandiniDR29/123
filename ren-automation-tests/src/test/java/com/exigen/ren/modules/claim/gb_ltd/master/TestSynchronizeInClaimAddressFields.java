package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentInquiryPaymentDetailsTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;

import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.*;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationPartiesInsuranceAgentTabMetaData.COUNTRY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationSponsorTabMetaData.SPONSOR_ADDRESS;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDRESS_DETAILS;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.CITY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_LINE_1;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_LINE_2;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_LINE_3;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AddressDetailsMetaData.STATE_PROVINCE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSynchronizeInClaimAddressFields extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentPaymentPaymentDetailsTab paymentDetailsTab = disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentDetailsTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-41136", component = CLAIMS_GROUPBENEFITS)
    public void testSynchronizeInClaimAddressFields() {
        Map<AssetDescriptor, String> mapFirstAddress = new HashMap<>();
        mapFirstAddress.put(COUNTRY, "United States");
        mapFirstAddress.put(ZIP_POST_CODE, "90001");
        mapFirstAddress.put(ADDRESS_LINE_1, "Address Line 1");
        mapFirstAddress.put(ADDRESS_LINE_2, "Address Line 2");
        mapFirstAddress.put(ADDRESS_LINE_3, "Address Line 3");
        mapFirstAddress.put(CITY, "Los Angeles");
        mapFirstAddress.put(STATE_PROVINCE, "CA");

        Map<AssetDescriptor, String> mapSecondAddress = new HashMap<>();
        mapSecondAddress.put(COUNTRY, "United Kingdom");
        mapSecondAddress.put(ZIP_POST_CODE, "10001");
        mapSecondAddress.put(ADDRESS_LINE_1, "Address Line test 1");
        mapSecondAddress.put(ADDRESS_LINE_2, "Address Line test 2");
        mapSecondAddress.put(ADDRESS_LINE_3, "Address Line test 3");
        mapSecondAddress.put(CITY, "New York");
        mapSecondAddress.put(STATE_PROVINCE, "CA");

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(makeKeyPath(generalTab.getMetaKey(), ADDRESS_DETAILS.getLabel(), COUNTRY.getLabel()), mapFirstAddress.get(COUNTRY))
                .adjust(makeKeyPath(generalTab.getMetaKey(), ADDRESS_DETAILS.getLabel(), ZIP_POST_CODE.getLabel()), mapFirstAddress.get(ZIP_POST_CODE))
                .adjust(makeKeyPath(generalTab.getMetaKey(), ADDRESS_DETAILS.getLabel(), STATE_PROVINCE.getLabel()), mapFirstAddress.get(STATE_PROVINCE))
                .adjust(makeKeyPath(generalTab.getMetaKey(), ADDRESS_DETAILS.getLabel(), ADDRESS_LINE_1.getLabel()), mapFirstAddress.get(ADDRESS_LINE_1))
                .adjust(makeKeyPath(generalTab.getMetaKey(), ADDRESS_DETAILS.getLabel(), CITY.getLabel()), mapFirstAddress.get(CITY)));
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        createDefaultLongTermDisabilityMasterPolicy();

        LOGGER.info("TEST REN-41136: Step 1");
        TestData tdClaimCreation = disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY);
        disabilityClaim.initiate(tdClaimCreation);
        disabilityClaim.getDefaultWorkspace().fillUpTo(tdClaimCreation, policyInformationSponsorTab.getClass(), false);
        assertThat(policyInformationSponsorTab.getAssetList().getAsset(SPONSOR_ADDRESS))
                .hasValue(String.format("%s %s %s %s %s", mapFirstAddress.get(ADDRESS_LINE_1), mapFirstAddress.get(CITY), mapFirstAddress.get(STATE_PROVINCE),
                        mapFirstAddress.get(ZIP_POST_CODE), mapFirstAddress.get(COUNTRY)));

        LOGGER.info("TEST REN-41136: Step 2");
        policyInformationSponsorTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFromTo(tdClaimCreation
                        .adjust(makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), BENEFIT.getLabel()), "index=1")
                        .adjust(makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), PARTY_NAME.getLabel()), "contains=Sponsor")
                        .mask(makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), SOCIAL_SECURITY_NUMBER_SSN.getLabel())),
                eventInformationLossEventTab.getClass(), additionalPartiesAdditionalPartyTab.getClass(), true);

        assertSoftly(softly -> {
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.COUNTRY)).hasValue(mapFirstAddress.get(COUNTRY));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.ZIP_POSTAL_CODE)).hasValue(mapFirstAddress.get(ZIP_POST_CODE));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.ADDRESS_LINE_1)).hasValue(mapFirstAddress.get(ADDRESS_LINE_1));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.STATE_PROVINCE)).hasValue(mapFirstAddress.get(STATE_PROVINCE));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.CITY)).hasValue(mapFirstAddress.get(CITY));
        });
        AdditionalPartiesAdditionalPartyTab.buttonSaveAndExit.click();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST REN-41136: Step 3");
        NavigationPage.toMainTab(CUSTOMER);
        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_Address3"));

        search(claimNumber);
        toSubTab(FNOL);
        RetryService.run(predicate -> additionalPartiesAdditionalPartyTab.navigate().getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.COUNTRY).getValue().equals(mapSecondAddress.get(COUNTRY)),
                () -> {
                    AdditionalPartiesAdditionalPartyTab.buttonCancel.click();
                    Page.dialogConfirmation.confirm();
                    toSubTab(FNOL);
                    return null;
                },
                StopStrategies.stopAfterAttempt(15),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.COUNTRY)).hasValue(mapSecondAddress.get(COUNTRY));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.ZIP_POSTAL_CODE)).hasValue(mapSecondAddress.get(ZIP_POST_CODE));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.ADDRESS_LINE_1)).hasValue(mapSecondAddress.get(ADDRESS_LINE_1));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.ADDRESS_LINE_2)).hasValue(mapSecondAddress.get(ADDRESS_LINE_2));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.ADDRESS_LINE_3)).hasValue(mapSecondAddress.get(ADDRESS_LINE_3));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(AdditionalPartiesAdditionalPartyTabMetaData.CITY)).hasValue(mapSecondAddress.get(CITY));
        });
        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonOpenClaim.click();

        LOGGER.info("TEST REN-41136: Step 6");
        disabilityClaim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(paymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=Sponsor")
                .adjust(makeKeyPath(paymentDetailsTab.getMetaKey(), STATE.getLabel()), "AB"), PaymentPaymentPaymentDetailsTab.class, true);

        assertSoftly(softly -> {
            softly.assertThat(paymentDetailsTab.getAssetList().getAsset(PaymentPaymentPaymentDetailsTabMetaData.COUNTRY)).hasValue(mapSecondAddress.get(COUNTRY));
            softly.assertThat(paymentDetailsTab.getAssetList().getAsset(PaymentPaymentPaymentDetailsTabMetaData.ZIP_POSTAL_CODE)).hasValue(mapSecondAddress.get(ZIP_POST_CODE));
            softly.assertThat(paymentDetailsTab.getAssetList().getAsset(PaymentPaymentPaymentDetailsTabMetaData.ADDRESS_LINE_1)).hasValue(mapSecondAddress.get(ADDRESS_LINE_1));
            softly.assertThat(paymentDetailsTab.getAssetList().getAsset(PaymentPaymentPaymentDetailsTabMetaData.ADDRESS_LINE_2)).hasValue(mapSecondAddress.get(ADDRESS_LINE_2));
            softly.assertThat(paymentDetailsTab.getAssetList().getAsset(PaymentPaymentPaymentDetailsTabMetaData.ADDRESS_LINE_3)).hasValue(mapSecondAddress.get(ADDRESS_LINE_3));
            softly.assertThat(paymentDetailsTab.getAssetList().getAsset(PaymentPaymentPaymentDetailsTabMetaData.CITY)).hasValue(mapSecondAddress.get(CITY));
        });

        LOGGER.info("TEST REN-41136: Step 7");
        PaymentPaymentPaymentDetailsTab.buttonNext.click();
        disabilityClaim.addPayment().getWorkspace().fillFromTo(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment"), PaymentPaymentPaymentAllocationTab.class, PaymentPaymentAdditionalPayeeTab.class);
        disabilityClaim.addPayment().submit();

        NavigationPage.toMainTab(CUSTOMER);
        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_Address1"));
        checkNewAddress(claimNumber, mapFirstAddress, mapFirstAddress);

        LOGGER.info("TEST REN-41136: Step 8");
        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);

        NavigationPage.toMainTab(CUSTOMER);
        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_Address3"));
        checkNewAddress(claimNumber, mapSecondAddress, mapSecondAddress);

        LOGGER.info("TEST REN-41136: Step 9");
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        NavigationPage.toMainTab(CUSTOMER);
        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_Address1"));
        checkNewAddress(claimNumber, mapFirstAddress, mapFirstAddress);

        LOGGER.info("TEST REN-41136: Step 10");
        disabilityClaim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(makeKeyPath(paymentDetailsTab.getMetaKey(), PAYMENT_TO.getLabel()), "contains=Sponsor")
                .adjust(makeKeyPath(paymentDetailsTab.getMetaKey(), STATE.getLabel()), "AB"));
        disabilityClaim.voidPayment().perform(disabilityClaim.getLTDTestData().getTestData("ClaimPayment", "TestData_VoidPayment"), 2);

        NavigationPage.toMainTab(CUSTOMER);
        customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_Address3"));
        checkNewAddress(claimNumber, mapSecondAddress, mapSecondAddress);
    }

    private void checkNewAddress(String claimNumber, Map<AssetDescriptor, String> mapAddressForSponsor, Map<AssetDescriptor, String> mapAddressForPayment) {
        checkSponsorAddress(claimNumber, mapAddressForSponsor);

        PaymentInquiryPaymentDetailsTab paymentInquiryDetailsTab = claim.paymentInquiry().getWorkspace().getTab(PaymentInquiryPaymentDetailsTab.class);
        claim.paymentInquiry().start(1);
        assertSoftly(softly -> {
            softly.assertThat(paymentInquiryDetailsTab.getInquiryAssetList().getAsset(PaymentInquiryPaymentDetailsTabMetaData.COUNTRY_VALUE)).hasValue(mapAddressForPayment.get(COUNTRY));
            softly.assertThat(paymentInquiryDetailsTab.getInquiryAssetList().getAsset(PaymentInquiryPaymentDetailsTabMetaData.ZIP_POSTAL_CODE_VALUE)).hasValue(mapAddressForPayment.get(ZIP_POST_CODE));
            softly.assertThat(paymentInquiryDetailsTab.getInquiryAssetList().getAsset(PaymentInquiryPaymentDetailsTabMetaData.ADDRESS_LINE_1_VALUE)).hasValue(mapAddressForPayment.get(ADDRESS_LINE_1));
            softly.assertThat(paymentInquiryDetailsTab.getInquiryAssetList().getAsset(PaymentInquiryPaymentDetailsTabMetaData.ADDRESS_LINE_2_VALUE)).hasValue(mapAddressForPayment.get(ADDRESS_LINE_2));
            softly.assertThat(paymentInquiryDetailsTab.getInquiryAssetList().getAsset(PaymentInquiryPaymentDetailsTabMetaData.ADDRESS_LINE_3_VALUE)).hasValue(mapAddressForPayment.get(ADDRESS_LINE_3));
            softly.assertThat(paymentInquiryDetailsTab.getInquiryAssetList().getAsset(PaymentInquiryPaymentDetailsTabMetaData.CITY_VALUE)).hasValue(mapAddressForPayment.get(CITY));
        });
        PaymentPaymentPaymentDetailsTab.buttonCancel.click();
    }

    private void checkSponsorAddress(String claimNumber, Map<AssetDescriptor, String> mapAddressForSponsor) {
        search(claimNumber);
        RetryService.run(predicate -> ClaimSummaryPage.tableClaimSponsor.getRow(1).getCell(ClaimConstants.ClaimSponsorTable.SPONSOR_ADDRESS).getValue()
                        .equals(String.format("%s %s %s %s %s %s %s", mapAddressForSponsor.get(ADDRESS_LINE_1), mapAddressForSponsor.get(ADDRESS_LINE_2), mapAddressForSponsor.get(ADDRESS_LINE_3), mapAddressForSponsor.get(CITY),
                                mapAddressForSponsor.get(STATE_PROVINCE), mapAddressForSponsor.get(ZIP_POST_CODE), mapAddressForSponsor.get(COUNTRY))),
                () -> {
                    toSubTab(OVERVIEW);
                    return null;
                },
                StopStrategies.stopAfterAttempt(15),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
    }
}
