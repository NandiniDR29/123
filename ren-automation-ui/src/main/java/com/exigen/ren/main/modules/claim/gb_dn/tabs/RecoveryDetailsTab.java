package com.exigen.ren.main.modules.claim.gb_dn.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData;
import org.openqa.selenium.By;

public class RecoveryDetailsTab extends DefaultTab {

    public static Button buttonUpdateRecovery = new Button(By.id("policyDataGatherForm:paymentActionLink_update"));
    public static Button buttonVoidRecovery = new Button(By.id("policyDataGatherForm:paymentActionLink_decline"));
    public static TableExtended<RecoveryDetails> tableRecoveryDetails = new TableExtended<>("Recovery Details", By.id("policyDataGatherForm:body_transactionInstanceTable_ClaimsRecoveryView"));

    public RecoveryDetailsTab() {
        super(RecoveryDetailsActionTabMetaData.class);
    }

    public enum RecoveryDetails implements Named {
        RECOVERY_NUMBER("Recovery #"),
        RECOVERY_AMOUNT("Recovery Amount"),
        RECOVERED_FROM("Recovered From"),
        RECOVERY_POST_DATE("Recovery Post Date"),
        RECOVERY_METHOD("Recovery Method"),
        CHECK("Check #"),
        BANK_ACCOUNT_NUMBER("Bank Account Number"),
        RECOVERY_MEMO("Recovery Memo"),
        CREATOR("Creator"),
        STATUS("Status");

        private String name;

        RecoveryDetails(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}