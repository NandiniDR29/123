package com.exigen.ren.modules.DXP;

import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.policy.EmployerMasterPoliciesDetailed;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.ren.main.enums.DXPConstants.EmployerMasterPolicies.CONFIG_DENTAL;
import static org.assertj.core.api.Assertions.assertThat;

public class DXPTEST extends RestBaseTest  {

        @Test()

        public void testDxpRest() {

           mainApp().open();
            String customerNum = "104878";

            String policyNumber = "MP0000023143";
            LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
//            LOGGER.info("---=={Step 1}==---");
//            ResponseContainer<EmployerMasterPoliciesDetailed> response = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_DENTAL,"", customerNum, policyNumber, null);
//            assertThat(response.getResponse().getStatus()).isEqualTo(200);
//            assertThat(response.getModel().getTransactionTypeCd()).isEqualTo("endorsement");
//            assertThat(response.getModel().getRevisionNumber()).isEqualTo("2");

            LOGGER.info("---=={Step 2}==---");
            ResponseContainer<EmployerMasterPoliciesDetailed> response2 = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_DENTAL,"", customerNum, policyNumber,
                    policyEffectiveDate.toLocalDate().toString());
            assertThat(response2.getResponse().getStatus()).isEqualTo(200);
            assertThat(response2.getModel().getTransactionTypeCd()).isEqualTo("policy");
            assertThat(response2.getModel().getRevisionNumber()).isEqualTo("1");

//            LOGGER.info("---=={Step 3}==---");
//            createDefaultNonIndividualCustomer();
//            String customerNumC2 = CustomerSummaryPage.labelCustomerNumber.getValue();
//            ResponseContainer<EmployerMasterPoliciesDetailed> response3 = dxpRestService.getEmployerMasterPoliciesDetailed(CONFIG_DENTAL,customerNumC2, customerNumC2, policyNumber, null);
//            assertThat(response3.getResponse().getStatus()).isEqualTo(403);
//            assertThat(response3.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
//            assertThat(response3.getModel().getMessage()).isEqualTo("User has no access to policy or certificate");
        }
}
