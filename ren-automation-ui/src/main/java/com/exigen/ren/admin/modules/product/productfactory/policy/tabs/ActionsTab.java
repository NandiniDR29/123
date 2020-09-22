/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.policy.tabs;

import com.exigen.ipb.eisa.base.application.Application;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.waiters.ElementWaiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.product.productfactory.DefaultTab;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.exigen.istf.webdriver.controls.waiters.Waiters.SLEEP;

public class ActionsTab extends DefaultTab {
    private PFButton btnDone = new PFButton(By.xpath("//button[.='Done']"));
    private PFButton buttonSave = new PFButton(By.id("persistence:save"));

    public ActionsTab() {
    }

    @Override
    protected String tabName() {
        return "Actions";
    }

    public ActionsTab addActions(TestData td) {
        td.getTestDataList("StateTransitions").forEach(
                testData -> ((ActionsTab) open()).new StateTransitions(testData).addActions()
        );
        return this;
    }

    public ActionsTab deleteActions(TestData td) {
        td.getTestDataList("StateTransitions").forEach(
                testData -> ((ActionsTab) open()).new StateTransitions(testData).removeActions()
        );
        return this;
    }

    public List<String> getAllProcessStates() {
        new ActionsTab().open();
        return new StateTransitions().getAllProcessStates();
    }

    public List<String> getAllActionsType(TestData td) {
        new ActionsTab().open();
        return new StateTransitions(td.getTestData("StateTransitions")).getAllActionsType();
    }

    public Map<String, String> getAllActions(TestData td) {
        new ActionsTab().open();
        return new StateTransitions(td.getTestData("StateTransitions")).getAllActions();
    }

    public void save() {
        buttonSave.click(Waiters.DEFAULT.then(SLEEP(5000)));
    }

    public class StateTransitions {
        private static final String PROCESS_STATE_TMPL = "//h3[.='%s']";
        private static final String ACTION_TYPE_TMPL = "pf-list-item";
        private Set<ActionType> availableActionType = new HashSet<>();
        private String stateTransitionType;

        public StateTransitions() {
        }

        public StateTransitions(TestData td) {
            this.stateTransitionType = td.getValue("name");
            if (td.getTestDataList("actionTypes").size() != 0) {
                td.getTestDataList("actionTypes").forEach(testData ->
                        availableActionType.add(new ActionType(testData.getValue("actionName"), testData.getList("actions"))
                        ));
            } else {
                availableActionType.add(new ActionType(td.getValue("actionName")));
            }
        }

        public StateTransitions expand() {
            PFLink linkProcessState = getLinkProcessState(stateTransitionType);
            if (!Boolean.parseBoolean(linkProcessState.getAttribute("aria-expanded"))) {
                linkProcessState.click(Waiters.DEFAULT.then(Waiters.SLEEP(1000)));
            }
            return this;
        }

        public StateTransitions close() {

            Application.wait(1000);
            getLinkProcessState(stateTransitionType).click();
            return this;
        }

        private PFLink getLinkProcessState(String processState) {
            return new PFLink(By.xpath(String.format(PROCESS_STATE_TMPL, processState)));
        }

        public List<String> getAllActionsType() {
            expand();
            List<String> list = BrowserController.get().driver().findElements(By.className(ACTION_TYPE_TMPL)).stream().map(
                    WebElement::getText).filter(we -> !we.isEmpty()).collect(Collectors.toList());
            close();
            return list;
        }

        public Map<String, String> getAllActions() {
            expand();
            Map<String, String> allActions = availableActionType.stream().findFirst().get().getAllActions();
            btnDone.click();
            close();
            return allActions;
        }

        public StateTransitions collapse() {
            // logic for closing pst
            return this;
        }

        public StateTransitions addActions() {
            expand();
            availableActionType.forEach(ActionType::addActions);
            close();
            return collapse();
        }

        public StateTransitions removeActions() {
            expand();
            availableActionType.forEach(ActionType::removeActions);
            btnDone.click();
            return collapse();

        }

        public List<String> getAllProcessStates() {
            return BrowserController.get().driver().findElements(By.xpath(".//h3"))
                    .stream().map(WebElement::getText).collect(Collectors.toList());
        }
    }

    public class ActionType {
        private static final String STATE_NAME_TMPL = "//h3[@aria-expanded='true']/following-sibling::form//a[.='%s']";
        private static final String ACTION_TMPL = "//form[@id='actions-add']//h4[.='%s']/ancestor::div[@class='pf-list-item']//label[//input]";
        private static final String DELETE_ELEMENT_TMPL = "//div[@class='pf-list-item action-item' and div//h4[.='%s']]//button[1]";
        private PFButton btnAddAction = new PFButton(By.id("actions:add"));
        private PFButton btnSubmitAddActions = new PFButton(By.id("actions-add:add"));
        private final static String ACTION_ITEM_TMPL = "//div[@class='pf-list-item action-item']";

        private String actionTypeName;
        private List<String> actions;

        public ActionType(String actionTypeName) {
            this.actionTypeName = actionTypeName;
        }

        public ActionType(String actionTypeName, List<String> actions) {
            this.actionTypeName = actionTypeName;
            this.actions = actions;
        }

        public ActionType(TestData td) {

        }

        public ActionType addActions() {
            new PFLink(By.xpath(String.format(STATE_NAME_TMPL, actionTypeName))).click(new ElementWaiter(btnDone));
            btnAddAction.click(new ElementWaiter(btnSubmitAddActions));
            actions.forEach(action ->
                    new PFCheckBox(By.xpath(String.format(ACTION_TMPL, action))).setValue(true)
            );
            btnSubmitAddActions.click(SLEEP(1000).then(new ElementWaiter(btnDone)));
            btnDone.waitForAccessible(3000);
            btnDone.click();
            return submit();
        }

        public ActionType removeActions() {
            new PFLink(By.xpath(String.format(STATE_NAME_TMPL, actionTypeName))).click(
                    Waiters.DEFAULT.then(new ElementWaiter(btnDone)));
            actions.forEach(action ->
                    new PFButton(By.xpath(String.format(DELETE_ELEMENT_TMPL, action))).click()
            );
            return submit();
        }

        public Map<String, String> getAllActions() {
            new PFLink(By.xpath(String.format(STATE_NAME_TMPL, actionTypeName))).click(new ElementWaiter(btnDone));
            return BrowserController.get().driver().findElements(By.xpath(ACTION_ITEM_TMPL)).
                    stream().collect(Collectors.toMap(we -> we.findElement(By.xpath(".//h3")).getText(),
                    o -> o.findElement(By.xpath(".//h4")).getText()));
        }

        public ActionType submit() {
            return this;
        }
    }
}
