package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.LossContextTab;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.MyWorkConstants.MyWorkTasksTable.TASK_NAME;
import static com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.tabs.LossContextTab.buttonStartClaims;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableWorks;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.buttonTasks;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimFutureDated extends ClaimGroupBenefitsSTBaseTest {

    private static final String ERROR_SUBMIT_MESSAGE = "Claim cannot be submitted with future Date of Loss";
    private static final String ERROR_OPEN_MESSAGE = "Claim cannot be opened with future Date of Loss";
    private static final String WARNING_MESSAGE = "Claim can be saved but cannot be submitted before DOL. User will be notified 14 days before DOL.";
    private static final String CLAIM_TASK_NAME = "Follow Up Claim with future DOL";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20801", component = CLAIMS_GROUPBENEFITS)
    public void testClaimFutureDatedPlus14d() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_ST);

        LOGGER.info("TEST: Step #1-2");
        disabilityClaim.initiate(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()),
                        TimeSetterUtil.getInstance().getCurrentTime().plusDays(14).format(DateTimeUtils.MM_DD_YYYY)));

        disabilityClaim.getDefaultWorkspace().fillUpTo(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY),
                completeNotificationTab.getClass());
        completeNotificationTab.submitTab();

        LOGGER.info("TEST: Step #3");
        disabilityClaim.claimSubmit().perform();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_SUBMIT_MESSAGE);

        buttonCancel.click();
        buttonCancel.click();
        dialogConfirmation.confirm();

        LOGGER.info("TEST: Step #4");
        disabilityClaim.claimOpen().perform();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_OPEN_MESSAGE);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20801", component = CLAIMS_GROUPBENEFITS)
    public void testClaimFutureDatedPlus15d() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_ST);

        LOGGER.info("TEST: Step #5");
        disabilityClaim.initiateCreation();
        disabilityClaim.getInitializationView().fillUpTo(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()),
                        TimeSetterUtil.getInstance().getCurrentTime().plusDays(15).format(DateTimeUtils.MM_DD_YYYY)), LossContextTab.class, true);

        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(WARNING_MESSAGE))).isPresent();

        LOGGER.info("TEST: Step #6");
        buttonStartClaims.click();
        buttonSaveAndExit.click();
        buttonTasks.click();

        assertThat(tableWorks.getRow(TASK_NAME.getName(), CLAIM_TASK_NAME)).isPresent();
    }
}
