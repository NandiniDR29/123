package com.exigen.ren.modules.docgen.gb_tl;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.XmlValidator;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.coveragesTable;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationXMLBaseTestTL extends ValidationXMLBaseTest {

    public ValidationXMLBaseTestTL() {
    }

    private String getBenefitCostInfoSectionXmlDocument(XmlValidator xmlValidator, DocGenEnum.AllSections section, String policyNumber,
                                                        String planName, String className) {
        String xmlDocument = xmlValidator.convertNodeToString(getBenefitCostInfoSection(xmlValidator, policyNumber, planName)
                .findNode(String.format(section.get(), className)));

        assertThat(xmlDocument).as("Section '" + section.get() + "' not found by className '" + className + "'").isNotEmpty();
        return xmlDocument;
    }

    private String getBenefitDetailsInfoSectionXmlDocument(XmlValidator xmlValidator, DocGenEnum.AllSections section, String policyNumber,
                                                           String planName, String coverageCd) {
        String xmlDocument = xmlValidator.convertNodeToString(getBenefitDetailsInfoSection(xmlValidator, policyNumber, planName)
                .findNode(String.format(section.get(), coverageCd)));

        assertThat(xmlDocument).as("Section '" + section.get() + "' not found by coverageCd '" + coverageCd + "'").isNotEmpty();
        return xmlDocument;
    }

    private String getBenefitDetailsInfoSectionXmlDocument(XmlValidator xmlValidator, DocGenEnum.AllSections section, String policyNumber,
                                                           String planName, String coverageCd, String groupName) {
        String xmlDocument = xmlValidator.convertNodeToString(getBenefitDetailsInfoSection(xmlValidator, policyNumber, planName)
                .findNode(String.format(section.get(), coverageCd, groupName)));

        assertThat(xmlDocument)
                .as("Section '" + section.get() + "' not found by coverageCd '" + coverageCd + "' and className '" + groupName + "'").isNotEmpty();
        return xmlDocument;
    }

    protected XmlValidator getClassificationBL_EmployeeSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_BL_EMPLOYEE_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getClassificationBL_DependentSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_BL_DEPENDENT_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getClassificationBADD_EmployeeSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_BADD_EMPLOYEE_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getClassificationBADD_DependentSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_BADD_DEPENDENT_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getClassificationVL_EmployeeSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_VL_EMPLOYEE_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getClassificationVL_SpouseSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_VL_SPOUSE_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getClassificationVADD_EmployeeSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_VADD_EMPLOYEE_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getClassificationVADD_DependentSection(XmlValidator xmlValidator, String policyNumber, String planName, String className) {
        return new XmlValidator(getBenefitCostInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.CLASSIFICATION_VADD_DEPENDENT_SECTION_BY_CLASS_NAME,
                policyNumber, planName, className));
    }

    protected XmlValidator getBenefitDetailsInfoCombinedBLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd) {
        return new XmlValidator(getBenefitDetailsInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.BENEFIT_DETAILS_INFO_COMBINED_BL_SECTION_BY_COVERAGE,
                policyNumber, planName, coverageCd));
    }

    protected XmlValidator getAgeBandedInfoBLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String ageDesc) {
        String xmlDocument = xmlValidator.convertNodeToString(getBenefitDetailsInfoCombinedBLSection(xmlValidator, policyNumber, planName, coverageCd)
                .findNode(String.format(DocGenEnum.AllSections.AGE_BANDED_INFO_SECTION_BY_AGE.get(), ageDesc)));

        assertThat(xmlDocument).as("Section 'ageBandedInfoItems' not found by coverageCd '" + ageDesc + "'").isNotEmpty();
        return new XmlValidator(xmlDocument);
    }

    protected List<String> getAgeReductionsBenefitDetailsInfoBLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd) {
        NodeList nodeList = getBenefitDetailsInfoCombinedBLSection(xmlValidator, policyNumber, planName, coverageCd)
                .findNodes(DocGenEnum.AllSections.LIFE56.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    protected List<String> getAgeReductionsBenefitDetailsInfoBADDSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd) {
        NodeList nodeList = getBenefitDetailsInfoCombinedBLSection(xmlValidator, policyNumber, planName, coverageCd)
                .findNodes(DocGenEnum.AllSections.LIFE73.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    protected XmlValidator getBenefitDetailsInfoVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd) {
        return new XmlValidator(getBenefitDetailsInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.BENEFIT_DETAILS_INFO_VL_SECTION_BY_COVERAGE,
                policyNumber, planName, coverageCd));
    }

    protected XmlValidator getAgeBandedInfoVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String ageDesc) {
        String xmlDocument = xmlValidator.convertNodeToString(getBenefitDetailsInfoVLSection(xmlValidator, policyNumber, planName, coverageCd)
                .findNode(String.format(DocGenEnum.AllSections.AGE_BANDED_INFO_SECTION_BY_AGE.get(), ageDesc)));

        assertThat(xmlDocument).as("Section 'ageBandedInfoItems' not found by coverageCd '" + ageDesc + "'").isNotEmpty();
        return new XmlValidator(xmlDocument);
    }

    protected List<String> getGuaranteedDescListBenefitDetailsInfoVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd) {
        NodeList nodeList = getBenefitDetailsInfoVLSection(xmlValidator, policyNumber, planName, coverageCd)
                .findNodes(DocGenEnum.AllSections.LIFE102.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    protected List<String> getAgeReductionsBenefitDetailsInfoVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd) {
        NodeList nodeList = getBenefitDetailsInfoVLSection(xmlValidator, policyNumber, planName, coverageCd)
                .findNodes(DocGenEnum.AllSections.LIFE103.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    protected XmlValidator getBenefitDetailsInfoDVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String groupName) {
        return new XmlValidator(getBenefitDetailsInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.BENEFIT_DETAILS_INFO_DVL_SECTION_BY_COVERAGE_AND_CLASS_NAME,
                policyNumber, planName, coverageCd, groupName));
    }

    protected XmlValidator getAgeBandedInfoDVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String groupName, String ageDesc) {
        String xmlDocument = xmlValidator.convertNodeToString(getBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, planName, coverageCd, groupName)
                .findNode(String.format(DocGenEnum.AllSections.AGE_BANDED_INFO_SECTION_BY_AGE.get(), ageDesc)));

        assertThat(xmlDocument).as("Section 'ageBandedInfoItems' not found by coverageCd '" + ageDesc + "'").isNotEmpty();
        return new XmlValidator(xmlDocument);
    }

    protected List<String> getGuaranteedDescListBenefitDetailsInfoDVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String groupName) {
        NodeList nodeList = getBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, planName, coverageCd, groupName)
                .findNodes(DocGenEnum.AllSections.LIFE117.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    protected List<String> getAgeReductionsBenefitDetailsInfoDVLSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String groupName) {
        NodeList nodeList = getBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, planName, coverageCd, groupName)
                .findNodes(DocGenEnum.AllSections.LIFE119.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    protected XmlValidator getBenefitDetailsInfoVADDSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String groupName) {
        return new XmlValidator(getBenefitDetailsInfoSectionXmlDocument(xmlValidator, DocGenEnum.AllSections.BENEFIT_DETAILS_INFO_VADD_SECTION_BY_COVERAGE_AND_CLASS_NAME,
                policyNumber, planName, coverageCd, groupName));
    }

    protected List<String> getDependentAgeReductionsBenefitDetailsInfoVADDSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String groupName) {
        NodeList nodeList = getBenefitDetailsInfoVADDSection(xmlValidator, policyNumber, planName, coverageCd, groupName)
                .findNodes(DocGenEnum.AllSections.LIFE132.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    protected List<String> getAgeReductionsBenefitDetailsInfoVADDSection(XmlValidator xmlValidator, String policyNumber, String planName, String coverageCd, String groupName) {
        NodeList nodeList = getBenefitDetailsInfoVADDSection(xmlValidator, policyNumber, planName, coverageCd, groupName)
                .findNodes(DocGenEnum.AllSections.LIFE131.get());

        return xmlValidator.convertNodeListToList(nodeList);
    }

    /*
     * Method return map of values from ClassificationManagementTab (Classification Group Name, Number of
     * Participants, Rate, Total Volume)
     */
    protected Map<String, String> getValuesFromClassificationManagement(String coverage, String groupName) {
        coveragesTable.getRow(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME, coverage).getCell(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME).click();

        tableCoverageRelationships.getRow(TableConstants.CoverageRelationships.CLASS_NAME.getName(), groupName)
                .getCell(6).controls.links.get(ActionConstants.CHANGE).click();

        HashMap<String, String> mapValues = new HashMap<>();
        mapValues.put(CLASSIFICATION_GROUP_NAME.getLabel(),
                new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(CLASSIFICATION_GROUP_NAME.getLabel())).getValue());
        mapValues.put(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName(),
                tableCoverageRelationships.getRow(TableConstants.CoverageRelationships.CLASS_NAME.getName(), groupName)
                        .getCell(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()).getValue());
        mapValues.put(TableConstants.CoverageRelationships.RATE.getName(),
                formatProposedRate(tableCoverageRelationships.getRow(TableConstants.CoverageRelationships.CLASS_NAME.getName(), groupName)
                        .getCell(TableConstants.CoverageRelationships.RATE.getName()).getValue()));
        mapValues.put(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName(),
                formatTotalVolume(new Currency(tableCoverageRelationships.getRow(TableConstants.CoverageRelationships.CLASS_NAME.getName(), groupName)
                        .getCell(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()).getValue())));

        return mapValues;
    }

    /*
     * Method return ModalPremium from table premiumSummaryByPayorCoveragesTable
     */
    protected Currency getCommonModalPremiumByPair(String coverage) {
        tableCoveragesName.getRow(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), coverage)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()).controls.links.getFirst().click(Tab.doubleWaiter);

        Currency modalPremium = new Currency();
        for (Row row : premiumSummaryByPayorCoveragesTable.getRows()) {
            modalPremium = modalPremium.add(new Currency(row.getCell(TableConstants.PremiumSummaryCoveragesTable.MODAL_PREMIUM.getName()).getValue()));
        }
        return modalPremium;
    }

    /*
     * Method return ModalPremium from table premiumSummaryClassNameTable
     */
    protected Currency getModalPremiumByClassName(String coverage, String groupName) {
        tableCoveragesName.getRow(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), coverage)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()).controls.links.getFirst().click(Tab.doubleWaiter);
        premiumSummaryByPayorCoveragesTable.getRow(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), coverage)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()).controls.links.getFirst().click(Tab.doubleWaiter);

        return new Currency(premiumSummaryClassNameTable.getRow(PremiumSummaryClassNameTable.CLASS_NAME.getName(), groupName)
                .getCell(PremiumSummaryClassNameTable.MODAL_PREMIUM.getName()).getValue());
    }
}
