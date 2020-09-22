/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.claim.tabs;

import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.waiters.ElementWaiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.product.productfactory.DefaultTab;
import org.openqa.selenium.By;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActionsTab extends DefaultTab {
    public ActionsTab() {
    }

    /**
     * TestData: {
     * ActionsTab: {
     * Claim: [
     * {
     * name: Quotation Process State Transitions - New Business,
     * state: [
     * {
     * name: Bound,
     * actions: [certificateQuoteIssue, quickIssue, proposalPreview, masterQuotepropose]
     * },
     * {
     * name: Data Gather,
     * actions: [certificateQuoteIssue, quickIssue, proposalPreview]
     * }
     * ]
     * },
     * {
     * name: Policy Process State Transitions - Renewal,
     * state: [
     * {
     * name: Bound,
     * actions: [certificateQuoteIssue, quickIssue, proposalPreview, masterQuotepropose]
     * },
     * {
     * name: Data Gather,
     * actions: [certificateQuoteIssue, quickIssue, proposalPreview]
     * }
     * ]
     * },
     * ]
     * }
     * }
     *
     * @param td
     */
    public ActionsTab addActions(TestData td) {
        td.getTestDataList("Claim").forEach(
                testData -> ((ActionsTab) open()).new Claim(testData).addActions()
        );
        return this;
    }

    public ActionsTab deleteActions(TestData td) {
        td.getTestDataList("Claim").forEach(
                testData -> ((ActionsTab) open()).new Claim(testData).removeActions()
        );
        return this;
    }

    @Override
    protected String tabName() {
        return "Actions";
    }


    public class Claim {
        private static final String PROCESS_STATE_TMPL = "//h3[.='%s']";
        private Set<ActionsTab.State> availableState = new HashSet<>();
        private String name;

        public Claim(TestData td) {
            this.name = td.getValue("name");
            td.getTestDataList("state").forEach(testData ->
                    availableState.add(new State(testData.getValue("name"), testData.getList("actions"))));
        }

        public Claim expand() {
            PFLink linkProcessState = getLinkProcessState(name);
            if (!Boolean.parseBoolean(linkProcessState.getAttribute("aria-expanded"))) {
                linkProcessState.click();
            }
            return this;
        }

        private PFLink getLinkProcessState(String processState) {
            return new PFLink(By.xpath(String.format(PROCESS_STATE_TMPL, processState)));
        }

        public Claim collapse() {
            // logic for closing pst
            return this;
        }

        public Claim addActions() {
            expand();
            availableState.forEach(state -> state.open().addActions());
            return collapse();
        }

        public Claim removeActions() {
            expand();
            availableState.forEach(state -> state.open().removeActions());
            return collapse();
        }
    }

    public class State {
        private static final String STATE_NAME_TMPL = "//h3[@aria-expanded='true']/following-sibling::form//a[.='%s']";
        private static final String ACTION_TMPL = "//form[@id='actions-add']//h4[.='%s']/ancestor::div[@class='pf-list-item']//label[//input]";

        PFButton btnAddAction = new PFButton(By.id("actions:add"));
        PFButton btnSubmitAddActions = new PFButton(By.id("actions-add:add"));
        PFButton btnDone = new PFButton(By.xpath("//button[.='Done']"));

        private String name;
        private List<String> actions;

        public State(String name) {
            this.name = name;
        }

        public State(String name, List<String> actions) {
            this.name = name;
            this.actions = actions;
        }

        public ActionsTab.State open() {
            new PFLink(By.xpath(String.format(STATE_NAME_TMPL, name))).click(new ElementWaiter(btnDone));
            btnAddAction.click(new ElementWaiter(btnSubmitAddActions));
            return this;
        }

        public ActionsTab.State addActions() {
            actions.forEach(action ->
                    new PFCheckBox(By.xpath(String.format(ACTION_TMPL, action))).setValue(true)
            );
            btnSubmitAddActions.click(Waiters.SLEEP(1000).then(new ElementWaiter(btnDone)));
            btnDone.click();
            return submit();
        }

        public ActionsTab.State removeActions() {
            //logic for deletion
            return submit();
        }

        public ActionsTab.State submit() {
            return this;
        }
    }

}
