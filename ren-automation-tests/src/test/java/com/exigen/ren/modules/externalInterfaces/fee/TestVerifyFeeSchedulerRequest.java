package com.exigen.ren.modules.externalInterfaces.fee;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.integration.model.IntegrationFindFeeScheduleFeeBodyModel;
import com.exigen.ren.rest.integration.model.IntegrationFindFeeScheduleFeeModel;
import com.exigen.ren.rest.integration.model.IntegrationFindUcrFeeBodyModel;
import org.testng.annotations.Test;

import java.util.Map;

import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestVerifyFeeSchedulerRequest extends RestBaseTest {

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-23012", "REN-23014"}, component = INTEGRATION)
    public void testVerifyFeeSchedulerRequest() {
        Map<String, String> dbRow = DBService.get().getRow("SELECT * FROM dbo.FEE_TABLE WHERE [START_DATE] = (SELECT max([START_DATE])\n" +
                "FROM dbo.FEE_TABLE WHERE FEE_TABLE_ID = 365)\n" +
                "and PROCEDURE_CODE = 'D2331'");

        IntegrationFindFeeScheduleFeeBodyModel model = new IntegrationFindFeeScheduleFeeBodyModel();
        model.setFeeTableId(dbRow.get("FEE_TABLE_ID"));
        model.setProcedureCode(dbRow.get("PROCEDURE_CODE"));
        model.setServiceDate(dbRow.get("START_DATE"));
        ResponseContainer<IntegrationFindFeeScheduleFeeModel> response = integrationRestService.postFindFeeScheduleFee(model);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        IntegrationFindFeeScheduleFeeModel modelResponse = response.getModel();
        assertThat(modelResponse.getFeeTableId()).isEqualTo(dbRow.get("FEE_TABLE_ID"));
        assertThat(modelResponse.getProcedureCode()).isEqualTo(dbRow.get("PROCEDURE_CODE"));
        assertThat(modelResponse.getServiceDate()).isEqualTo(dbRow.get("START_DATE"));
        assertThat(modelResponse.getAmount()).isEqualTo(dbRow.get("AMOUNT"));

        LOGGER.info("REN-23014 TC1");
        assertThat(modelResponse.getNoMaxAmount()).isEqualTo("false");
    }


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23014", component = INTEGRATION)
    public void testVerifyNoAmountValueTC2() {
        Map<String, String> dbRow = DBService.get().getRow("SELECT NAME, FEE_TABLE_ID,START_DATE, PROCEDURE_CODE,AMOUNT, NO_MAX_AMT_IND\n" +
                "FROM dbo.FEE_TABLE\n" +
                "WHERE PROCEDURE_CODE = 'D3999'\n" +
                "and FEE_TABLE_ID = 327\n" +
                "and NO_MAX_AMT_IND = 1");

        IntegrationFindFeeScheduleFeeBodyModel model = new IntegrationFindFeeScheduleFeeBodyModel();
        model.setFeeTableId(dbRow.get("FEE_TABLE_ID"));
        model.setProcedureCode(dbRow.get("PROCEDURE_CODE"));
        model.setServiceDate(dbRow.get("START_DATE"));
        assertThat(dbRow.get("NO_MAX_AMT_IND")).isEqualTo("1");

        ResponseContainer<IntegrationFindFeeScheduleFeeModel> response = integrationRestService.postFindFeeScheduleFee(model);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        IntegrationFindFeeScheduleFeeModel modelResponse = response.getModel();
        assertThat(modelResponse.getFeeTableId()).isEqualTo(dbRow.get("FEE_TABLE_ID"));
        assertThat(modelResponse.getProcedureCode()).isEqualTo(dbRow.get("PROCEDURE_CODE"));
        assertThat(modelResponse.getServiceDate()).isEqualTo(dbRow.get("START_DATE"));
        assertThat(modelResponse.getAmount()).isEqualTo("0");
        assertThat(modelResponse.getNoMaxAmount()).isEqualTo("true");
    }


    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23459", component = INTEGRATION)
    public void testVerifyTheResponseForUCRRequest() {

        commonStepsREN23459("80", "142", "D9945");
        commonStepsREN23459("70", "018", "D9946");
        commonStepsREN23459("90", "549", "D1526");
        commonStepsREN23459("50", "624", "D7311");
    }

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23460", component = INTEGRATION)
    public void testVerifyTheNoMaxAmountUCR() {
        Map<String, String> dbRow = DBService.get().getRow("SELECT * FROM dbo.FEE_TABLE\n" +
                "where START_DATE= '2008-01-01' and PROCEDURE_CODE='D2140' and FEE_ID='2100016' and NAME like 'R&C 80th%Region%024'");

        IntegrationFindUcrFeeBodyModel model = new IntegrationFindUcrFeeBodyModel();
        model.setPercentile("80");
        model.setRegion("024");
        model.setProcedureCode(dbRow.get("PROCEDURE_CODE"));
        model.setServiceDate(dbRow.get("START_DATE"));
        assertThat(dbRow.get("NO_MAX_AMT_IND")).isEqualTo("0");

        ResponseContainer<IntegrationFindFeeScheduleFeeModel> response = integrationRestService.postFindURCFee(model);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        IntegrationFindFeeScheduleFeeModel modelResponse = response.getModel();
        assertThat(modelResponse.getFeeTableId()).isEqualTo(dbRow.get("FEE_TABLE_ID"));
        assertThat(modelResponse.getProcedureCode()).isEqualTo(dbRow.get("PROCEDURE_CODE"));
        assertThat(modelResponse.getServiceDate()).isEqualTo(dbRow.get("START_DATE"));
        assertThat(modelResponse.getNoMaxAmount()).isEqualTo("false");
    }


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23014", component = INTEGRATION)
    public void testVerifyTheNoMaxAmountUCRTC2() {
        Map<String, String> dbRow = DBService.get().getRow("SELECT *\n" +
                "FROM dbo.FEE_TABLE\n" +
                "WHERE PROCEDURE_CODE = 'D8693'\n" +
                "and NO_MAX_AMT_IND = '1'" +
                "and NAME like 'R&C 80th%'");

        IntegrationFindUcrFeeBodyModel model = new IntegrationFindUcrFeeBodyModel();
        model.setPercentile("80");
        model.setRegion("039");
        model.setProcedureCode(dbRow.get("PROCEDURE_CODE"));
        model.setServiceDate(dbRow.get("START_DATE"));
        assertThat(dbRow.get("NO_MAX_AMT_IND")).isEqualTo("1");

        ResponseContainer<IntegrationFindFeeScheduleFeeModel> response = integrationRestService.postFindURCFee(model);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        IntegrationFindFeeScheduleFeeModel modelResponse = response.getModel();
        assertThat(modelResponse.getProcedureCode()).isEqualTo(dbRow.get("PROCEDURE_CODE"));
        assertThat(modelResponse.getServiceDate()).isEqualTo(dbRow.get("START_DATE"));
        assertThat(modelResponse.getNoMaxAmount()).isEqualTo("true");
    }


    private void commonStepsREN23459(String percentile, String region, String procedureCode) {
        IntegrationFindUcrFeeBodyModel model = new IntegrationFindUcrFeeBodyModel();
        model.setPercentile(percentile);
        model.setRegion(region);
        model.setServiceDate("2019-01-01");
        model.setProcedureCode(procedureCode);

        ResponseContainer<IntegrationFindFeeScheduleFeeModel> response = integrationRestService.postFindURCFee(model);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        IntegrationFindFeeScheduleFeeModel modelResponse = response.getModel();

        Map<String, String> dbRow = DBService.get().getRow(String.format("SELECT NAME, FEE_TABLE_ID,START_DATE, PROCEDURE_CODE,AMOUNT, NO_MAX_AMT_IND\n" +
                "FROM dbo.FEE_TABLE\n" +
                "WHERE PROCEDURE_CODE = '%s'\n" +
                "and FEE_TABLE_ID = '%s'\n", modelResponse.getProcedureCode(), modelResponse.getFeeTableId()));

        assertThat(dbRow).isNotEmpty().isNotNull();
        assertThat(modelResponse.getAmount()).isEqualTo(dbRow.get("AMOUNT"));
    }


}