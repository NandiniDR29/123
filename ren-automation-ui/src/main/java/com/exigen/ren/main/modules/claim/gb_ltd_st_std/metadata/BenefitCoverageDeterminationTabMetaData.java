package com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;

public class BenefitCoverageDeterminationTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> INSURED_PERSON_COVERAGE_EFFECTIVE_DATE = declare("Insured Person Coverage Effective Date", TextBox.class);
    public static final AssetDescriptor<TextBox> APPROVED_THROUGH_DATE = declare("Approved Through Date", TextBox.class);
    public static final AssetDescriptor<ComboBox> LIABILITY_DECISION = declare("Liability Decision", ComboBox.class);
    public static final AssetDescriptor<ComboBox> COVERAGE = declare("Coverage", ComboBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
}