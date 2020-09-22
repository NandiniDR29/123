package com.exigen.ren.modules.policy.gb_eap.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.EmployeeAssistanceProgramCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.CertificatePolicyTabMetaData.ENROLLMENT_TYPE;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.AddressInformationMetaData.ADDRESS_TYPE;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.AddressInformationMetaData.COUNTRY;
import static com.exigen.ren.main.modules.policy.gb_eap.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureCertificatePolicyCertPolicyInsuredTabs extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext, EmployeeAssistanceProgramCertificatePolicyContext {
    private static final AssetList relationShipInformationAssetList = insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23604", "REN-23634"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureCertificatePolicyCertPolicyInsuredTabs() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(employeeAssistanceProgramMasterPolicy.getType());
        employeeAssistanceProgramCertificatePolicy.initiate(getDefaultEAPCertificatePolicyDataGatherData());

        LOGGER.info("REN-23604 Step 1, Step 3, Step 5 and Step 7");
        assertThat(certificatePolicyTab.getAssetList().getAsset(ENROLLMENT_TYPE)).hasValue("New Hire");

        LOGGER.info("REN-23604  Step 9 and Step 11");
        insuredTab.navigateToTab();
        assertThat(insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(ADDRESS_TYPE)).isPresent().isRequired().hasValue("Mailing");
        assertThat(insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(COUNTRY)).isPresent().isRequired().hasValue("United States");

        assertSoftly(softly -> {
            LOGGER.info("REN-23604 Step 15, Step 17 and Step 21");
            ImmutableList.of(JOB_CODE, EMPLOYMENT_STATUS, PAYROLL_FREQUENCY).forEach(control ->
                    softly.assertThat(relationShipInformationAssetList.getAsset(control)).isPresent().isOptional());
            softly.assertThat(insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(PAYROLL_FREQUENCY)).hasValue(StringUtils.EMPTY).hasOptions(StringUtils.EMPTY, "Weekly", "Bi-Weekly", "Monthly", "Semi-Monthly", "Annual", "Hourly", "Unknown");
            LOGGER.info("REN-23634 Step 1, Step 6 and Step 7");
            softly.assertThat(insuredTab.getAssetList().getAsset(GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH)).isPresent().isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(insuredTab.getAssetList().getAsset(RELATIONSHIP_INFORMATION).getAsset(InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS)).isPresent().hasValue(StringUtils.EMPTY);
        });
    }
}