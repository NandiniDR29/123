package com.exigen.ren.modules.platform.efolder;

import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.webdriver.Downloads;
import com.exigen.ren.common.module.efolder.EfolderContext;
import com.exigen.ren.common.module.efolder.defaulttabs.AddFileTab;
import com.exigen.ren.common.module.efolder.metadata.AddFileTabMetaData;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;

import static com.exigen.ren.TestDataKey.DATA_GATHER;

public class EfolderBaseTest extends BaseTest implements EfolderContext {

    protected void assertAddRetrieveDocument(String documentType, String folderName) {
        String fileName = RandomStringUtils.randomAlphanumeric(10) + ".txt";
        efolder.addDocument(efolder.getDefaultTestData(DATA_GATHER).getTestData("TestData_TXT")
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.NAME.getLabel()), fileName)
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.TYPE.getLabel()), documentType), folderName);
        efolder.retrieveFile(fileName);
        CustomAssertions.assertThat(Downloads.getFile(fileName).orElseThrow(() -> new IstfException(fileName + " not found"))).exists();
    }

}
