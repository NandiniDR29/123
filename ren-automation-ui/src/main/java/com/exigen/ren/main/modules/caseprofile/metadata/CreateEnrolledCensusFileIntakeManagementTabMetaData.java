package com.exigen.ren.main.modules.caseprofile.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class CreateEnrolledCensusFileIntakeManagementTabMetaData  extends MetaData {
    public static final AssetDescriptor<ComboBox> INTAKE_PROFILE = declare("Intake Profile", ComboBox.class, By.id("caseForm:intakeProfileTab:selectableCensusFile"));
    public static final AssetDescriptor<Button> GENERATE_AND_UPLOAD_CENSUS_FILE = declare("Generate And Upload Census File",Button.class, By.id("caseForm:intakeProfileTab:generateAndUploadCensusFile"));
    public static final AssetDescriptor<StaticElement> NOTE = declare("Note", StaticElement.class, By.id("caseForm:intakeProfileTab:noteField"));
    public static final AssetDescriptor<StaticElement> ERROR_MESSAGE = declare("Error Message", StaticElement.class, By.id("caseForm:intakeProfileTab:errorMsg"));
    public static final AssetDescriptor<StaticElement> SUCCESS_MESSAGE = declare("Success Message", StaticElement.class, By.id("caseForm:intakeProfileTab:successMsg"));
}
