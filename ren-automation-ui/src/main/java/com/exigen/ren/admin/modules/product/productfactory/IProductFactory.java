/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.product.IProduct;

public interface IProductFactory extends IProduct {

    void initiate(TestData td);

    void submit();

    void searchAndOpen(TestData td);

    void expire(TestData td);

    void importProduct(TestData td);

    ProductFactoryActions.Copy copy();

    ProductFactoryActions.Update update();

    ProductFactoryActions.Inquiry inquiry();

    void search(TestData td);

    void navigate();


}
