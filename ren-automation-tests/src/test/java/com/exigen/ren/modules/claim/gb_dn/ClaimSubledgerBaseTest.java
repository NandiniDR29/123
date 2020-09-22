package com.exigen.ren.modules.claim.gb_dn;

import com.exigen.istf.data.TestData;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.claim.ClaimRestContext;

import java.util.*;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.istf.verification.CustomAssertions.assertThat;

public class ClaimSubledgerBaseTest extends ClaimGroupBenefitsDNBaseTest implements ClaimRestContext {

    protected void validatePayment(String transactionTypeId, String transactionGroupId, String txType, String entryType,
                                 String ledgerAccountNo, String transactionType, String paymentNumber, List<Map<String, String>> financeReport) {
        Map<String, String> record = getRecordInFinanceReportByTransactionTypeId(financeReport, transactionTypeId, paymentNumber);

        assertSoftly(softly -> {
            softly.assertThat(record.get("transactionGroupId")).isEqualTo(transactionGroupId);
            softly.assertThat(record.get("txType")).isEqualTo(txType);
            softly.assertThat(record.get("entryType")).isEqualTo(entryType);
            softly.assertThat(record.get("ledgerAccountNo")).isEqualTo(ledgerAccountNo);
            softly.assertThat(record.get("transactionType")).isEqualTo(transactionType);
        });
    }

    protected Map<String, String> getRecordInFinanceReportByTransactionTypeId(List<Map<String, String>> financeReport,
                                                                            String transactionTypeId, String paymentNumber) {
        for (Map<String, String> row : financeReport) {
            if (row.get("paymentNumber").equals(paymentNumber) && row.get("transactionTypeId").equals(transactionTypeId)) {
                return row;
            }
        }
        return null;
    }

    protected List<Map<String, String>> parseResponse(List<String> response) {
        List<Map<String, String>> listFinanceReport = new ArrayList<>();
        String[] keys = getKeys(response);
        for (int index = 1; index < response.size(); index++) {
            String[] values = getValues(index, response);
            listFinanceReport.add(getMapFinanceReport(keys, values));
        }
        return listFinanceReport;
    }

    protected Map<String, String> getMapFinanceReport(String[] keys, String[] values) {
        Map<String, String> mapFinanceReport = new HashMap<>();
        for (int index = 0; index < values.length; index++) {
            mapFinanceReport.put(keys[index], values[index]);
        }
        return mapFinanceReport;
    }

    protected String[] getKeys(List<String> response) {
        return response.get(0).split(",");
    }

    protected String[] getValues(int rowIndex, List<String> response) {
        return response.get(rowIndex).split(",");
    }

    protected void assertReportsEqual(List<Map<String, String>> reportsTD, List<Map<String, String>> reportsREST) {
        int reportsSize = reportsTD.size();
        assertThat(reportsSize).isEqualTo(reportsREST.size());
        for (int index = 0; index < reportsSize; index++) {
            Map<String, String> reportTD = reportsTD.get(index);
            for (Map.Entry<String, String> reportTDEntry : reportTD.entrySet()) {
                LOGGER.info(String.format("Comparing by key \"%s\" TD value \"%s\" with REST value \"%s\"", reportTDEntry.getKey(), reportTDEntry.getValue(), reportsREST.get(index).get(reportTDEntry.getKey())));
                assertThat(reportTDEntry.getValue()).isEqualTo(reportsREST.get(index).get(reportTDEntry.getKey()));
            }
        }
    }

    protected List<Map<String, String>> getFinReportsFromTestData(List<String> testDataEntries) {
        return getFinReportsFromTestData(testDataEntries.toArray(new String[testDataEntries.size()]));
    }

    protected List<Map<String, String>> getFinReportsFromTestData(String... testDataEntries) {
        List<Map<String, String>> financeReports = new ArrayList<>();
        Arrays.stream(testDataEntries).forEach(td -> financeReports.add(getFinReportFromTestData(tdSpecific().getTestData(td))));
        return financeReports;
    }

    protected Map<String, String> getFinReportFromTestData(TestData td) {
        Map<String, String> financeReport = new HashMap<>();
        td.getKeys().forEach(key -> financeReport.put(key, td.getValue(key)));
        return financeReport;
    }

}
