/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.mywork.metadata;

import com.exigen.ipb.eisa.controls.AutoCompleteBox;
import com.exigen.ipb.eisa.controls.RichTextBox;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.modules.mywork.tabs.CompleteTaskActionTab;
import org.openqa.selenium.By;

public class CompleteTaskActionTabMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> COMPLETION_REASON = declare("Completion Reason", ComboBox.class);
    public static final AssetDescriptor<ComboBox> DECISION = declare("Decision", ComboBox.class);
    public static final AssetDescriptor<ComboBox> CHOICE = declare("Choice", ComboBox.class);
    public static final AssetDescriptor<ComboBox> USER_ACTIONS = declare("User Actions", ComboBox.class);
    public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class);

    public static final AssetDescriptor<AutoCompleteBox> PATIENT = declare("Patient", AutoCompleteBox.class, By.id("taskCompleteForm:patient_input"));
    public static final AssetDescriptor<ComboBox> SOURCE = declare("Source", ComboBox.class);
    public static final AssetDescriptor<AutoCompleteBox> PROVIDER = declare("Provider", AutoCompleteBox.class, By.id("taskCompleteForm:provider_input"));
    public static final AssetDescriptor<TextBox> RECEIVED_DATE = declare("Received Date", TextBox.class);
    public static final AssetDescriptor<TextBox> INVOICE_CURRENCY = declare("Invoice Currency", TextBox.class);
    public static final AssetDescriptor<CompleteTaskActionTab.SubmittedServicesRepeatAssetList> SUBMITTED_SERVICES =
            declare("Submitted Services", CompleteTaskActionTab.SubmittedServicesRepeatAssetList.class, SubmittedServices.class);
    public static final AssetDescriptor<CompleteTaskActionTab.InvoiceRepeatAssetList> INVOICE_REPEAT_ASSET_LIST_ASSET = declare("Invoices", CompleteTaskActionTab.InvoiceRepeatAssetList.class, InvoiceMetaData.class);

    public static final class SubmittedServices extends  MetaData {
        public static final AssetDescriptor<TextBox> DOS = declare("DOS", TextBox.class, Waiters.SLEEP, By.xpath(".//input[contains(@id, 'dosInputDate')]"));
        public static final AssetDescriptor<AutoCompleteBox> CDT_CODE = declare("CDT Code", AutoCompleteBox.class, By.xpath(".//input[contains(@id, 'serviceCodeInput_input')]"));
        public static final AssetDescriptor<AutoCompleteBox> PROCEDURE = declare("Procedure", AutoCompleteBox.class, By.xpath(".//input[contains(@id, 'procedure') and not(contains(@type, 'hidden'))]"));
        public static final AssetDescriptor<AutoCompleteBox> DIAGNOSIS = declare("Diagnosis", AutoCompleteBox.class, By.xpath(".//input[contains(@id, 'diagnosisInput') and not(contains(@type, 'hidden'))]"));
        public static final AssetDescriptor<TextBox> CHARGE = declare("Charge", TextBox.class, By.xpath(".//input[contains(@id, 'charge')]"));
    }

    public static final class InvoiceMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> TYPE_OF_TRANSACTION = declare("Type of Transaction", ComboBox.class, Waiters.DEFAULT, true);
        public static final AssetDescriptor<ComboBox> PAYEE_TYPE = declare("Payee Type", ComboBox.class);
        public static final AssetDescriptor<AutoCompleteBox> PROVIDER = declare("Provider", AutoCompleteBox.class, By.xpath(".//input[contains(@id, 'provider_input')]"));
        public static final AssetDescriptor<TextBox> INVOICE_CURRENCY = declare("Invoice Currency", TextBox.class);
        public static final AssetDescriptor<Button> ADD_SERVICE = declare("Add Service", Button.class, Waiters.SLEEP(20000),
                By.xpath(".//div[contains(@id, 'taskCompleteForm') and not(@style='display:none') and (@class='ui-panel-content ui-widget-content')]//button[contains(@id, 'taskCompleteForm') and contains(@id, 'addService')]"));
        public static final AssetDescriptor<AutoCompleteBox> PROCEDURE = declare("Procedure", AutoCompleteBox.class, By.xpath(".//input[contains(@id, 'procedure') and not(contains(@type, 'hidden'))]"));
        public static final AssetDescriptor<AutoCompleteBox> DIAGNOSIS = declare("Diagnosis", AutoCompleteBox.class, By.xpath(".//input[contains(@id, 'diagnosisInput') and not(contains(@type, 'hidden'))]"));
        public static final AssetDescriptor<TextBox> CHARGE = declare("Charge", TextBox.class, By.xpath(".//input[contains(@id, 'charge')]"));
    }
}
