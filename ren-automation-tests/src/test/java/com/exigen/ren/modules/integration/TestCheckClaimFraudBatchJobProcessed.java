package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.general.scheduler.JobContext;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.helpers.file.entity.ClaimFraudLine;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.helpers.file.ClaimFraudFileHelper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.ALLOWED;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.DENIED;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.COVERED_CDT_CODE;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.DECISION;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CHARGE;
import static com.exigen.ren.main.modules.mywork.metadata.CompleteTaskActionTabMetaData.SubmittedServices.CDT_CODE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.INTEGRATION;

public class TestCheckClaimFraudBatchJobProcessed extends ClaimGroupBenefitsDNBaseTest implements JobContext {

    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = {"REN-30352", "REN-26416"}, component = INTEGRATION)
    public void testVerifyClaimFraudBatchJobFile2() throws JSchException, SftpException {
        Map<String, String> codes = new HashMap<>();
        codes.put(ALLOWED, "PD");
        codes.put(DENIED, "DN");

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();

        LOGGER.info("TEST REN-30352: TC2 Step 1");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_TwoServices")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[0]", CDT_CODE.getLabel()), ALLOWED)
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[1]", CDT_CODE.getLabel()), DENIED)
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[0]", CHARGE.getLabel()), "65")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[1]", CHARGE.getLabel()), "95"));

        dentalClaim.claimSubmit().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        assertSoftly(softly -> {
            softly.assertThat(tableResultsOfAdjudication.getRow(COVERED_CDT_CODE.getName(), ALLOWED)).hasCellWithValue(DECISION.getName(), "Allowed");
            softly.assertThat(tableResultsOfAdjudication.getRow(COVERED_CDT_CODE.getName(), DENIED)).hasCellWithValue(DECISION.getName(), "Denied");
        });

        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.PAID);

        mainApp().close();
        FileHelper.clearFolder(ClaimFraudFileHelper.OUTPUT_FOLDER);
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1).withDayOfMonth(1));
        JobRunner.executeJob(GeneralSchedulerPage.CLAIM_FRUAD_RED_FLAG_JOB);

        LOGGER.info("TEST REN-30352, REN-26416: TC2 Step 2");
        List<ClaimFraudLine> lines = ClaimFraudFileHelper.getLinesInFile();
        for (Map.Entry<String, String> param : codes.entrySet()) {
            Optional<ClaimFraudLine> lineOptional = lines.stream().filter(line -> (line.getCdtCode().equals(param.getKey())) && line.getClaimNumber().equals(claimNumber)).findFirst();
            assertSoftly(softly -> {
                softly.assertThat(lineOptional).isPresent();
                softly.assertThat(lineOptional.get().getLineStatus()).isEqualTo(param.getValue());
            });
        }
    }

    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-30352", component = INTEGRATION)
    public void testVerifyClaimFraudBatchJobFile3() throws JSchException, SftpException {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();

        LOGGER.info("TEST REN-30352: TC3 Step 1");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithoutPayment")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), "D0130")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CHARGE.getLabel()), "95"));

        dentalClaim.claimSubmit().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        assertThat(tableResultsOfAdjudication.getRow(COVERED_CDT_CODE.getName(), "D0130")).hasCellWithValue(DECISION.getName(), "Disallowed");

        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.PAID);

        mainApp().close();
        FileHelper.clearFolder(ClaimFraudFileHelper.OUTPUT_FOLDER);
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1).withDayOfMonth(1));
        JobRunner.executeJob(GeneralSchedulerPage.CLAIM_FRUAD_RED_FLAG_JOB);

        LOGGER.info("TEST REN-30352: TC3 Step 2");
        List<ClaimFraudLine> lines = ClaimFraudFileHelper.getLinesInFile();

        Optional<ClaimFraudLine> lineOptional = lines.stream().filter(line -> (line.getCdtCode().equals("D0130")) && line.getClaimNumber().equals(claimNumber)).findFirst();
        assertSoftly(softly -> {
            softly.assertThat(lineOptional).isPresent();
            softly.assertThat(lineOptional.get().getLineStatus()).isEqualTo("DA");
        });
    }
}