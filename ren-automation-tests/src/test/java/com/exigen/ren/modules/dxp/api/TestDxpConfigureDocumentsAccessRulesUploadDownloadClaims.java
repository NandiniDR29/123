package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_ac.AccidentHealthClaimACContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.UUIDModel;
import com.exigen.ren.rest.platform.efolder.model.DocumentModel;
import com.exigen.ren.rest.platform.efolder.model.FoldersDocumentModel;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpConfigureDocumentsAccessRulesUploadDownloadClaims extends RestBaseTest implements CustomerContext, CaseProfileContext,
        GroupAccidentMasterPolicyContext, AccidentHealthClaimACContext {

    private static String UPLOADED_FILE_NAME = "File.txt";

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-40302"}, component = CUSTOMER_REST)
    public void testDxpEmployerConfigureDocumentsAccessRulesUploadDownloadClaims() {
        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentClaimForMasterPolicy();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        ResponseContainer<List<FoldersDocumentModel>> foldersResponse = dxpRestService.getEmployerFolders(customerNumberAuthorize, "Claim");
        assertThat(foldersResponse.getResponse().getStatus()).isEqualTo(200);
        assertThat(foldersResponse.getModel()).isNotEmpty();
        FoldersDocumentModel model = foldersResponse.getModel().stream().filter(foldersModel -> foldersModel.getFolderName().equals("Unfiled Document")).collect(Collectors.toList()).get(0);
        assertThat(model.getFolderId()).isNotNull().isNotEmpty();
        String unfiledDocumentFolderId = model.getFolderId();

        LOGGER.info("REN-40302: Step 1");
        ResponseContainer<UUIDModel> uuidEmployerResponse = dxpRestService.postEmployerFolders(customerNumberAuthorize, claimNumber,
                "Claim", unfiledDocumentFolderId, UPLOADED_FILE_NAME, "MISC_CLAIM", null, null);
        assertThat(uuidEmployerResponse.getResponse().getStatus()).isEqualTo(200);
        String documentId = uuidEmployerResponse.getModel().getUuid();
        assertThat(documentId).isNotNull().isNotEmpty();

        LOGGER.info("REN-40302: Step 2");
        ResponseContainer<List<FoldersDocumentModel>> employerFoldersResponse = dxpRestService.getEmployerFoldersRefNo(customerNumberAuthorize, "Claim", claimNumber);
        assertThat(employerFoldersResponse.getResponse().getStatus()).isEqualTo(200);
        FoldersDocumentModel unfiledDocumentEntity = employerFoldersResponse.getModel().stream().filter(foldersModel -> foldersModel.getFolderName().equals("Unfiled Document")).collect(Collectors.toList()).get(0);
        assertThat(unfiledDocumentEntity.getDocuments()).isNotEmpty();
        DocumentModel firstDocumentForUnfiledDocumentFolder = unfiledDocumentEntity.getDocuments().get(0);
        assertSoftly(softly -> {
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getDocumentId()).isEqualTo(documentId);
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getFileName()).isEqualTo(UPLOADED_FILE_NAME);
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getCreationDate()).isNotNull().isNotEmpty();
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getFileSize()).isNotNull().isNotEmpty();
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getContentType()).isNotNull().isNotEmpty();
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getAttachmentTypeCd()).isNotNull().isNotEmpty();
        });

        LOGGER.info("REN-40302: Step 3");
        Response response = dxpRestService.getEmployerDocumentByUUID(customerNumberAuthorize, documentId);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-40504"}, component = CUSTOMER_REST)
    public void testDxpMemberConfigureDocumentsAccessRulesUploadDownloadClaims() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentClaimForMasterPolicy();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        ResponseContainer<List<FoldersDocumentModel>> foldersResponse = dxpRestService.getMemberFolders(customerNumber, "Claim");
        assertThat(foldersResponse.getResponse().getStatus()).isEqualTo(200);
        assertThat(foldersResponse.getModel()).isNotEmpty();
        FoldersDocumentModel model = foldersResponse.getModel().stream().filter(foldersModel -> foldersModel.getFolderName().equals("Unfiled Document")).collect(Collectors.toList()).get(0);
        assertThat(model.getFolderId()).isNotNull().isNotEmpty();
        String unfiledDocumentFolderId = model.getFolderId();

        LOGGER.info("REN-40504: Step 1");
        ResponseContainer<UUIDModel> uuidMemberResponse = dxpRestService.postMemberFolders(customerNumber, claimNumber,
                "Claim", unfiledDocumentFolderId, UPLOADED_FILE_NAME, "MISC_CLAIM", null, null);
        assertThat(uuidMemberResponse.getResponse().getStatus()).isEqualTo(200);
        String documentId = uuidMemberResponse.getModel().getUuid();
        assertThat(documentId).isNotNull().isNotEmpty();

        LOGGER.info("REN-40504: Step 2");
        ResponseContainer<List<FoldersDocumentModel>> memberFoldersResponse = dxpRestService.getMemberFoldersRefNo(customerNumber, "Claim", claimNumber);
        assertThat(memberFoldersResponse.getResponse().getStatus()).isEqualTo(200);
        FoldersDocumentModel unfiledDocumentEntity = memberFoldersResponse.getModel().stream().filter(foldersModel -> foldersModel.getFolderName().equals("Unfiled Document")).collect(Collectors.toList()).get(0);
        assertThat(unfiledDocumentEntity.getDocuments()).isNotEmpty();
        DocumentModel firstDocumentForUnfiledDocumentFolder = unfiledDocumentEntity.getDocuments().get(0);
        assertSoftly(softly -> {
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getDocumentId()).isEqualTo(documentId);
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getFileName()).isEqualTo(UPLOADED_FILE_NAME);
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getCreationDate()).isNotNull().isNotEmpty();
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getFileSize()).isNotNull().isNotEmpty();
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getContentType()).isNotNull().isNotEmpty();
            softly.assertThat(firstDocumentForUnfiledDocumentFolder.getAttachmentTypeCd()).isNotNull().isNotEmpty();
        });

        LOGGER.info("REN-40504: Step 3");
        Response response = dxpRestService.getMemberDocumentByUUID(customerNumber, documentId);
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
