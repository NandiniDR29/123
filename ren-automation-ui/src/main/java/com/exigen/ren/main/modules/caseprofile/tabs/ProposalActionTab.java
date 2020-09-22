package com.exigen.ren.main.modules.caseprofile.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.table.NamedTable;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.Dialog;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.enums.TableConstants.OverrideErrorsTable;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;

import java.util.List;

import static com.exigen.ren.main.enums.PolicyConstants.OverrideReasons.BUSINESS_DECISION;
import static com.exigen.ren.main.modules.caseprofile.CaseProfileContext.proposalActionTab;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.FEE_UPDATE_REASON;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.PROPOSED_ASO_FEE;

public class ProposalActionTab extends ActionTab {

    public ProposalActionTab() {
        super(ProposalTabMetaData.class);
    }

    public static StaticElement labelProposalNumber = new StaticElement(By.id("proposalForm:groupProposalDataGatherBanner_proposalNumber"));
    public static StaticElement labelProposalStatus = new StaticElement(By.id("proposalForm:groupProposalDataGatherBanner_proposalStatus"));

    public static final String OVERRIDE_RULES_LIST_KEY = "Override Rules List";
    public static final String QUOTE_PROPOSAL_BLOCK_PATTERN = "//span[text()='%s']//ancestor::tr[contains(@class, 'ui-widget-content ui-datatable')]";
    public static final String COVERAGE_PROPOSAL_BLOCK_PATTERN = "//span[text()='%s']//ancestor::tr[contains(@class, 'ui-widget-content ui-datatable-odd')]";

    public static NamedTable<OverrideErrorsTable> tableErrorsList = new NamedTable<>("Errors", By.id("proposalForm:msgList"));
    private static final ByT locatorTableCoverageInfo = ByT.xpath("//span[text()='%s']/ancestor::table[1]/following-sibling::div//table[contains(@id,'coverageInfoTable') and descendant::*[text()='%s']]");
    private static final ByT locatorTableCoveragePremium = ByT.xpath("//span[text()='%s']/ancestor::table[1]/following-sibling::div//table[contains(@id,'coverageInfoTable') and descendant::*[text()='%s']]/following-sibling::div/div/table");

    private static final ByT PROPOSAL_TERM_RATE = ByT.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:%s:classTabId:%s:proposedTermRateA");
    private static final ByT PROPOSAL_TERM_RATE_MONEY = ByT.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:%s:classTabId:%s:proposedTermRateMoney");
    private static final ByT RATE_UPDATE_REASON = ByT.id("proposalForm:proposalsQuotesSelectionTable:0:coverageInfoTable:%s:classTabId:%s:rateUpdateReason");

    public static StaticElement masterQuoteErrorMessage = new StaticElement(By.xpath("//div[@id='proposalForm:proposalsQuotesSelectionTable']//table//span[contains(@class,'error_message')]"));

    public static Button buttonOverrideRules = new Button(By.id("proposalForm:overrideRulesBtn"));
    public static Button buttonOverride = new Button(By.xpath("//input[@value='Override']"));

    public static Button buttonCalculatePremium = new Button(By.xpath("//input[@value = 'Calculate Premium' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static Button buttonGenerateProposal = new Button(By.id("proposalForm:generateProposalBtn_footer"));
    public static Button buttonGeneratePreProposal = new Button(By.id("proposalForm:generatePreProposalBtn_footer"));
    public static Dialog dialogPreProposal = new Dialog("//div[@id='proposalGeneratePreProposalConfirmDialogDialog_container']");
    public static Dialog dialogProposal = new Dialog("//div[@id='proposalGenerateProposalConfirmDialogDialog_container']");
    public static Button buttonSaveAndExit = new Button(By.id("proposalForm:proposalSaveAndExitBtn_footer"));
    public static final TableExtended<TableConstants.ProposalASOFeeTable> proposalASOFeeTable = new TableExtended<>("Proposal Action ASO Fee Table", By.xpath("//div[@id='proposalForm:proposalsQuotesSelectionTable:0:proposalASOFeetable']//table[@role='grid']"));
    public static Button buttonReferForApproval = new Button(By.xpath("//input[@value='Refer for Approval']"));

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(assetList.getName()) && td.getTestData(assetList.getName()).containsKey(OVERRIDE_RULES_LIST_KEY)) {
            overrideRules(td.getTestData(assetList.getName()).getList(OVERRIDE_RULES_LIST_KEY), "Life");
        }
        assetList.fill(td);
        return this;
    }

    @Override
    public Tab submitTab() {
        ProposalActionTab.buttonCalculatePremium.click();
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.confirm();
        return this;
    }

    public void overrideRules(List<String> rules, String duration) {
        buttonOverrideRules.click();

        tableErrorsList.getRows().forEach(row -> {
            if (rules.contains(row.getCell(OverrideErrorsTable.MESSAGE.getName()).getValue())) {
                LOGGER.info(String.format("Row num: {%s} With message: {%s}  is overriding", row.getIndex(), row.getCell(OverrideErrorsTable.MESSAGE.getName()).getValue()));
                tableErrorsList.getRow(row.getIndex()).getCell(OverrideErrorsTable.OVERRIDE.getName()).controls.checkBoxes.getFirst().setValue(true);
                tableErrorsList.getRow(row.getIndex()).getCell(OverrideErrorsTable.DURATION.getName()).controls.radioGroups.getFirst().setValue(duration);
                tableErrorsList.getRow(row.getIndex()).getCell(OverrideErrorsTable.REASON_FOR_OVERRIDE.getName()).controls.comboBoxes.getFirst().setValue("Customer Satisfaction");
            } else {
                LOGGER.info(String.format("Row num: {%s} With message: {%s}  is NOT FOUND", row.getIndex(), row.getCell(OverrideErrorsTable.MESSAGE.getName()).getValue()));
            }
        });
        buttonOverride.click();
    }

