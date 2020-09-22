package com.exigen.ren;

import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.data.TestData;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerNonIndividual;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.PolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EntitiesHolder implements CaseProfileContext {

    private EntitiesHolder() {
    }

    private static final Boolean policyReuseAllowed = PropertyProvider.getProperty(RenTestProperties.TEST_REUSE_POLICY, true);
    protected static final Logger LOGGER = LoggerFactory.getLogger(EntitiesHolder.class);

    private static MultiValueMap policiesList = new MultiValueMap();
    private static ConcurrentMap<String, MutableInt> policiesReferencesCounts = new ConcurrentHashMap<>();

    public static Optional<String> getPolicy(PolicyType policyType) {
        //policiesList.put(GroupBenefitsMasterPolicyType.GB_VS, "MP0000000004"); //Uncomment this line and add already created policy that can be used as "default"
        if (!policiesList.containsKey(policyType)) {
            return Optional.empty();
        } else {
            return policiesList.getCollection(policyType).stream().filter(num -> getReferencesCount((String) num) < 20).findFirst();
        }
    }

    public static void addPolicyEntity(PolicyType type, String number) {
        policiesList.put(type, number);
    }

    private static Integer getReferencesCount(String number) {
        return policiesReferencesCounts.getOrDefault(number, new MutableInt(0)).intValue();
    }

    private static void incrementPolicyReferencesCount(String number) {
        policiesReferencesCounts.getOrDefault(number, new MutableInt(0)).increment();
    }

    private static void clearPolicyReferencesCount(String number) {
        policiesReferencesCounts.put(number, new MutableInt(1));
    }

    /**
     * Open policy created from default test data. Default policy will be created if it is not present in pool.
     * This policy will be reused in several tests, do not perform actions that can affect this policy and make it unusable by other tests.
     * @param type - Master policy type
     * @return - policy number
     */
    public static String openDefaultMasterPolicy(GroupBenefitsMasterPolicyType type) {
        Optional<String> storedNumber = getPolicy(type);
        String usedNumber;
        if (policyReuseAllowed && storedNumber.isPresent()) {
            usedNumber = storedNumber.get();
            EntitiesHolder.incrementPolicyReferencesCount(usedNumber);
            LOGGER.info("Using default master policy from pool: " + usedNumber);
            MainPage.QuickSearch.search(usedNumber);
        } else {
            createDefaultCustomerAndMasterPolicy(type);
            usedNumber = PolicySummaryPage.labelPolicyNumber.getValue();

            EntitiesHolder.addPolicyEntity(type, usedNumber);
            EntitiesHolder.clearPolicyReferencesCount(usedNumber);
        }
        return usedNumber;
    }

    /**
     * Open policy created from default test data. Default policy will be created if it is not present in pool.
     * This policy will be reused in several tests, do not perform actions that can affect this policy and make it unusable by other tests.
     * @param type - Certificate policy type
     * @return - policy number
     */
    public static String openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType type) {
        Optional<String> storedNumber = getPolicy(type);
        String usedNumber;
        if (policyReuseAllowed && storedNumber.isPresent()) {
            usedNumber = storedNumber.get();
            EntitiesHolder.incrementPolicyReferencesCount(usedNumber);
            LOGGER.info("Using default certificate policy from pool: " + usedNumber);
            MainPage.QuickSearch.search(usedNumber);
        } else {
            createDefaultCustomerAndMasterPolicy(type.getMasterPolicyType());
            type.get().createPolicy(type.get().getDefaultTestData(TestDataKey.DATA_GATHER,TestDataKey.DEFAULT_TEST_DATA_KEY)
                    .adjust(type.get().getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY)));
            usedNumber = PolicySummaryPage.labelPolicyNumber.getValue();

            EntitiesHolder.addPolicyEntity(type, usedNumber);
            EntitiesHolder.clearPolicyReferencesCount(usedNumber);
        }
        return usedNumber;
    }

    /**
     * Get quote in Premium Calculated status copied from default policy. Default policy will be created if it is not present in pool.
     * Customer and Case will stay the same as for Default policy, do not perform actions that can affect other policies on this Customer and Case
     * @param type - Master policy type
     * @return - new quote number
     */
    public static String openCopiedMasterQuote(GroupBenefitsMasterPolicyType type) {
        String newQuoteNum;
        if (policyReuseAllowed) {
            String originalPolicyNum = openDefaultMasterPolicy(type);
            LOGGER.info("Copying policy " + originalPolicyNum);
            type.get().policyCopy().perform(type.get().getDefaultTestData("CopyFromPolicy", TestDataKey.DEFAULT_TEST_DATA_KEY));
            newQuoteNum = PolicySummaryPage.labelPolicyNumber.getValue();
            LOGGER.info("New Quote number: " + newQuoteNum);
            type.get().calculatePremium();
        } else {
            CustomerNonIndividual customerNonIndividual = AutomationContext.getService(CustomerNonIndividual.class);
            customerNonIndividual.create(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
            CaseProfileContext.createDefaultCaseProfile(type);
            type.get().createQuote(type.get().getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
            newQuoteNum = PolicySummaryPage.labelPolicyNumber.getValue();
        }
        return newQuoteNum;
    }

    /**
     * Get policy in Active status copied from another default policy. Default policy will be created if it is not present in pool.
     * Customer and Case will stay the same as for Default policy, do not perform actions that can affect other policies on this Customer and Case
     * @param type - Master policy type
     * @return - new policy number
     */
    public static String openCopiedMasterPolicy(GroupBenefitsMasterPolicyType type) {
        if (policyReuseAllowed) {
            String policyNum = openCopiedMasterQuote(type);
            type.get().propose().perform(type.get().getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()
                    .mask(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), QuotesSelectionActionTab.SELECT_QUOTE_BY_ROW_NUMBER_KEY))
                    .adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), QuotesSelectionActionTab.SELECT_QUOTE_BY_QUOTE_NUMBER_KEY), policyNum));
            type.get().acceptContract().perform(type.get().getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));
            type.get().issue().perform(type.get().getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));
            LOGGER.info("Policy copied and issued to new one: " + policyNum);
            return policyNum;
        } else {
            createDefaultCustomerAndMasterPolicy(type);
            return PolicySummaryPage.labelPolicyNumber.getValue();
        }
    }

    private static void createDefaultCustomerAndMasterPolicy(GroupBenefitsMasterPolicyType masterPolicyType) {
        CustomerNonIndividual customerNonIndividual = AutomationContext.getService(CustomerNonIndividual.class);
        customerNonIndividual.create(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        CaseProfileContext.createDefaultCaseProfile(masterPolicyType);
        masterPolicyType.get().createPolicy(masterPolicyType.get().getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(masterPolicyType.get().getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(masterPolicyType.get().getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(masterPolicyType.get().getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }
}
