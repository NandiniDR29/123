/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.DXPConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.dxp.api.certificate.scenario.TestDxpEmployerCertificatePremiumSummaryBase;
import org.testng.annotations.Test;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpEmployerCertificatePremiumSummarySTD extends TestDxpEmployerCertificatePremiumSummaryBase implements CaseProfileContext,
        ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35621", component = CUSTOMER_REST)
    public void testDxpEmployerCertificatePremiumSummarySTD() {

        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        customerNumNIC1.set(customerNumberAuthorize);

        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        createDefaultShortTermDisabilityMasterPolicy();
        createDefaultShortTermDisabilityCertificatePolicy();

        policyNumCP1.set(PolicySummaryPage.labelPolicyNumber.getValue());
        policyEffectiveDate.set(PolicySummaryPage.getEffectiveDate());

        PolicySummaryPage.tablePremiumSummary.getRow(1).getCell(PolicySummaryPage.PremiumSummary.COVERAGE_NAME.getName()).controls.links.getFirst().click();
        NavigationPage.PolicyNavigation.leftMenu(PREMIUM_SUMMARY.get());

        Row row = PremiumSummaryTab.tablePremiumSummary.getRow(1);
        paymentMode.set(row.getCell(PremiumSummaryTab.PremiumSummary.PAYMENT_MODE.getName()).getValue());
        modalPremium.set(row.getCell(PremiumSummaryTab.PremiumSummary.MODAL_PREMIUM.getName()).getValue());
        annualPremium.set(row.getCell(PremiumSummaryTab.PremiumSummary.ANNUAL_PREMIUM.getName()).getValue());
        rate.set(row.getCell(PremiumSummaryTab.PremiumSummary.RATE.getName()).getValue());
        contributionType.set(row.getCell(PremiumSummaryTab.PremiumSummary.CONTRIBUTION_TYPE.getName()).getValue());
        Tab.buttonTopCancel.click();

        verifyResponse(DXPConstants.EmployerCertificatesPremium.CONFIG_STD, "STD");
    }
}
