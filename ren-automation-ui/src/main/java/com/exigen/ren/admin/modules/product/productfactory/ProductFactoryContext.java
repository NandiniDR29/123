/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory;

import com.exigen.ren.admin.modules.product.productfactory.claim.ProductFactoryClaim;
import com.exigen.ren.admin.modules.product.productfactory.policy.ProductFactoryPolicy;
import com.exigen.ren.common.AutomationContext;

public interface ProductFactoryContext {
    ProductFactoryPolicy productFactoryPolicy = AutomationContext.getService(ProductFactoryPolicy.class);
    ProductFactoryClaim productFactoryClaim = AutomationContext.getService(ProductFactoryClaim.class);
}
