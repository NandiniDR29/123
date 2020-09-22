package com.exigen.ren.modules.caseprofile;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab.ClassificationSubGroups.COVERAGE;
import static com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab.ClassificationSubGroups.SUB_GROUP_NAME;
import static com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab.tableClassificationSubGroups;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCaseProfileCheckAutomaticSubGroup extends CaseProfileBaseTest {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-6491", component = POLICY_GROUPBENEFITS)
    public void testCheckAutomaticSubGroupForStdProduct() {
        openAppCreateNonIndCustomerInitNewCaseProfile();
        fillCaseProfile(GroupBenefitsMasterPolicyType.GB_DI_STD.getName());
        assertSubGroupsName();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-6491", component = POLICY_GROUPBENEFITS)
    public void testCheckAutomaticSubGroupForLtdProduct() {
        openAppCreateNonIndCustomerInitNewCaseProfile();
        fillCaseProfile(GroupBenefitsMasterPolicyType.GB_DI_LTD.getName());
        assertSubGroupsName();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-6491", component = POLICY_GROUPBENEFITS)
    public void testCheckAutomaticSubGroupForStProduct() {
        openAppCreateNonIndCustomerInitNewCaseProfile();
        fillCaseProfile(GroupBenefitsMasterPolicyType.GB_ST.getName());

        assertSoftly(softly -> {
            Arrays.asList("Stat NY", "Enhanced NY").forEach(coverageName -> {
                softly.assertThat(tableClassificationSubGroups).hasMatchingRows(1, ImmutableMap.of(COVERAGE.getName(), coverageName, SUB_GROUP_NAME.getName(), "Male"));
                softly.assertThat(tableClassificationSubGroups).hasMatchingRows(1, ImmutableMap.of(COVERAGE.getName(), coverageName, SUB_GROUP_NAME.getName(), "Female"));
                softly.assertThat(tableClassificationSubGroups).hasMatchingRows(1, ImmutableMap.of(COVERAGE.getName(), coverageName, SUB_GROUP_NAME.getName(), "Proprietor"));
            });
            softly.assertThat(tableClassificationSubGroups).hasMatchingRows(1, ImmutableMap.of(COVERAGE.getName(), "PFL NY", SUB_GROUP_NAME.getName(), "Male"));
            softly.assertThat(tableClassificationSubGroups).hasMatchingRows(1, ImmutableMap.of(COVERAGE.getName(), "PFL NY", SUB_GROUP_NAME.getName(), "Female"));
            softly.assertThat(tableClassificationSubGroups).hasMatchingRows(1, ImmutableMap.of(COVERAGE.getName(), "PFL NY", SUB_GROUP_NAME.getName(), "Other/Not Specified"));
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-11004", component = POLICY_GROUPBENEFITS)
    public void testCheckAutomaticSubGroupForDnProduct() {
        openAppCreateNonIndCustomerInitNewCaseProfile();
        fillCaseProfile(GroupBenefitsMasterPolicyType.GB_DN.getName());
        assertSoftly(softly -> {
            softly.assertThat(tableClassificationSubGroups).hasRows(8);
            tableClassificationSubGroups.getRows().forEach(
                    row -> softly.assertThat(row.getCell(SUB_GROUP_NAME.getName())).hasValue(String.format("Area %d", row.getIndex())));
        });
    }

    private void assertSubGroupsName() {
        assertSoftly(softly -> Arrays.asList("65-69", "70+").forEach(groupName -> {
            tableClassificationSubGroups.searchForRow(ImmutableMap.of(SUB_GROUP_NAME.getName(), groupName));
            softly.assertThat(tableClassificationSubGroups).hasMatchingRows(1, ImmutableMap.of(SUB_GROUP_NAME.getName(), groupName));
        }));
    }

    private void fillCaseProfile(String productName) {
        caseProfile.getDefaultWorkspace().fillUpTo(tdCaseProfile.getTestData("CaseProfile", "TestData_PolicyCreationWithOnePlan").resolveLinks()
                        .adjust(TestData.makeKeyPath("ProductAndPlanManagementTab", "Product"), productName),
                ClassificationManagementTab.class);
        caseProfile.getDefaultWorkspace().getTab(ClassificationManagementTab.class).getAssetList().fill(
                tdCaseProfile.getTestData("CaseProfile", "TestData_PolicyCreationWithOnePlan")
                        .adjust(TestData.makeKeyPath("ClassificationManagementTab"), tdCaseProfile.getTestData("CaseProfile", "ClassificationManagementSubGroupsAuto")));
    }
}