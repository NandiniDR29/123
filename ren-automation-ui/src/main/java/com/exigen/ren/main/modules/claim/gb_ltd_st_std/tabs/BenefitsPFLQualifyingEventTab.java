package com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLQualifyingEventTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.BENEFITS;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.PFL_QUALIFYING_EVENT;

public class BenefitsPFLQualifyingEventTab extends DefaultTab {

    public static final TableExtended<ListOfPFLDateColumns> tableListOfPFLDate =
            new TableExtended<>("List Of PFL Date", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListClaimsPaidFamilyLeaveDate']/div/table"));
    public static final TableExtended<ListOfHealthCareProviderCertificationColumns> tableListOfHealthCareProviderCertification =
            new TableExtended<>("List of Health care provider certification",
                    By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListHealthCareProviderCertification']/div/table"));
    public static final TableExtended<ListOfHealthCareProviderInformationColumns> tableListOfHealthCareProviderInformation =
            new TableExtended<>("List of Health care provider Information",
                    By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListClaimsHCPInformation']/div/table"));
    public static final TableExtended<ListOfMilitaryQualifyingEventInformationColumns> tableListOfMilitaryQualifyingEventInformation =
            new TableExtended<>("List of Military Qualifying Event Information",
                    By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListClaimsMilitaryQualifyingEventInformation']/div/table"));

    public BenefitsPFLQualifyingEventTab() {
        super(BenefitsPFLQualifyingEventTabMetaData.class);
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.expand(BENEFITS.get(), this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return PFL_QUALIFYING_EVENT.get();
    }

    public enum ListOfPFLDateColumns implements Named {
        TYPE("Type"),
        DATE("Date");

        private String name;

        ListOfPFLDateColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ListOfHealthCareProviderCertificationColumns implements Named {
        FIRST_DATE("First date care for patient is needed"),
        EXPECTED_DATE("Expected date patient will no longer require care");

        private String name;

        ListOfHealthCareProviderCertificationColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ListOfHealthCareProviderInformationColumns implements Named {
        LICENSE_NUMBER("License Number");

        private String name;

        ListOfHealthCareProviderInformationColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ListOfMilitaryQualifyingEventInformationColumns implements Named {
        BEGINNING_DATE("Beginning Date"),
        TO_DATE("To Date");

        private String name;

        ListOfMilitaryQualifyingEventInformationColumns(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
