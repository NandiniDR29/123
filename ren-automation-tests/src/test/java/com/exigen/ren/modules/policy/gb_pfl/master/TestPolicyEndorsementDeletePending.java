/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.PolicyConstants.EndorsementReason.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyEndorsementDeletePending extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"IPBQA-24491", "REN-27894"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyEndorsementDeletePending() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterPolicy(paidFamilyLeaveMasterPolicy.getType());

        assertSoftly(softly -> {
            LOGGER.info("REN-27894 Step 1");
            paidFamilyLeaveMasterPolicy.endorse().start();
            softly.assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(StartEndorsementActionTabMetaData.OTHER_REASON))).isAbsent();

            LOGGER.info("REN-27894 Step 2");
            AbstractContainer<?, ?> assetListForStartEndorsementActionTab = paidFamilyLeaveMasterPolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList();
            softly.assertThat(assetListForStartEndorsementActionTab.getAsset(StartEndorsementActionTabMetaData.ENDORSEMENT_REASON))
                    .hasOptions(EMPTY, ACQUISITION_OR_MERGER, ADDITION_OF_NEW_COVERAGE, CHANGEIN_COVERED_LIVES, CHANGE_IN_ELIGIBLE_CLASS, COMMISSION_CHANGE,
                    EOI_ENROLLMENT_CHANGE, LEGAL_NAME_CHANGE, PLAN_DESIGN_CHANGE, POLICY_ANNIVERSARY_CHANGE, SITUS_STATE_CHANGE, POLICY_YEAR_CHANGE, OTHER);

            LOGGER.info("REN-27894 Step 4");
            assetListForStartEndorsementActionTab.getAsset(StartEndorsementActionTabMetaData.ENDORSEMENT_REASON).setValue(OTHER);
            softly.assertThat(assetListForStartEndorsementActionTab.getAsset(StartEndorsementActionTabMetaData.OTHER_REASON)).hasValue("").isEnabled();

            LOGGER.info("REN-27894 Step 7");
            assetListForStartEndorsementActionTab.getAsset(StartEndorsementActionTabMetaData.ENDORSEMENT_REASON).setValue(ACQUISITION_OR_MERGER);
            assetListForStartEndorsementActionTab.getAsset(StartEndorsementActionTabMetaData.ENDORSEMENT_DATE)
                    .setValue(TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).format(DateTimeUtils.MM_DD_YYYY));
            Tab.buttonOk.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(NavigationPage.PolicyNavigation.isLeftMenuTabSelected(NavigationEnum.ClaimFNOLLeftMenu.POLICY_INFORMATION.get())).isTrue();
            PolicyInformationTab.buttonCancel.click();
            Page.dialogConfirmation.confirm();

            LOGGER.info("TEST: Delete Pending Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
            paidFamilyLeaveMasterPolicy.endorse().perform(paidFamilyLeaveMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
            PolicySummaryPage.buttonPendedEndorsement.click();
            paidFamilyLeaveMasterPolicy.deletePendedTransaction().perform(new SimpleDataProvider());
            softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
        });
    }
}
