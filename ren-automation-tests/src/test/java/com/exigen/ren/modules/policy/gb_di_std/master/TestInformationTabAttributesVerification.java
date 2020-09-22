package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab.helpTextGroupIsMemberCompany;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestInformationTabAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private static final String HELP_TEXT_GROUP_IS_MEMBER_COMPANY = "Company is member of PEO, Association, or Trust";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32205"}, component = POLICY_GROUPBENEFITS)
    public void testInformationTabAttributesVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey() + "[1]", RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()), MEMBER_COMPANY).resolveLinks());
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());

        LOGGER.info("Steps#1, 3 verification");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY)).isRequired().isPresent().hasValue(VALUE_NO);
        helpTextGroupIsMemberCompany.mouseOver();
        assertThat(helpTextGroupIsMemberCompany.getAttribute("title")).isEqualTo(HELP_TEXT_GROUP_IS_MEMBER_COMPANY);//This TC is not for automation but done

        LOGGER.info("Steps#2, 3 verification");
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY).setValue(VALUE_YES);
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.MEMBER_COMPANY_NAME)).isRequired().isPresent().hasValue(EMPTY);

        LOGGER.info("Steps#4, 5 execution");
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), "index=1").resolveLinks(), PolicyInformationTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Steps#6 verification");
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.issue().start();

        assertThat(shortTermDisabilityMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class)
                .getAssetList().getAsset(PolicyInformationIssueActionTabMetaData.NAME_TO_DISPLAY_ON_MP_DOCUMENTS))
                .isPresent().isRequired().hasValue(MEMBER_COMPANY).hasOptions(GROUP_SPONSOR, MEMBER_COMPANY);
    }
}