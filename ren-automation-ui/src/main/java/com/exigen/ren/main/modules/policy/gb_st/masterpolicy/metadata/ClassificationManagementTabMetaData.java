/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import org.openqa.selenium.By;

public class ClassificationManagementTabMetaData extends MetaData {
    public static final AssetDescriptor<Button> ADD_CLASSIFICATION_GROUP_COVERAGE_RELATIONSHIP = declare("Add Classification Group Coverage Relationship", Button.class,
            By.id("policyDataGatherForm:addGroupClassGroupCoverageRelationship"));
    public static final AssetDescriptor<ComboBox> CLASSIFICATION_GROUP_NAME = declare("Classification Group Name", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> USE_CLASSIFICATION_SUB_GROUPS = declare("Use Classification Sub-Groups?", RadioGroup.class);
    public static final AssetDescriptor<ComboBox> COVERAGE_TIER = declare("Coverage Tier", ComboBox.class);
    public static final AssetDescriptor<TextBox> NUMBER_OF_PARTICIPANTS = declare("Number of Participants", TextBox.class);
    public static final AssetDescriptor<TextBox> TOTAL_VOLUME = declare("Total Volume", TextBox.class);
    public static final AssetDescriptor<TextBox> VOLUME = declare("Volume", TextBox.class);
    public static final AssetDescriptor<TextBox> RATE = declare("Rate", TextBox.class);
    public static final AssetDescriptor<TextBox> EMPLOYEE_RATE = declare("Employee Rate", TextBox.class);
    public static final AssetDescriptor<ComboBox> CLASSIFICATION_SUB_GROUP_NAME = declare("Classification Sub-Group Name", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> UNISEX = declare("Unisex", RadioGroup.class);
    public static final AssetDescriptor<RadioGroup> UNI_TOBACCO = declare("Uni-Tobacco", RadioGroup.class);

    public static final AssetDescriptor<ClassificationManagementTab.ClassificationSubGroupsAndRatingInfoMultiAssetList> CLASSIFICATION_SUB_GROUPS_AND_RATING_INFO =
            declare("Classification Sub-Groups And Rating Info", ClassificationManagementTab.ClassificationSubGroupsAndRatingInfoMultiAssetList.class, ClassificationSubGroupsAndRatingInfoMetaData.class,
                    By.xpath("//div[contains(@id,'policyDataGatherForm:componentView_GroupClassCoverRelation_SubGroupRating')]"));

    public static final AssetDescriptor<ClassificationManagementTab.PlanTierAndRatingInfoMultiAssetList> PLAN_TIER_AND_RATING_INFO =
            declare("Plan Tier And Rating Info", ClassificationManagementTab.PlanTierAndRatingInfoMultiAssetList.class, PlanTierAndRatingInfoMetaData.class,
                    By.xpath("//table[@id='policyDataGatherForm:formGrid_GroupClassDefaultCoverRelationTierDecimalView']"));

    public static final class PlanTierAndRatingInfoMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> COVERAGE_TIER = declare("Coverage Tier", ComboBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_PARTICIPANTS = declare("Number of Participants", TextBox.class);
        public static final AssetDescriptor<TextBox> TOTAL_VOLUME = declare("Total Volume", TextBox.class);
        public static final AssetDescriptor<TextBox> VOLUME = declare("Volume", TextBox.class);
        public static final AssetDescriptor<TextBox> RATE = declare("Rate", TextBox.class);
    }

    public static final class ClassificationSubGroupsAndRatingInfoMetaData extends MetaData{
        public static final AssetDescriptor<ComboBox> CLASSIFICATION_SUBGROUP_NAME = declare("Classification Sub-Group Name", ComboBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_PARTICIPANTS = declare("Number of Participants", TextBox.class);
        public static final AssetDescriptor<TextBox> TOTAL_VOLUME = declare("Total Volume", TextBox.class);
        public static final AssetDescriptor<TextBox> RATE = declare("Rate", TextBox.class);

    }
}
