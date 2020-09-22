package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.table.Cell;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimSourceField extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-21265", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCreationCertificate() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("Steps 1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY));
        ComboBox comboBoxSource = intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SOURCE);
        assertSoftly(softly -> {
            softly.assertThat(comboBoxSource.getValue()).isEmpty();
            LOGGER.info("Step 2");
            softly.assertThat(comboBoxSource).hasOptions("","Manual Entry","ECS - Tesia","OCR - Tesia","ECS - CHC");
        });

        LOGGER.info("Step 3");
        comboBoxSource.setValue("ECS - Tesia");
        IntakeInformationTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        Cell sourceCell = ClaimSummaryPage.tableClaimData.getRow(1).getCell(TableConstants.ClaimSummaryClaimDataTableExtended.SOURCE.getName());
        assertThat(sourceCell).hasValue("");

        LOGGER.info("Step 4");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), IntakeInformationTabMetaData.SOURCE.getLabel()), "OCR - Tesia"));
        Tab.buttonSaveAndExit.click();
        assertThat(sourceCell).hasValue("OCR - Tesia");

        LOGGER.info("Step 5");
        dentalClaim.claimSubmit().perform();
        assertThat(sourceCell).hasValue("OCR - Tesia");
    }

}
