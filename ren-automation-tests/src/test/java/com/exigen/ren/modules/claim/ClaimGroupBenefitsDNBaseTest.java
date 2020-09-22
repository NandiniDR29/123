/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.claim.GroupBenefitsClaimType;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.rest.claim.model.dental.processediclaim.ProcessEdiClaimResponseModel;

import java.util.List;

public class ClaimGroupBenefitsDNBaseTest extends ClaimGroupBenefitsBaseTest implements GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, DentalClaimContext {

    protected GroupBenefitsClaimType getClaimType() {
        return GroupBenefitsClaimType.CLAIM_DENTAL;
    }

    protected ProcessEdiClaimResponseModel createOrUpdateClaim(TestData testData, String policyNumber, String customerId) {
        return dentalClaimRest.processEdiClaim(prepareTestData(testData, policyNumber, customerId)).getModel();
    }

    protected TestData prepareTestData(TestData testData, String policyNumber, String customerId) {
        List<TestData> partiesTestDataList = testData.getTestDataList("claim", "parties");
        List<TestData> damagesTestDataList = testData.getTestDataList("damages");

        partiesTestDataList.forEach(party -> party.adjust("oid", customerId));
        damagesTestDataList.forEach(damage -> damage.adjust(TestData.makeKeyPath("loss", "associatedRiskItemOid"), customerId));

        return testData
                .adjust(TestData.makeKeyPath("claim", "policyNumber"), policyNumber)
                .adjust(TestData.makeKeyPath("claim", "parties"), partiesTestDataList)
                .adjust("damages", damagesTestDataList);
    }

    protected ProcessEdiClaimResponseModel createOrUpdateClaimTodayDateREST(TestData td, String policyNumber, String customerNumber) {
        String todayDateTime = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        return createOrUpdateClaim(td.adjust(TestData.makeKeyPath("claim", "reportedDt"), todayDateTime)
                .adjust(TestData.makeKeyPath("claim", "lossDt"), todayDateTime)
                .adjust(TestData.makeKeyPath("claim", "extension", "claimData", "receivedDate"), todayDateTime), policyNumber, customerNumber);
    }

    protected ProcessEdiClaimResponseModel createOrUpdateClaimWithoutPolicyTodayDateREST(TestData testData, String customerId) {
        String todayDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        List<TestData> partiesTestDataList = testData.getTestDataList("claim", "parties");
        List<TestData> damagesTestDataList = testData.getTestDataList("damages");
        partiesTestDataList.forEach(party -> party.adjust("oid", customerId));
        damagesTestDataList.forEach(damage -> damage.adjust(TestData.makeKeyPath("loss", "associatedRiskItemOid"), customerId));
        return dentalClaimRest.processEdiClaim(testData.adjust("damages", damagesTestDataList)
                .adjust(TestData.makeKeyPath("claim", "parties"), partiesTestDataList)
                .adjust(TestData.makeKeyPath("claim", "customerNumber"), customerId)
                .adjust(TestData.makeKeyPath("claim", "reportedDt"), todayDate)
                .adjust(TestData.makeKeyPath("claim", "lossDt"), todayDate)
                .adjust(TestData.makeKeyPath("claim", "extension", "claimData", "receivedDate"), todayDate)).getModel();
    }

}
