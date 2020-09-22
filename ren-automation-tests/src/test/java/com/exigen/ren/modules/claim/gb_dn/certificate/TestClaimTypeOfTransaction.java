package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.dental.processediclaim.ProcessEdiClaimResponseModel;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.TYPE_OF_TRANSACTION;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimTypeOfTransaction extends ClaimGroupBenefitsDNBaseTest implements ClaimRestContext {

    private static final String DOS_IS_REQUIRED_WHEN_TYPE_OF_TRANSACTION_ACTUAL_SERVICES = "'DOS' is required when Type of Transaction = 'Actual Services'.";
    private static final String DOS_MUST_BE_BLANK_WHEN_TYPE_OF_TRANSACTION_PREDETERMINATION = "'DOS' must be blank when Type of Transaction = 'Predetermination'.";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36940", component = CLAIMS_GROUPBENEFITS)
    public void testClaimTypeOfTransaction() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step 1");
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        TestData testDataClaim = dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_PardisRajabi_Provider");
        dentalClaim.initiateWithoutPolicy(testDataClaim);

        LOGGER.info("Steps 2,3");
        AbstractContainer<?, ?> intakeInformationTabAssetList = intakeInformationTab.getAssetList();
        assertThat(intakeInformationTabAssetList.getAsset(TYPE_OF_TRANSACTION)).isPresent().isEnabled().isRequired().hasValue("").hasOptions("", "Actual Services", "Predetermination");

        LOGGER.info("Step 4");
        intakeInformationTab.fillTab(testDataClaim.adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .mask(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel())));
        intakeInformationTabAssetList.getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue(REVIEW_REQUIRED_1);
        buttonSubmitClaim.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DOS_IS_REQUIRED_WHEN_TYPE_OF_TRANSACTION_ACTUAL_SERVICES)).exists();

        LOGGER.info("Step 5");
        Tab.buttonCancel.click();
        buttonAddService.click();
        intakeInformationTabAssetList.getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D3351");
        intakeInformationTabAssetList.getAsset(SUBMITTED_SERVICES).getAsset(TOOTH).setValue("3");
        intakeInformationTabAssetList.getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        buttonSubmitClaim.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DOS_IS_REQUIRED_WHEN_TYPE_OF_TRANSACTION_ACTUAL_SERVICES)).exists();

        LOGGER.info("Step 6");
        Tab.buttonCancel.click();
        intakeInformationTabAssetList.getAsset(TYPE_OF_TRANSACTION).setValue("Predetermination");
        buttonSubmitClaim.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DOS_MUST_BE_BLANK_WHEN_TYPE_OF_TRANSACTION_PREDETERMINATION)).exists();

        LOGGER.info("Step 7");
        Tab.buttonCancel.click();
        tableSubmittedServices.getRow(SubmittedServicesColumns.DOS.getName(), "").getCell(9).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        buttonSubmitClaim.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DOS_MUST_BE_BLANK_WHEN_TYPE_OF_TRANSACTION_PREDETERMINATION)).exists();

        LOGGER.info("Step 8");
        Tab.buttonCancel.click();
        intakeInformationTabAssetList.getAsset(TYPE_OF_TRANSACTION).setValue("Actual Services");
        buttonSubmitClaim.click();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);
            softly.assertThat(ClaimSummaryPage.tableClaimData.getRow(1).getCell(TableConstants.ClaimSummaryClaimDataTableExtended.TYPE_OF_TRANSACTION.getName())).hasValue("Actual Services");
            softly.assertThat(getClaimImageTransactionTypeREST(claimNumber)).isEqualTo("ACTUAL_SERVICES");
        });
        claim.claimInquiry().start();
        assertThat(new StaticElement(By.id("policyDataGatherForm:sedit_ClaimsDentalTreatmentDetails_claimTypeCd"))).hasValue("Actual Services");

        LOGGER.info("Step 9");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTabAssetList.getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue("");
        Tab.buttonSaveAndExit.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DOS_IS_REQUIRED_WHEN_TYPE_OF_TRANSACTION_ACTUAL_SERVICES)).exists();

        LOGGER.info("Step 13");
        String dos = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        assertSoftly(softly -> {
            ProcessEdiClaimResponseModel responseActualServicesWithDOS = createClaimREST("ACTUAL_SERVICES", dos, policyNumber, customerNumber);
            softly.assertThat(responseActualServicesWithDOS.getErrorMessages()).isEmpty();
            softly.assertThat(getClaimImageTransactionTypeREST(responseActualServicesWithDOS.getClaimNumber())).isEqualTo("ACTUAL_SERVICES");

            ProcessEdiClaimResponseModel responsePredeterminationWithoutDOS = createClaimREST("PREDETERMINATION", "", policyNumber, customerNumber);
            softly.assertThat(responsePredeterminationWithoutDOS.getErrorMessages()).isEmpty();
            softly.assertThat(getClaimImageTransactionTypeREST(responsePredeterminationWithoutDOS.getClaimNumber())).isEqualTo("PREDETERMINATION");

            LOGGER.info("Step 14");
            ProcessEdiClaimResponseModel responseActualServicesWithoutDOS = createClaimREST("ACTUAL_SERVICES", "", policyNumber, customerNumber);
            softly.assertThat(responseActualServicesWithoutDOS.getErrorMessages().get(0)).isEqualTo(DOS_IS_REQUIRED_WHEN_TYPE_OF_TRANSACTION_ACTUAL_SERVICES);
            softly.assertThat(getClaimImageTransactionTypeREST(responseActualServicesWithoutDOS.getClaimNumber())).isEqualTo("ACTUAL_SERVICES");

            ProcessEdiClaimResponseModel responsePredeterminationWithDOS = createClaimREST("PREDETERMINATION", dos, policyNumber, customerNumber);
            softly.assertThat(responsePredeterminationWithDOS.getErrorMessages().get(0)).isEqualTo(DOS_MUST_BE_BLANK_WHEN_TYPE_OF_TRANSACTION_PREDETERMINATION);
            softly.assertThat(getClaimImageTransactionTypeREST(responsePredeterminationWithDOS.getClaimNumber())).isEqualTo("PREDETERMINATION");
        });
    }

    private ProcessEdiClaimResponseModel createClaimREST(String transactionType, String dos, String policyNumber, String customerNumber) {
        String todayDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        return createOrUpdateClaim(claimDentalRestService.defaultTestData().getTestData("FNOL", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath("claim", "reportedDt"), todayDate)
                .adjust(TestData.makeKeyPath("claim", "lossDt"), todayDate)
                .adjust(TestData.makeKeyPath("claim", "extension", "claimData", "receivedDate"), todayDate)
                .adjust(TestData.makeKeyPath("claim", "extension", "claimData", "transactionType"), transactionType)
                .adjust(TestData.makeKeyPath("damages[0]", "loss", "extension", "procedureDate"), dos), policyNumber, customerNumber);
    }

    private String getClaimImageTransactionTypeREST(String claimNumber) {
        return claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getExtension().getClaimData().getTransactionType();
    }

}
