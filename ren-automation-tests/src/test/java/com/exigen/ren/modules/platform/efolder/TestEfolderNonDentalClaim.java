package com.exigen.ren.modules.platform.efolder;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_tl.TermLifeClaimContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.EfolderConstants.*;
import static com.exigen.ren.common.enums.EfolderConstants.DocumentTypes.*;
import static com.exigen.ren.utils.components.Components.ACCOUNT_AND_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEfolderNonDentalClaim extends EfolderBaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext, TermLifeClaimContext {

    @Test(groups = {PLATFORM, PLATFORM_EFOLDER, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33041"}, component = ACCOUNT_AND_CUSTOMER)
    public void testStructureClaimsEFolderTreeNonDental() {

        LOGGER.info("REN-33041: Preconditions: Create Claim for Non-Dental product");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceCertificatePolicy();
        createDefaultTermLifeInsuranceClaimForCertificatePolicy();

        LOGGER.info("REN-33041 TC1a Steps 1-3");
        Efolder.linkOpenEFolder.click();
        assertSoftly(softly -> {
            Stream.of(EFolderNonDentalClaim.APPEALS_AND_INQUIRIES.getName(),
                    EFolderNonDentalClaim.CHECKS.getName(),
                    EFolderNonDentalClaim.CLAIMANT_NOTIFICATIONS.getName(),
                    EFolderNonDentalClaim.EOB.getName(),
                    EFolderNonDentalClaim.EMPLOYER_NOTIFICATIONS.getName(),
                    EFolderNonDentalClaim.GENERAL.getName(),
                    EFolderNonDentalClaim.OUTBOUND_CORRESPONDENCE.getName(),
                    EFolderNonDentalClaim.RETURNED_MAIL.getName(),
                    EFolderNonDentalClaim.SUPPORTING_CLAIM_DOCUMENTATION.getName(),
                    EFolderNonDentalClaim.UNFILED_DOCUMENT.getName()).forEach(
                    folderPath -> softly.assertThat(Efolder.isDocumentExist(folderPath)).isTrue());

            LOGGER.info("REN-33041 TC1a Step 4");
            Efolder.expandFolder(EFolderNonDentalClaim.OUTBOUND_CORRESPONDENCE.getName());
            softly.assertThat(Efolder.isDocumentExist(EFolderNonDentalClaimOutboundCorresp.RECEIPT_OF_CLAIM_LETTERS.getName())).isTrue();
        });

        LOGGER.info("REN-33041 TC2a Steps 3,4");
        Efolder.expandFolder(EFolderNonDentalClaim.APPEALS_AND_INQUIRIES.getName());
        assertAddRetrieveDocument("Appeals", EFolderNonDentalClaimAppealsInq.APPEALS.getName());

        LOGGER.info("REN-33041 TC2a Step 5");
        assertAddRetrieveDocument(DOI_INQUIRIES, EFolderNonDentalClaimAppealsInq.DOI_INQUIRIES.getName());

        LOGGER.info("REN-33041 TC2a Step 6");
        Stream.of(INFORMATION_REQUEST, "Attorney Request", "3rd Party Request", "Subpoena").forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimAppealsInq.INFORMATION_REQUEST.getName()));

        LOGGER.info("REN-33041 TC2a Step 7");
        assertAddRetrieveDocument("Requests for Reconsideration", EFolderNonDentalClaimAppealsInq.REQUESTS_FOR_RECONSIDERATION.getName());

        LOGGER.info("REN-33041 TC2a Step 8");
        Efolder.expandFolder(EFolderNonDentalClaim.CHECKS.getName());
        Stream.of("Refund Checks", "Overpayment Recovery").forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimChecks.REFUND_CHECKS.getName()));

        LOGGER.info("REN-33041 TC2a Step 9");
        assertAddRetrieveDocument("Returned Checks", EFolderNonDentalClaimChecks.RETURNED_CHECKS.getName());

        LOGGER.info("REN-33041 TC2a Step 10");
        Efolder.expandFolder(EFolderNonDentalClaim.CLAIMANT_NOTIFICATIONS.getName());
        assertAddRetrieveDocument("Claimant Request for Information", EFolderNonDentalClaimNotif.CLAIMANT_REQUEST_FOR_INFORMATION.getName());

        LOGGER.info("REN-33041 TC2a Step 11");
        Stream.of("Claimant Status Notice", ACKNOWLEDGEMENT, APPROVAL, DENIAL, CLOSURE).forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimNotif.CLAIMANT_STATUS_NOTICE.getName()));

        LOGGER.info("REN-33041 TC2a Step 12");
        assertAddRetrieveDocument(EOB, EFolderNonDentalClaim.EOB.getName());

        LOGGER.info("REN-33041 TC2a Step 13");
        Efolder.expandFolder(EFolderNonDentalClaim.EMPLOYER_NOTIFICATIONS.getName());
        assertAddRetrieveDocument("Employer Request for Information", EFolderNonDentalClaimEmplNotif.EMPLOYER_REQUEST_FOR_INFORMATION.getName());

        LOGGER.info("REN-33041 TC2a Step 14");
        Stream.of("Employer Status Notice", ACKNOWLEDGEMENT, APPROVAL, DENIAL, CLOSURE).forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimEmplNotif.EMPLOYER_STATUS_NOTICE.getName()));

        LOGGER.info("REN-33041 TC2a Step 15");
        Efolder.expandFolder(EFolderNonDentalClaim.GENERAL.getName());
        assertAddRetrieveDocument("Reinsurance", EFolderNonDentalClaimGeneral.REINSURANCE.getName());

        LOGGER.info("REN-33041 TC2a Step 16");
        Stream.of(MISCELLANEOUS_DOCUMENT, CLAIM_LETTER).forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaim.OUTBOUND_CORRESPONDENCE.getName()));
        LOGGER.info("REN-33041 TC2a Step 17");
        Stream.of(MISCELLANEOUS_DOCUMENT, CLAIM_LETTER).forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimOutboundCorresp.RECEIPT_OF_CLAIM_LETTERS.getName()));

        LOGGER.info("REN-33041 TC2a Step 18");
        assertAddRetrieveDocument("Returned Mail", EFolderNonDentalClaim.RETURNED_MAIL.getName());

        LOGGER.info("REN-33041 TC2a Step 19");
        Efolder.expandFolder(EFolderNonDentalClaim.SUPPORTING_CLAIM_DOCUMENTATION.getName());
        assertAddRetrieveDocument("Beneficiary Documentation", EFolderNonDentalClaimSuppClaimDoc.BENEFICIARY_DOCUMENTATION.getName());

        LOGGER.info("REN-33041 TC2a Step 20");
        assertAddRetrieveDocument(CLAIM_FORMS, EFolderNonDentalClaimSuppClaimDoc.CLAIM_FORMS.getName());

        LOGGER.info("REN-33041 TC2a Step 21");
        Stream.of("Death Records", "Death Certificate", "Autopsy", "Obituary", "Funeral Home Bill").forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimSuppClaimDoc.DEATH_RECORDS.getName()));

        LOGGER.info("REN-33041 TC2a Step 22");
        assertAddRetrieveDocument("Enrollment Documentation", EFolderNonDentalClaimSuppClaimDoc.ENROLLMENT_DOCUMENTATION.getName());

        LOGGER.info("REN-33041 TC2a Step 23");
        assertAddRetrieveDocument("Intermittent PFL Calendar", EFolderNonDentalClaimSuppClaimDoc.INTERMITTENT_PFL_CALENDAR.getName());

        LOGGER.info("REN-33041 TC2a Step 24");
        Stream.of("Investigation", "Coverage Verification", "Census", "Police Report").forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimSuppClaimDoc.INVESTIGATION.getName()));

        LOGGER.info("REN-33041 TC2a Step 25");
        assertAddRetrieveDocument("Medical Records", EFolderNonDentalClaimSuppClaimDoc.MEDICAL_RECORDS.getName());

        LOGGER.info("REN-33041 TC2a Step 26");
        Stream.of("Pay and Timecard Records", "Pay Stubd", "Timecard").forEach(
                documentType -> assertAddRetrieveDocument(documentType, EFolderNonDentalClaimSuppClaimDoc.PAY_AND_TIMECARD_RECORDS.getName()));

        LOGGER.info("REN-33041 TC2a Step 27");
        assertAddRetrieveDocument("Supplemental Documents", EFolderNonDentalClaimSuppClaimDoc.SUPPLEMENTAL_DOCUMENTS.getName());

        LOGGER.info("REN-33041 TC2a Step 28");
        assertAddRetrieveDocument("Unfiled Document", EFolderNonDentalClaim.UNFILED_DOCUMENT.getName());
    }
}
