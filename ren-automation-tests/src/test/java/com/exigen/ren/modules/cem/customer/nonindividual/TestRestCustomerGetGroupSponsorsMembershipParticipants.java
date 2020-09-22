/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.groupSponsors.model.GroupSponsorModel;
import com.exigen.ren.rest.customer.groupSponsors.model.ParticipationDetailsModel;
import org.testng.annotations.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.MEMBERSHIP_ID;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.MEMBER_ID;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetGroupSponsorsMembershipParticipants extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26298", component = CRM_CUSTOMER)
    public void testRestCustomerGetMembershipParticipants() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerNonIndividual.addParticipant().perform(tdCustomerNonIndividual.getTestData("AddParticipant", "TestData_Membership")
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), MEMBER_ID.getLabel()), "001")
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), MEMBERSHIP_ID.getLabel()), "003"));

        ResponseContainer<List<GroupSponsorModel>> actualResponse = customerRestClient.getMembershipParticipants(customerNumber);
        assertThat(actualResponse.getResponse().getStatus()).isEqualTo(200);
        ParticipationDetailsModel participationDetailsModel = actualResponse.getModel().get(0).getParticipationDetails();
        assertSoftly(softly -> {
            softly.assertThat(participationDetailsModel.getType()).isEqualTo("MEM");
            softly.assertThat(participationDetailsModel.getMemberId()).isEqualTo("001");
            softly.assertThat(participationDetailsModel.getMembershipId()).isEqualTo("003");
            softly.assertThat(participationDetailsModel.getMembershipStatus()).isEqualTo("ACT");
            softly.assertThat(participationDetailsModel.getMembershipStartDate()).isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().minusDays(1).format(YYYY_MM_DD));
        });
    }
}
