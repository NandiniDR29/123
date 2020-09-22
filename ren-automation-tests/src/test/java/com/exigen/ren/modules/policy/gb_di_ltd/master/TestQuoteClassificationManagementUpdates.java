package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.TableConstants.CoverageRelationships.CLASS_NAME;
import static com.exigen.ren.main.enums.TableConstants.CoverageRelationships.CLASS_NUMBER;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteClassificationManagementUpdates extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private final StaticElement labelCustomizePlan = new StaticElement(By.xpath("//*[@id='policyDataGatherForm:componentContextHolder']//*[text()='Customize Plan?']"));

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-3700", "REN-6037"}, component = POLICY_GROUPBENEFITS)
    public void testQuoteClassificationManagementUpdates() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        TestData tdCaseProfile = caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithOnePlan");
        caseProfile.create(tdCaseProfile, longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());

        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()), ClassificationManagementTab.class);
        longTermDisabilityMasterPolicy.getDefaultWorkspace().getTab(ClassificationManagementTab.class).getAssetList().fill(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));

        assertSoftly(softly -> {

            // Assert for REN-6037/#2.1
            softly.assertThat(labelCustomizePlan).as("Label 'Customize Plan?' isn't hidden").isPresent(false);

            softly.assertThat(ClassificationManagementTab.tableCoverageRelationships.getHeader().getCell(2)).hasValue(CLASS_NUMBER.getName());
            softly.assertThat(ClassificationManagementTab.tableCoverageRelationships).hasMatchingRows(1,
                    ImmutableMap.of(
                            CLASS_NAME.getName(), tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY).getTestData(ClassificationManagementTab.class.getSimpleName()).getValue(ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME.getLabel()),
                            CLASS_NUMBER.getName(), tdCaseProfile.getTestDataList(ClassificationManagementTab.class.getSimpleName()).get(0).getTestData("Classification Group").getValue(CLASS_NUMBER.getName())));
        });
    }
}