    public static Table getCoverageInfo(String policyNumber, String planName) {
        return new Table(locatorTableCoverageInfo.format(policyNumber, planName));
    }

    public static TableExtended<TableConstants.CoveragePremiumTable> getTableCoveragePremium(String policyNumber, String planName) {
        return new TableExtended<>("Coverage Premium", locatorTableCoveragePremium.format(policyNumber, planName));
    }

    public static ComboBox getDiscountReasonForQuote(String quoteNumber) {
        return new ComboBox(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber) + "//select[contains(@id, 'discountReasonCd')]"));
    }

    public static TextBox getRateAdjustmentPercentageForQuote(String quoteNumber) {
        return new TextBox(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber) + "//input[contains(@id, 'discountPct')]"));
    }

    public static StaticElement getErrorMessageRateForAdjustmentPercentageForQuote(String quoteNumber) {
        return new StaticElement(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber) + "//input[contains(@id, 'discountPct')]//ancestor::td[1]//span[@class='error_message white_space_normal']"));
    }

    public static Button getRateAdjustmentApplyButtonForQuote(String quoteNumber) {
        return new Button(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber) + "//input[contains(@id, 'rateAdjustmentApplyButton')]"));
    }

    public static StaticElement getDiscountReasonErrorMessageForQuote(String quoteNumber) {
        return new StaticElement(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber) + "//span[@class='error_message white_space_normal']"));
    }

    public static TextBox getProposedTermRateForQuote(String quoteNumber) {
        return new TextBox(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber) + "//input[contains(@id, 'proposedTermRate')]"));
    }

    public static TextBox getProposedTermRateForQuote(String quoteNumber, int rowNumber) {
        return new TextBox(By.xpath("(".concat(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber).concat(String.format("//input[contains(@id, 'proposedTermRate')])[%s]", rowNumber)))));
    }

    public static StaticElement getDiscountReasonErrorMessageForQuoteByPolicy(String quoteNumber, int rowNumber) {
        return new StaticElement(By.xpath("(".concat(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber).concat(String.format("//span[@class='error_message white_space_normal'])[%s]", rowNumber)))));
    }

    public static StaticElement getDiscountReasonErrorMessageForQuoteByCoverage(String coverage, int rowNumber) {
        return new StaticElement(By.xpath("(".concat(String.format(COVERAGE_PROPOSAL_BLOCK_PATTERN, coverage).concat(String.format("//span[@class='error_message white_space_normal'])[%s]", rowNumber)))));
    }

    public static void setRateUpdateReasonForQuote(String quoteNumber, int rowNumber, String value) {
        new ComboBox(By.xpath("(".concat(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber).concat(String.format("//select[contains(@id, 'proposalForm:proposalsQuotesSelectionTable')])[%s]", rowNumber))))).setValue(value);
    }

    public static void setProposedAsoFeeForQuote(String quoteNumber, String value) {
        new TextBox(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber).concat("//td[6]/input[contains(@id, 'proposalForm:proposalsQuotesSelectionTable')]"))).setValue(value);
    }

    public static void setFeeUpdateReasonForQuote(String quoteNumber, String value) {
        new ComboBox(By.xpath(String.format(QUOTE_PROPOSAL_BLOCK_PATTERN, quoteNumber).concat("//td[8]/select[contains(@id, 'proposalForm:proposalsQuotesSelectionTable')]"))).setValue(value);
    }

    public static void performProposalForPolicyWithASO(GroupBenefitsMasterPolicyType policyType, TestData td, ImmutableList rulesForOverride) {
        policyType.get().propose().start();
        policyType.get().propose().getWorkspace().fillUpTo(policyType.get().getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY), ProposalActionTab.class, false);
        proposalActionTab.getAssetList().getAsset(PROPOSED_ASO_FEE).setValue("10");
        proposalActionTab.getAssetList().getAsset(FEE_UPDATE_REASON).setValue("index=1");

        policyType.get().propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(td
                .adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY), rulesForOverride));
        proposalActionTab.submitTab();
    }

    public static void setProposedTermRateAndUpdateReason(int coverageInfoTable, int classTabId) {
        double proposalTermRateInitial = new Double(new TextBox(PROPOSAL_TERM_RATE.format(coverageInfoTable, classTabId)).getValue());
        new TextBox(PROPOSAL_TERM_RATE.format(coverageInfoTable, classTabId)).setValue(String.valueOf((proposalTermRateInitial - 0.01)).substring(0, 4));
        new ComboBox(RATE_UPDATE_REASON.format(coverageInfoTable, classTabId)).setValue(BUSINESS_DECISION);
    }

    public static void setProposedTermRateMoneyAndUpdateReason(int coverageInfoTable, int classTabId) {
        Currency proposalTermRateInitial = new Currency(new TextBox(PROPOSAL_TERM_RATE_MONEY.format(coverageInfoTable, classTabId)).getValue());
        new TextBox(PROPOSAL_TERM_RATE_MONEY.format(coverageInfoTable, classTabId)).setValue(proposalTermRateInitial.add(0.01).toPlainString());
        new ComboBox(RATE_UPDATE_REASON.format(coverageInfoTable, classTabId)).setValue(BUSINESS_DECISION);
    }

    public static void performPreProposal(){
        buttonCalculatePremium.click();
        buttonGeneratePreProposal.click();
        dialogPreProposal.buttonYes.click();
    }
}
