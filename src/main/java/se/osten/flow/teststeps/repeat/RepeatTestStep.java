package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepWithProperties;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.monitor.support.TestMonitorListenerAdapter;
import com.eviware.soapui.plugins.auto.PluginTestStep;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.eviware.soapui.support.xml.XmlObjectConfigurationReader;
import com.google.common.collect.Lists;

import java.util.List;

@PluginTestStep(typeName = "RepeatTestStep", name = "Repeat Test Step",
        description = "Repeats the test from selected teststep",
        iconPath = "repeat.jpg")
public class RepeatTestStep extends WsdlTestStepWithProperties {

    private int maxAttempts;
    private String targetTestStep;
    private static boolean actionGroupAdded = false;
    private int currentAttempts;

    public RepeatTestStep(WsdlTestCase testCase, TestStepConfig config, boolean forLoadTest) {
        super(testCase, config, true, true);
        if (!actionGroupAdded) {
            SoapUI.getActionRegistry().addActionGroup(new RepeatTestStepActionGroup());
            actionGroupAdded = true;
        }
        SoapUI.getTestMonitor().addTestMonitorListener(new TestMonitorListenerAdapter() {
            public void testCaseStarted(TestCaseRunner testCaseRunner) {
                boolean isParentTestCase = testCaseRunner.getTestCase().getId().equalsIgnoreCase(getModelItem().getParent().getId());
                if (isParentTestCase) {
                    currentAttempts = 0;
                }
            }
        });
        readConfig(config);
    }

    private void readConfig(TestStepConfig config) {
        if (config != null) {
            XmlObjectConfigurationReader reader = new XmlObjectConfigurationReader(config.getConfig());
            this.maxAttempts = reader.readInt("maxAttempts", 0);
            this.targetTestStep = reader.readString("targetTestStep", "");
        } else {
            this.maxAttempts = 0;
            this.targetTestStep = "";
        }
        this.currentAttempts = 0;
    }

    private void updateConfig() {
        XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
        builder.add("maxAttempts", this.maxAttempts);
        builder.add("targetTestStep", this.targetTestStep);
        getConfig().setConfig(builder.finish());
    }

    private boolean settingExists(String settingId, String key) {
        return settingId.equalsIgnoreCase(key);
    }

    public TestStepResult run(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext) {
        WsdlTestStepResult result = new WsdlTestStepResult(this);
        if (isPredecessorsSuccessful(testCaseRunner)) {
            handleSuccess(testCaseRunner, testCaseRunContext, result);
        } else if (currentAttempts < maxAttempts) {
            handleMaxAttemptsReached(testCaseRunner, result);
        } else {
            result.setStatus(TestStepResult.TestStepStatus.FAILED);
        }
        return result;
    }

    private void handleMaxAttemptsReached(TestCaseRunner testCaseRunner, WsdlTestStepResult result) {
        testCaseRunner.gotoStepByName(getTargetTestStep());
        currentAttempts = currentAttempts + 1;
        result.setStatus(TestStepResult.TestStepStatus.OK);
    }

    private void handleSuccess(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext, WsdlTestStepResult result) {
        result.setStatus(TestStepResult.TestStepStatus.OK);
        boolean allTestsPassed = true;
        for (TestStepResult testStepResult : testCaseRunner.getResults()) {
            if (testStepResult.getStatus() != TestStepResult.TestStepStatus.OK) {
                allTestsPassed = false;
                break;
            }
        }
        if (allTestsPassed) {
            testCaseRunContext.setProperty(TestCaseRunner.Status.class.getName(), TestCaseRunner.Status.RUNNING);
        }
        currentAttempts = 0;
    }

    private boolean isPredecessorsSuccessful(TestCaseRunner runner) {
        WsdlTestCase testCase = getTestCase();
        int targetIndex = testCase.getTestStepIndexByName(this.getTargetTestStep());
        int thisIndex = testCase.getTestStepIndexByName(this.getName());
        List<TestStep> includedTestSteps = Lists.newArrayList();
        for (int i = targetIndex; i <= thisIndex; i++) {
            includedTestSteps.add(runner.getResults().get(i).getTestStep());
            boolean isFailed = runner.getResults().get(i).getStatus() != WsdlTestStepResult.TestStepStatus.OK;
            if (isFailed) {
                for (TestStep includedTestStep : includedTestSteps) {
                    for (TestStepResult historicResult : runner.getResults()) {
                        if (historicResult.getTestStep().getId().equals(includedTestStep.getId())) {
                            runner.getResults().remove(historicResult);
                            break;
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        int old = this.maxAttempts;
        this.maxAttempts = maxAttempts;
        updateConfig();
        notifyPropertyChanged("maxAttempts", old, maxAttempts);
    }

    @Override
    public boolean hasEditor() {
        return true;
    }

    public String getTargetTestStep() {
        return targetTestStep;
    }

    public void setTargetTestStep(String targetTestStep) {
        String old = this.targetTestStep;
        this.targetTestStep = targetTestStep;
        updateConfig();
        notifyPropertyChanged("targetTestStep", old, targetTestStep);
    }
}
