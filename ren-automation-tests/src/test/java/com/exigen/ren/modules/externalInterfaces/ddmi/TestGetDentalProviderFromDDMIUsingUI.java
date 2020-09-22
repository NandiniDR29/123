package com.exigen.ren.modules.externalInterfaces.ddmi;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER_WITHOUT_POLICY;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGetDentalProviderFromDDMIUsingUI extends BaseTest implements CustomerContext, DentalClaimContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38738", component = CLAIMS_GROUPBENEFITS)
    public void testGetDentalProviderFromDDMIUsingUI() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        dentalClaim.initiateWithoutPolicy(dentalClaim.getDefaultTestData(DATA_GATHER_WITHOUT_POLICY, TestDataKey.DEFAULT_TEST_DATA_KEY));
        intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SEARCH_PROVIDER)
                .fill(dentalClaim.getDefaultTestData(DATA_GATHER_WITHOUT_POLICY, "IntakeInformationTab")
                        .mask(TestData.makeKeyPath(IntakeInformationTabMetaData.SEARCH_PROVIDER.getLabel(), IntakeInformationTabMetaData.SearchProviderMetaData.ADD_VENDOR.getLabel())));

        assertThat(intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SEARCH_PROVIDER).getAsset(IntakeInformationTabMetaData.SearchProviderMetaData.ADD_VENDOR))
                .as("Provider was not found in DDMI service").isPresent();

        //added for sequential suite run
        IntakeInformationTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
    }
}
