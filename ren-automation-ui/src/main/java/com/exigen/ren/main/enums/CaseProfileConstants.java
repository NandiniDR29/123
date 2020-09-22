/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.enums;

public final class CaseProfileConstants {

    private CaseProfileConstants() {}

    public static final String ARE_YOU_SURE_WANT_TO_GENERATE_THE_PROPOSAL_DOCUMENT = "Are you sure you want to generate the proposal document?";

    public static final class ProposalStatus {
        public static final String PROPOSED_PREMIUM_CALCULATED = "Proposed Premium Calculated";
        public static final String PROPOSAL_SET_UP = "Proposal Set-up";
        public static final String GENERATED = "Generated";
    }

    public static final class ErrorMessages {
        public static final String THIS_MASTER_QUOTE_IS_INCLUDED_INTO_OTHER_PROPOSAL = "This Master Quote is included into other proposal(s). " +
                "Updates for this quote will be reflected on other not generated proposals.";
        public static final String NO_QUOTES_APPLICABLE = "No quotes applicable for selection exist.";
        public static final String NO_QUOTE_HAS_BEEN_SELECTED = "No Quote has been selected to be included into a Proposal.";
        public static final String PROPOSAL_NAME_SHOULD_BE_MANDATORY = "Proposal Name should be Mandatory.";
        public static final String VALUE_IS_GREATER_THAN_ALLOWABLE_MAXIMUM = "Value is greater than allowable maximum of '255'";
        public static final String NO_RECORD_FOUND = "No records found.";
    }

    public static final class CaseProfileTable {
        public static final String CASE_PROFILE_NAME = "Case Profile Name";
        public static final String STATUS = "Status";
    }

    public static final class CaseProfileProposalTable {
        public static final String PROPOSAL_NAME = "Proposal Name";
        public static final String PROPOSAL_STATUS = "Proposal Status";
        public static final String ACTION = "Action";
        public static final String PROPOSAL_NUMBER = "Proposal Number";
    }

    public static final class CaseProfileSubGroupsTable {
        public static final String ACTIONS = "Actions";
    }

    public static final class CaseProfileVersionHistoryTable {
        public static final String VERSION_STATE = "Version State";
        public static final String EXPIRATION_DATE = "Expiration Date";
        public static final String HASHTAG = "#";
    }
}
