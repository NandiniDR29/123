package com.exigen.ren.main.modules.policy.common.actions.common;

import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.ActionConstants;

public class ArchiveTransactionAction implements Action {

    @Override
    public String getName() {
        return ActionConstants.ProductAction.ARCHIVE_TRANSACTION;
    }

    @Override
    public Workspace getWorkspace() {
        throw new IstfException(String.format("Please register workspace view for current action [%1$s]", this.getClass().getSimpleName()));
    }

    @Override
    public Action perform() {
        start();
        return submit();
    }

    @Override
    public Action submit() {
        Tab.buttonOk.click();
        return this;
    }
}
