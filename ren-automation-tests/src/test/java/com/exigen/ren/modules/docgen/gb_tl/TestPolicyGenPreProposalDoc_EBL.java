package com.exigen.ren.modules.docgen.gb_tl;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.C1;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.C10;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCustomerName;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenPreProposalDoc_EBL extends ValidationXMLBaseTestTL implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {


    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-21295", component = POLICY_GROUPBENEFITS)
    public void testPolicyGenPreProposalDocEbl() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createQuote(tdSpecific().getTestData("TestData_CreateQuote"));
        String caseProfileNumber = labelCaseProfileNumber.getValue();
        String customerName = labelCustomerName.getValue();
        termLifeInsuranceMasterPolicy.preProposal().perform();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("Step 2");
        xmlValidator.checkDocument(C1, "true");

        LOGGER.info("Step 3");
        String value = xmlValidator.getNodeValue(C10);
        assertSoftly(softly -> {
            softly.assertThat(findDocNameValueFromFooterDocInfo(value)).isEqualTo(String.format("PRE-PROPOSAL-%d", TimeSetterUtil.getInstance().getCurrentTime().getYear()));
            softly.assertThat(findGroupNameValueFromFooterDocInfo(value)).isEqualTo(customerName);
            softly.assertThat(LocalDate.parse(findDateTimeValueFromFooterDocInfo(value), DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")).format(DateTimeUtils.MM_DD_YYYY))
                    .isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        });
    }

    public static String findDocNameValueFromFooterDocInfo(String footerDocInfoValue) {
        return findValueViaGroupNumber(footerDocInfoValue, 1);
    }

    public static String findGroupNameValueFromFooterDocInfo(String footerDocInfoValue) {
        return findValueViaGroupNumber(footerDocInfoValue, 2);
    }

    public static String findDateTimeValueFromFooterDocInfo(String footerDocInfoValue) {
        return findValueViaGroupNumber(footerDocInfoValue, 3);
    }

    private static String findValueViaGroupNumber(String text, int groupNumber) {
        Pattern pattern = Pattern.compile("(\\D+-\\d{4})-(.*)-(\\d{4}.*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(groupNumber);
        } else {
            throw new IstfException("Cannot find any match");
        }
    }
}
