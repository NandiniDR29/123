package com.exigen.ren.main.modules.claim.common.metadata;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class EliminationQualificationPeriodActionTabMetaData extends MetaData {

    public static final AssetDescriptor<AssetList> ELIMINATION_PERIOD = declare("Elimination Period", AssetList.class, EliminationPeriodMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_PrecClaimsEliminationQualificationPeriodOverride']"));

    public static final AssetDescriptor<AssetList> QUALIFICATION_PERIOD = declare("Qualification Period", AssetList.class, QualificationPeriodMetaData.class,
            By.xpath("//div[@id='policyDataGatherForm:componentView_PrecClaimsQualificationPeriodOverride']"));

    public static final class EliminationPeriodMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> ELIMINATION_PERIOD_THROUGH_DATE = declare("Elimination Period Through Date", TextBox.class);
        public static final AssetDescriptor<TextBox> OVERRIDE_REASON = declare("Override Reason", TextBox.class);
    }

    public static final class QualificationPeriodMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> ELIMINATION_PERIOD_THROUGH_DATE = declare("Qualification Period Through Date", TextBox.class);
        public static final AssetDescriptor<TextBox> OVERRIDE_REASON = declare("Override Reason", TextBox.class);
    }
}
