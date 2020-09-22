package com.exigen.ren.modules.policy.gb_di_std.certificate;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.PRIMARY_PARTICIPANT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.GENERAL_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.labelPolicyStatus;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.GB;
import static com.exigen.ren.utils.groups.Groups.GB_DI_STD;
import static com.exigen.ren.utils.groups.Groups.GB_PRECONFIGURED;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestAgeRules extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityCertificatePolicyContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33512"}, component = POLICY_GROUPBENEFITS)
    public void testAgeRulesForPrimaryParticipant() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(shortTermDisabilityMasterPolicy.getType());

        LOGGER.info("REN-33512 Step 1");
        TestData tdCertificatePolicy = shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        shortTermDisabilityCertificatePolicy.initiate(tdCertificatePolicy);

        LOGGER.info("REN-33512 Step 2");
        certificatePolicyTab.fillTab(tdCertificatePolicy);
        String effectiveDate = certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.EFFECTIVE_DATE).getValue();

        LOGGER.info("REN-33512 Step 3");
        Tab.buttonNext.click();
        insuredTab.fillTab(tdCertificatePolicy);
        String dateOfBirth = "08/03/1983";
        insuredTab.getAssetList().getAsset(GENERAL_INFORMATION).getAsset(DATE_OF_BIRTH).setValue(dateOfBirth);

        LOGGER.info("REN-33512 Step 4, 5");
        Tab.buttonNext.click();
        coveragesTab.fillTab(tdCertificatePolicy);
        assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);

        LOGGER.info("REN-33512 Step 7");
        Tab.buttonNext.click();
        ShortTermDisabilityCertificatePolicyContext.premiumSummaryTab.fillTab(tdCertificatePolicy).submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-33512 Step 8");
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String getAgeQuery = "select dateOfBirth,age,firstName from GeneralPartyInfoEntity\n" +
                "where id in ( select personInfo_id from Participant where id in" +
                "( select participant_id from ParticipantGroup_Participant where " +
                "participantGroup_id in (select id from RiskItem where POLICYDETAIL_ID in " +
                "(select policyDetail_id from PolicySummary ps where policyNumber=?)) ))";
        String age = DBService.get().getRow(getAgeQuery, quoteNumber).get("age");

        DateTimeFormatter formatter = DateTimeUtils.MM_DD_YYYY;
        int calculatedAge = Period.between(LocalDate.parse(dateOfBirth, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age).isEqualTo(String.valueOf(calculatedAge));

        LOGGER.info("REN-33512 Step 10");
        shortTermDisabilityCertificatePolicy.dataGather().start();
        String dateOfBirth2 = "08/03/1993";
        insuredTab.navigateToTab().getAssetList().getAsset(GENERAL_INFORMATION).getAsset(DATE_OF_BIRTH).setValue(dateOfBirth2);
        Tab.buttonNext.click();
        coveragesTab.fillTab(tdCertificatePolicy);
        assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);
        Tab.buttonNext.click();
        Tab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatusCp).valueContains(PREMIUM_CALCULATED);
        Tab.buttonNext.click();

        LOGGER.info("REN-33512 Step 11");
        String age2 = DBService.get().getRow(getAgeQuery, quoteNumber).get("age");
        int calculatedAge2 = Period.between(LocalDate.parse(dateOfBirth2, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age2).isEqualTo(String.valueOf(calculatedAge2));

        LOGGER.info("REN-33512 Step 12");
        shortTermDisabilityCertificatePolicy.issue().perform(
                tdCertificatePolicy.adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }
}
