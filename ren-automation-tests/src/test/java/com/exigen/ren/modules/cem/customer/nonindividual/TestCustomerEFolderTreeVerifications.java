package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.EfolderConstants;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.module.efolder.defaulttabs.AddFileTab;
import com.exigen.ren.common.module.efolder.metadata.AddFileTabMetaData;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustomer.PORTAL_XML_UPLOAD;
import static com.exigen.ren.common.module.efolder.EfolderContext.efolder;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerEFolderTreeVerifications extends BaseTest implements CustomerContext {
    @Test(groups = {CEM, CUSTOMER_NONIND, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-45939", component = CRM_CUSTOMER)
    public void testVerificationOfEnrollmentFilePopUp() {
        LOGGER.info("General preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();

        LOGGER.info("TC#1 Steps# 1-3 verification");
        assertThat(Efolder.getLabel(PORTAL_XML_UPLOAD.getName())).isPresent();

        LOGGER.info("TC#2 Steps#1, 2 verification");
        Efolder.executeContextMenu(PORTAL_XML_UPLOAD.getName(), EfolderConstants.DocumentOparetions.ADD_DOCUMENT);
        assertThat(AddFileTab.comboBoxType).hasOptions("Enrollment File");
        Page.dialogConfirmation.buttonCancel.click();

        efolder.addDocument(tdSpecific().getTestData("TestData_PortalFileUpload"), PORTAL_XML_UPLOAD.getName());
        String xlsxFile = tdSpecific().getTestData("TestData_PortalFileUpload", AddFileTab.class.getSimpleName()).getValue(AddFileTabMetaData.NAME.getLabel());
        assertThat(Efolder.isDocumentExist(PORTAL_XML_UPLOAD.getName(), xlsxFile)).isTrue();

        efolder.addDocument(tdSpecific().getTestData("TestData_XML"), PORTAL_XML_UPLOAD.getName());
        String xmlFile = tdSpecific().getTestData("TestData_XML", AddFileTab.class.getSimpleName()).getValue(AddFileTabMetaData.NAME.getLabel());
        assertThat(Efolder.isDocumentExist(PORTAL_XML_UPLOAD.getName(), xmlFile)).isTrue();
    }
}
