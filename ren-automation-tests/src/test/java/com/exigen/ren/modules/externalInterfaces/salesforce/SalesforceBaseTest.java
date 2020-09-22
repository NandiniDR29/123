package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.SalesforceRestService;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.exigen.ren.rest.salesforce.model.SalesforceOpportunityModel;
import com.exigen.ren.rest.salesforce.model.SalesforceUserModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.concurrent.TimeUnit;

public class SalesforceBaseTest extends BaseTest {

    protected SalesforceRestService salesforceService = RESTServiceType.SALESFORCE.get();

    protected String getSalesforceAccountId() {
        RetryService.run(predicate -> (CustomerSummaryPage.labelSalesforceID.isPresent() && !CustomerSummaryPage.labelSalesforceID.getValue().isEmpty()),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        return CustomerSummaryPage.labelSalesforceID.getValue();
    }

    protected ResponseContainer<SalesforceOpportunityModel> getResponseOpportunity(String caseNumber) {
        return RetryService.run(predicate -> (predicate.getResponse().getStatus() == 200),
                () -> salesforceService.getOpportunity(caseNumber),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
    }

    protected ResponseContainer<SalesforceUserModel> getResponseUser(String ownerId) {
        return RetryService.run(predicate -> (predicate.getResponse().getStatus() == 200),
                () -> salesforceService.getUserFromSalesforce(ownerId),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
    }

    protected ResponseContainer<SalesforceQuoteModel> getResponseQuote(String quoteId) {
        return RetryService.run(predicate -> (predicate.getResponse().getStatus() == 200),
                () -> salesforceService.getQuoteFromSalesforce(quoteId),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
    }
}
