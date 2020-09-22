/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ipb.eisa.utils;

import com.exigen.istf.config.ClassConfigurator.Configurable;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author ikisly
 */
// TODO Line 180 was added. Remove this class after changes will be applied in ISBA
public class RetrySuiteGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrySuiteGenerator.class);
    @Configurable
    private static String retrySuiteFilePath = "retry.xml";
    @Configurable
    private static String retrySuiteNamePostfix = "RETRY";
    @Configurable
    private static String rootPath = FilenameUtils.separatorsToSystem("src\\test\\resources\\testsuites");
    private ConcurrentLinkedQueue<XmlTestWrapper> xmlTestWrapperList = new ConcurrentLinkedQueue<>();

    private boolean isRootDataSet = false;
    private XmlSuite suite;

    public RetrySuiteGenerator() {
        suite = new XmlSuite();
    }

    public void setRootSuiteData(XmlSuite rootSuite) {
        if (!isRootDataSet) {
            suite.setName(String.format("%s %s", rootSuite.getName(), retrySuiteNamePostfix));
            suite.setVerbose(rootSuite.getVerbose());
            suite.setThreadCount(rootSuite.getThreadCount());
            suite.setParallel(rootSuite.getParallel());
            suite.setListeners(rootSuite.getListeners());
            suite.setParameters(rootSuite.getParameters());
            isRootDataSet = true;
        }
    }

    public void collectFailedTests(ITestResult result, boolean isFailureCovered) {
        int status = result.getStatus();
        if ((status == ITestResult.FAILURE || status == ITestResult.SKIP) && !isFailureCovered) {
            Optional<XmlTestWrapper> xmlTestWrapper = xmlTestWrapperList.stream()
                    .filter(xmlTestW -> xmlTestW.getXmlTest().getName().equals(result.getTestClass().getXmlTest().getName())).findFirst();
            if (xmlTestWrapper.isPresent()) {
                // if xmlTestWrapperList already contains xmlTestWrapper with XmlTest name which failed, we iterate to find the XmlClass and include the failing method
                // if saved failed class already contain failed method - doing nothing
                Optional<XmlClass> xmlClass = xmlTestWrapper.get().getFailedClasses().stream()
                        .filter(xmlC -> xmlC.getName().equals(result.getTestClass().getXmlClass().getName())).findFirst();
                if (xmlClass.isPresent()) {
                    if (xmlClass.get().getIncludedMethods().stream().noneMatch(xmlInclude -> Objects.equals(xmlInclude.getName(), result.getMethod().getMethodName()))) {
                        xmlClass.get().getIncludedMethods().add(prepareXmlInclude(result));
                        xmlTestWrapper.get().setDependentOnGroups(result);
                    }
                } else {
                    XmlClass anotherXmlClass = (XmlClass) result.getTestClass().getXmlClass().clone();
                    anotherXmlClass.getIncludedMethods().removeIf(xmlInclude -> ObjectUtils.notEqual(xmlInclude.getName(), result.getMethod().getMethodName()));
                    if (CollectionUtils.isEmpty(anotherXmlClass.getIncludedMethods())) {
                        anotherXmlClass.getIncludedMethods().add(prepareXmlInclude(result));
                    } else {
                        anotherXmlClass.getIncludedMethods().get(0).setParameters(getTestMethodParameters(result));
                    }
                    xmlTestWrapper.get().getXmlTest().getXmlClasses().add(anotherXmlClass);
                    xmlTestWrapper.get().getFailedClasses().add(anotherXmlClass);
                    xmlTestWrapper.get().setDependentOnGroups(result);
                }
            } else {
                xmlTestWrapperList.add(new XmlTestWrapper(result));
            }
        }
    }

    public void generateSuite() {
        if (xmlTestWrapperList.stream().anyMatch(XmlTestWrapper::isDependentOnGroups)) {
            LOGGER.warn("\"Depends On Groups\" reference was detected." +
                            "Removing following class(es) from retry.xml: \n{}" +
                            "\nWe are not able to determine all tests from dependent groups.",
                    xmlTestWrapperList.stream()
                            .filter(XmlTestWrapper::isDependentOnGroups)
                            .collect(Collectors.toList()));
            xmlTestWrapperList.removeIf(XmlTestWrapper::isDependentOnGroups);
        }
        if (!this.xmlTestWrapperList.isEmpty()) {
            ArrayList<XmlTest> retryTestsList = this.xmlTestWrapperList.stream()
                    .map((x) -> {
                        return x.resolve().getXmlTest();
                    })
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));
            this.suite.setTests(retryTestsList);

            try {
                File file = FileUtils.getFile(rootPath + File.separator + retrySuiteFilePath);
                FileUtils.writeStringToFile(file, this.suite.toXml(), UTF_8);
                LOGGER.info("Generating retry suite at: {}", file.getAbsolutePath());
                LOGGER.info("Retry suite file created: \n " + suite.toXml());
            } catch (IOException var3) {
                LOGGER.error("Failed to write " + retrySuiteFilePath, var3);
            }
        }
    }

    private XmlInclude prepareXmlInclude(ITestResult result) {
        XmlInclude include = new XmlInclude(result.getName());
        include.setParameters(getTestMethodParameters(result));
        return include;
    }

    /**
     * Get method parameters. If parameters defined on method level - returns method params, on class level - class params, on test level - test params and on suite level - suite params
     * For method level params there is an restriction/bug/limitation from TestNG side, i.e. in case if class tag includes multiple methods with same param key - only first entrance of such test will contain parameters,
     * all following methods will return null(empty) value
     * Refer to https://stackoverflow.com/questions/48171506/how-to-obtain-same-parameter-for-an-included-method-in-testng-suite-xml
     *
     * @param result {@link ITestResult}
     * @return available parameters for the test method
     */
    private Map<String, String> getTestMethodParameters(ITestResult result) {
        return result.getMethod().findMethodParameters(result.getTestContext().getCurrentXmlTest());
    }

    private class XmlTestWrapper {
        private XmlTest xmlTest;
        private ConcurrentLinkedQueue<XmlClass> failedClasses = new ConcurrentLinkedQueue<>();
        private boolean isDependentOnGroups;

        //TODO move initialization mapping logic outside the constructor
        private XmlTestWrapper(ITestResult testResult) {
            this.xmlTest = getXmlTest(testResult);
            this.setDependentOnGroups(testResult);
            this.failedClasses.addAll(this.xmlTest.getClasses());
        }

        private ConcurrentLinkedQueue<XmlClass> getFailedClasses() {
            return failedClasses;
        }

        private XmlTest getXmlTest() {
            return xmlTest;
        }

        private XmlTestWrapper resolve() {
            this.xmlTest.setXmlClasses(new ArrayList<>(this.failedClasses));
            return this;
        }

        private boolean isDependentOnGroups() {
            return isDependentOnGroups;
        }

        private void setDependentOnGroups(ITestResult testResult) {
            this.isDependentOnGroups = testResult.getMethod().getGroupsDependedUpon().length != 0;
        }

        private XmlTest getXmlTest(ITestResult testResult) {
            XmlTest test = (XmlTest) testResult.getTestClass().getXmlTest().clone();
            test.setExcludedGroups(Lists.newArrayList());
            test.setIncludedGroups(Lists.newArrayList());
            test.setPackages(Lists.newArrayList());
            XmlClass xmlClass = (XmlClass) testResult.getTestClass().getXmlClass().clone();
            xmlClass.setIncludedMethods(new LinkedList<>(xmlClass.getIncludedMethods()));
            xmlClass.getIncludedMethods().removeIf(xmlInclude -> ObjectUtils.notEqual(xmlInclude.getName(), testResult.getMethod().getMethodName()));
            if (Objects.nonNull(testResult.getMethod().getMethodsDependedUpon()) && testResult.getMethod().getMethodsDependedUpon().length != 0) {
                Arrays.stream(testResult.getMethod().getMethodsDependedUpon()).forEach(s ->
                {
                    String dependentMethodName = s.substring(s.lastIndexOf(".") + 1);
                    if (xmlClass.getIncludedMethods().stream().noneMatch(xmlInclude -> xmlInclude.getName().equals(dependentMethodName))) {
                        xmlClass.getIncludedMethods().add(new XmlInclude(dependentMethodName));
                    }
                });
            }
            if (Objects.nonNull(xmlClass.getIncludedMethods()) && xmlClass.getIncludedMethods().size() == 0) {
                xmlClass.getIncludedMethods().add(prepareXmlInclude(testResult));
            } else {
                xmlClass.getIncludedMethods().get(0).setParameters(getTestMethodParameters(testResult));
            }
            List<XmlClass> classes = Lists.newArrayList();
            classes.add(xmlClass);
            test.setXmlClasses(classes);
            return test;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof XmlTestWrapper && this.xmlTest.equals(((XmlTestWrapper) obj).getXmlTest());
        }

        @Override
        public int hashCode() {
            return Objects.hash(xmlTest, failedClasses);
        }

        @Override
        public String toString() {
            return failedClasses.stream().map(XmlClass::getName).collect(Collectors.joining(","));
        }
    }
}
