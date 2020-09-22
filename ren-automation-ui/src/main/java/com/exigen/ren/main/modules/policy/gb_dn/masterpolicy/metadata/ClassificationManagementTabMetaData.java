/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import org.openqa.selenium.By;

public class ClassificationManagementTabMetaData extends MetaData {
    public static final AssetDescriptor<Button> ADD_CLASSIFICATION_GROUP_RELATIONSHIP = declare("Add Classification Group Relationship", Button.class,
            By.id("policyDataGatherForm:addGroupClassGroupCoverageRelationship"));
    public static final AssetDescriptor<ComboBox> CLASSIFICATION_GROUP_NAME = declare("Classification Group Name", ComboBox.class);
    public static final AssetDescriptor<RadioGroup> USE_CLASSIFICATION_SUB_GROUPS = declare("Use Classification Sub-Groups?", RadioGroup.class);
    public static final AssetDescriptor<TextBox> TOTAL_VOLUME = declare("Total Volume", TextBox.class);
    public static final AssetDescriptor<ComboBox> CLASSIFICATION_SUB_GROUP_NAME = declare("Classification Sub-Group Name", ComboBox.class);

    public static final AssetDescriptor<ClassificationManagementTab.PlanTierAndRatingInfoMultiAssetList> PLAN_TIER_AND_RATING_INFO =
            declare("Plan Tier And Rating Info", ClassificationManagementTab.PlanTierAndRatingInfoMultiAssetList.class, PlanTierAndRatingInfoMetaData.class,
                    By.xpath("//div[@id='policyDataGatherForm:formGrid_GroupClassDefaultCoverRelationTierMoneyView' or (contains(@id,'policyDataGatherForm:componentView_GroupClass') and contains(@id,'TierMoneyView'))]"));

    public static final class PlanTierAndRatingInfoMetaData extends MetaData {
        public static final AssetDescriptor<ComboBox> COVERAGE_TIER = declare("Coverage Tier", ComboBox.class);
        public static final AssetDescriptor<ComboBox> RATE_BASIS = declare("Rate", ComboBox.class);
        public static final AssetDescriptor<TextBox> NUMBER_OF_PARTICIPANTS = declare("Number of Participants", TextBox.class);
        public static final AssetDescriptor<TextBox> RATE = declare("Rate", TextBox.class);
        public static final AssetDescriptor<TextBox> PROPOSED_RATE = declare("Proposed Rate", TextBox.class);
        public static final AssetDescriptor<ComboBox> PROPOSED_RATE_UPDATE_REASON = declare("Proposed Rate Update Reason", ComboBox.class);
    }

    public static final AssetDescriptor<AssetList> CLASSIFICATION_SUB_GROUPS_RATING_INFO = declare("Classification Sub-Groups And Rating Info ", AssetList.class, ClassificationSubGroupRatingInfoMetaData.class, By.xpath("//div[@id='policyDataGatherForm:componentView_GroupClassGroupCoverRelationship_SubGroupsRating']"));

    public static final class ClassificationSubGroupRatingInfoMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> C_NUMBER_OF_PARTICIPANTS = declare("Number of Participants", TextBox.class);
        public static final AssetDescriptor<TextBox> C_RATE = declare("Rate", TextBox.class);
    }
}
