package com.exigen.ren.main.modules.account;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;

public class AccountNonIndividual extends Account {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/cem/account/nonindividual");

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
