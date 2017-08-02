package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepWithProperties;
import com.eviware.soapui.model.testsuite.*;
import com.eviware.soapui.monitor.support.TestMonitorListenerAdapter;
import com.eviware.soapui.plugins.auto.PluginTestStep;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.eviware.soapui.support.xml.XmlObjectConfigurationReader;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

@PluginTestStep(typeName = "RepeatTestStep", name = "Repeat Test Step",
        description = "Repeats the test from selected teststep",
        iconPath = "se/osten/flow/teststeps/repeat/repeat.png")
public class RepeatTestStep extends WsdlTestStepWithProperties {

    private int maxAttempts;
    private String targetTestStep;
    private static boolean actionGroupAdded = false;
    private int currentAttempts;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
        propertyChangeSupport.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(TestStep.NAME_PROPERTY) && evt.getOldValue().equals(RepeatTestStep.this.targetTestStep)){
                    RepeatTestStep.this.targetTestStep = evt.getNewValue().toString();
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

    @Override
    public ImageIcon getIcon() {
        return UISupport.createImageIcon("se/osten/flow/teststeps/repeat/repeat.png");
    }

    public TestStepResult run(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext) {
        RepeatTestStepResult result = new RepeatTestStepResult(this);
        if(this.targetTestStep.equals("")){
            result.setStatus(TestStepResult.TestStepStatus.FAILED);
            result.setError(new Exception("Unable to run repeat teststep without a target teststep"));
            return result;
        }
        if (isIterationSuccessful(testCaseRunner)) {
            handleSuccessfulIteration(testCaseRunner, testCaseRunContext, result);
        } else if (currentAttempts < maxAttempts) {
            triggerNextIteration(testCaseRunner, result);
        } else {
            result.setStatus(TestStepResult.TestStepStatus.FAILED);
            result.setError(new RuntimeException("maximum number of attempts reached"));
        }
        return result;
    }

    private void triggerNextIteration(TestCaseRunner testCaseRunner, WsdlTestStepResult result) {
        testCaseRunner.gotoStepByName(getTargetTestStep());
        currentAttempts = currentAttempts + 1;
        result.setStatus(TestStepResult.TestStepStatus.OK);
    }

    private void handleSuccessfulIteration(TestCaseRunner runner, TestCaseRunContext testCaseRunContext, WsdlTestStepResult result) {
        result.setStatus(TestStepResult.TestStepStatus.OK);
        if (isIterationSuccessful(runner) && isPreviousRepeatStepsSuccessful(runner)) {
            testCaseRunContext.setProperty(TestCaseRunner.Status.class.getName(), TestCaseRunner.Status.RUNNING);
        } else {
            testCaseRunContext.setProperty(TestCaseRunner.Status.class.getName(), TestCaseRunner.Status.FAILED);
        }
        currentAttempts = 0;
    }

    private boolean isPreviousRepeatStepsSuccessful(TestCaseRunner runner) {
        List<TestStepResult> results = runner.getResults();
        for(TestStepResult result : results ){
            if(result instanceof RepeatTestStepResult && result.getStatus() == RepeatTestStepResult.TestStepStatus.FAILED){
                return false;
            }
        }
        return true;
    }

    private boolean isIterationSuccessful(TestCaseRunner runner) {
        List<TestStepResult> results = runner.getResults();
        WsdlTestCase testCase = getTestCase();
        int fromIndex = testCase.getTestStepIndexByName(this.getTargetTestStep());
        int toIndex = testCase.getTestStepIndexByName(this.getName());
        int distance = toIndex-fromIndex;
        int topIndex = results.size()-1;
        for (int i = topIndex; i > topIndex-distance; i--) {
            TestStepResult testResult = results.get(i);
            boolean isFailedTestStep = testResult.getStatus() != WsdlTestStepResult.TestStepStatus.OK;
            if(isFailedTestStep){
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
