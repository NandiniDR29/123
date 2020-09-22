/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.caseprofile;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.*;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface CaseProfileContext {

    CaseProfile caseProfile = AutomationContext.getService(CaseProfile.class);
    CaseProfileDetailsTab caseProfileDetailsTab = caseProfile.getDefaultWorkspace().getTab(CaseProfileDetailsTab.class);
    ProductAndPlanManagementTab productAndPlanManagementTab = caseProfile.getDefaultWorkspace().getTab(ProductAndPlanManagementTab.class);
    ClassificationManagementTab classificationManagementTab = caseProfile.getDefaultWorkspace().getTab(ClassificationManagementTab.class);
    LocationManagementTab locationManagementTab = caseProfile.getDefaultWorkspace().getTab(LocationManagementTab.class);
    FileIntakeManagementTab fileIntakeManagementTab = caseProfile.getDefaultWorkspace().getTab(FileIntakeManagementTab.class);
    ProposalActionTab proposalActionTab = caseProfile.getDefaultWorkspace().getTab(ProposalActionTab.class);
    CreateEnrolledCensusFileIntakeManagementTab createEnrolledCensusTab = caseProfile.createEnrolledCensus().getWorkspace().getTab(CreateEnrolledCensusFileIntakeManagementTab.class);

    default void createDefaultCaseProfile() {
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultCaseProfile(GroupBenefitsMasterPolicyType... productTypes) {
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(productTypes));
    }

    static void createDefaultCaseProfile(GroupBenefitsMasterPolicyType productType) {
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(productType));
    }

    static TestData getDefaultCaseProfileTestData(GroupBenefitsMasterPolicyType... productTypes) {
        List<TestData> testDataList = Arrays.stream(productTypes).map(
                type -> caseProfile.getDefaultTestData("CaseProfile", "ProductAndPlanManagementTabGBGB")
                        .adjust(ProductAndPlanManagementTabMetaData.PRODUCT.getLabel(), type.getName()))
                .collect(Collectors.toList());

        //This is workaround for STD product's policy now can be Rated only if Census file was used
        TestData tdCase;
        if (Arrays.asList(productTypes).contains(GroupBenefitsMasterPolicyType.GB_DI_STD) || Arrays.asList(productTypes).contains(GroupBenefitsMasterPolicyType.GB_TL)
                || Arrays.asList(productTypes).contains(GroupBenefitsMasterPolicyType.GB_DI_LTD) || Arrays.asList(productTypes).contains(GroupBenefitsMasterPolicyType.GB_ST)) {
            tdCase = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                    .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName()), testDataList);
        } else if (Arrays.asList(productTypes).contains(GroupBenefitsMasterPolicyType.GB_PFL) || Arrays.asList(productTypes).contains(GroupBenefitsMasterPolicyType.GB_EAP)) {
            tdCase = caseProfile.getDefaultTestData("CaseProfile", "TestData_ForPFL")
                    .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName()), testDataList);
        } else {
            tdCase = caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName()), testDataList);
        }

        //This is workaround for PFL product's effective date (product can't be selected if Case's eff date is before product's eff date).
        if (Arrays.asList(productTypes).contains(GroupBenefitsMasterPolicyType.GB_PFL)) {
            tdCase.adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), "05/28/2019");
            if (!tdCase.getTestDataList(FileIntakeManagementTab.class.getSimpleName()).isEmpty()) {
                tdCase.getTestDataList(FileIntakeManagementTab.class.getSimpleName()).forEach(testData -> testData.adjust(TestData.makeKeyPath(FileIntakeManagementTabMetaData.EFFECTIVE_DATE.getLabel()), "05/28/2019")
                );
            }
        }
        return tdCase;
    }
}

