/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.utils;

import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.SoapJobActions;

public class BatchJobRunner {

    public static void main(String[] args) {
        SoapJobActions soapJobActions = new SoapJobActions();
        soapJobActions.createJob(JobGroup.fromSingleJob("automatedProcessingInitiationJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("automatedProcessingIssuingOrProposingJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("automatedProcessingRatingJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("benefits.billingInvoiceJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("billingInvoiceGenerationJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("cancellationConfirmationGenerationJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("cancellationNoticeGenerationJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("cascadingTransactionCertificateProcessingDispatchingJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("cascadingTransactionCompletionDetectionJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("cascadingTransactionProcessingInitiationJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("earnedPremiumWriteoffProcessingJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("policyAutomatedRenewalAsyncTaskGenerationJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("policyBORTransferJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("policyStatusUpdateJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("refundGenerationJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("renewalProposingJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("renewalRatingJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("pendingUpdateJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("recurringPaymentsProcessingJob"));
        soapJobActions.createJob(JobGroup.fromSingleJob("recurringPaymentNoticesProcessingJob"));
    }
}
