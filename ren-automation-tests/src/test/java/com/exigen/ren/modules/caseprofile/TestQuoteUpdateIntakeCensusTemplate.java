package com.exigen.ren.modules.caseprofile;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.AbstractEditableStringElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData;
import com.exigen.ren.main.modules.caseprofile.pages.ProcessingResultsPage;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.caseprofile.tabs.ProductAndPlanManagementTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.utils.components.Components;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.pages.ProcessingResultsPage.RecordValidationResult.EMPLOYEE_NUMBER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteUpdateIntakeCensusTemplate extends CaseProfileBaseTest {

    @Test(groups = {GB, GB_DN, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-3565", component = Components.CASE_PROFILE)
    public void testQuoteUpdateIntakeCensusTemplateForDn() {
        openAppCreateNonIndCustomerInitNewCaseProfile();
        fillCaseProfile(GroupBenefitsMasterPolicyType.GB_DN.getName());
        assertCoveragesRelationshipsInfoSection("1",
                ImmutableMap.of(
                        ProcessingResultsPage.getLabelFromSection("Dental", "Plan"), "ALACARTE",
                        ProcessingResultsPage.getLabelFromSection("Dental", "Class"), "1",
                        ProcessingResultsPage.getLabelFromSection("Dental", "Tier"), "EO",
                        ProcessingResultsPage.getRadioButtonFromSection("Dental", "eligible"), VALUE_YES,
                        ProcessingResultsPage.getRadioButtonFromSection("Dental", "elected"), VALUE_YES));
    }

    @Test(groups = {GB, GB_VS, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-3565", component = Components.CASE_PROFILE)
    public void testQuoteUpdateIntakeCensusTemplateForGv() {
        openAppCreateNonIndCustomerInitNewCaseProfile();
        fillCaseProfile("Group Vision");
        assertCoveragesRelationshipsInfoSection("2",
                ImmutableMap.of(
                        ProcessingResultsPage.getLabelFromSection("Vision", "Plan"), "ALACARTE",
                        ProcessingResultsPage.getLabelFromSection("Vision", "Class"), "1",
                        ProcessingResultsPage.getLabelFromSection("Vision", "Tier"), "ES",
                        ProcessingResultsPage.getRadioButtonFromSection("Vision", "eligible"), VALUE_YES,
                        ProcessingResultsPage.getRadioButtonFromSection("Vision", "elected"), VALUE_YES));
    }

    private void fillCaseProfile(String product) {
        caseProfile.getDefaultWorkspace().fillUpTo(tdCaseProfile.getTestData("CaseProfile", "TestData_PolicyCreationWithOnePlan")
                        .adjust(CaseProfileDetailsTab.class.getSimpleName(), tdCaseProfile.getTestData("CaseProfile", "CaseProfileDetailsTabWithIntakeProfile"))
                        .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName(), ProductAndPlanManagementTabMetaData.PRODUCT.getLabel()), product),
                FileIntakeManagementTab.class);
        caseProfile.getDefaultWorkspace().getTab(FileIntakeManagementTab.class).getAssetList().fill(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    private void assertCoveragesRelationshipsInfoSection(String employeeNumber, ImmutableMap<AbstractEditableStringElement, String> assertMap) {
        NavigationPage.toMainTab(NavigationEnum.FileIntakeManagementTab.PROCESSING_RESULTS);
        RetryService.run(predicate ->
                ProcessingResultsPage.tableProcessedFiles.getRow(1).getCell(ProcessingResultsPage.ProcessedFiles.STATUS.getName()).getValue().contains("Success"),
                () -> {BrowserController.get().driver().navigate().refresh();
                       return null;
                       },
                StopStrategies.stopAfterAttempt(15), WaitStrategies.fixedWait(2, TimeUnit.SECONDS));

        assertThat(ProcessingResultsPage.tableProcessedFiles).isPresent();
        RetryService.run(predicate ->
                        (ProcessingResultsPage.tableRecordValidationResult.isPresent() &&
                                ProcessingResultsPage.tableRecordValidationResult.getRow(ImmutableMap.of(EMPLOYEE_NUMBER.getName(),employeeNumber)).isPresent()),
                () -> {ProcessingResultsPage.tableProcessedFiles.getRow(1).getCell(1).click();
                    return null;
                },
                StopStrategies.stopAfterAttempt(15), WaitStrategies.fixedWait(2, TimeUnit.SECONDS));

        ProcessingResultsPage.tableRecordValidationResult.getRow(ImmutableMap.of(EMPLOYEE_NUMBER.getName(),employeeNumber)).getCell(EMPLOYEE_NUMBER.getName()).click();
        assertSoftly(softly -> assertMap.forEach((key, value) ->
                softly.assertThat(key).hasValue(value)));

    }
}
