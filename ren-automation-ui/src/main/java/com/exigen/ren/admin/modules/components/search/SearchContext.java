/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.components.search;

import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.enums.SearchEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.SearchPage;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

public interface SearchContext {
    String CLAIM_TEMPLATE = "Claim: ";

    default void findBySearchPlus(String searchString, String columnName, SearchEnum.SearchFor searchFor, SearchEnum.SearchBy searchBy, String value, StaticElement label) {
        MainPage.QuickSearch.buttonSearchPlus.click();
        SearchPage.search(searchFor, searchBy, value);
        resultVerification(label, columnName, searchString);
    }

    default void findByQuickSearch(String searchString, StaticElement label) {
        if(isClaim(searchString)) {
            searchString = validateClaim(searchString);
        }
        MainPage.QuickSearch.search(searchString);
        assertThat(label.getValue().contains(searchString)).isTrue();
    }

    default void findByQuickSearchExpectedError(SearchEnum.SearchFor searchFor, SearchEnum.SearchBy searchBy, String value) {
        MainPage.QuickSearch.buttonSearchPlus.click();
        SearchPage.search(searchFor, searchBy, value);
        assertThat(SearchPage.labelSearchError.isPresent()).isTrue();
    }

    default void resultVerification(StaticElement label, String columnName, String searchString) {
        if (label.isPresent()) {
            CustomAssertions.assertThat(label).hasValue(searchString);
        } else {
            if (isClaim(searchString)) {
                searchString = validateClaim(searchString);
            }
            assertThat(SearchPage.isResultPresent(columnName, searchString) || SearchPage.tableSearchResults.getRow(1).getCell(1).controls.links.getFirst().getValue().equals(searchString)).isTrue();
        }
    }

    default boolean isClaim(String claim) {
        return claim.contains(CLAIM_TEMPLATE);
    }

    default String validateClaim(String searchString) {
        return  searchString.split(" ")[1].replace("#", "");
    }
}
