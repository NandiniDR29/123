/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.account;

import com.exigen.ren.common.EntityService;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.account.actions.*;

public interface IAccount extends EntityService {

    Workspace getDefaultWorkspace();

    AddCustomerAction addCustomer();

    EliminateCustomerAction eliminateCustomer();

    MoveCustomerAction moveCustomer();

    MoveCustomerToNewAccountAction moveCustomerToNewAccount();

    AddAffinityGroupAction addAffinityGroup();

    EliminateAffinityGroupAction eliminateAffinityGroup();

    AddCommunicationAction addCommunication();

    RemoveCommunicationAction removeCommunication();

    UpdateAction update();

    AddDesignatedContactAction addDesignatedContact();

    EliminateDesignatedContactAction eliminateDesignatedContact();
}
