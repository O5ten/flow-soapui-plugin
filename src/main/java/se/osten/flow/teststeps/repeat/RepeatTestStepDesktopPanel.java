package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlRunTestCaseTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.support.TestSuiteListenerAdapter;
import com.eviware.soapui.model.testsuite.*;
import com.eviware.soapui.security.SecurityTest;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.ui.support.ModelItemDesktopPanel;
import com.google.common.collect.Sets;
import com.jgoodies.binding.PresentationModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;

import static com.eviware.soapui.model.ModelItem.NAME_PROPERTY;

/**
 * Simple DesktopPanel that provides a basic UI for configuring the EMailTestStep. Should perhaps be improved with
 * a "Test" button and a log panel.
 */
public class RepeatTestStepDesktopPanel extends ModelItemDesktopPanel<RepeatTestStep> implements ActionListener {
    private PresentationModel<RepeatTestStep> pm;
    private SimpleBindingForm form;
    private JComboBox maxAttemptsComboBox;
    private JComboBox targetTestStepComboBox;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(getModelItem());
    private WsdlTestStep targetTestStep;

    public RepeatTestStepDesktopPanel(RepeatTestStep modelItem) {
        super(modelItem);
        this.pm = new PresentationModel<RepeatTestStep>(getModelItem());
        this.form = new SimpleBindingForm(pm);
        String testStepName = modelItem.getTargetTestStep();
        WsdlTestStep step = modelItem.getTestCase().getTestStepByName(testStepName);
        if(step != null){
            this.targetTestStep = step;
            this.targetTestStep.addPropertyChangeListener(testStepNameListener);
        }
        modelItem.getTestCase().getTestSuite().addTestSuiteListener(new TestSuiteListenerAdapter() {
            private void resetIfTargetTestStep(TestStep activatedStep) {
                if (targetTestStep.getName().equals(activatedStep.getName())){
                    resetTargetTestStepComboBox(activatedStep.getName());
                }
                if(activatedStep.getName().equals(getModelItem().getName())){
                    resetTargetTestStepComboBox(targetTestStep.getName());
                }
            }

            @Override
            public void testStepAdded(TestStep testStep, int index) {
                resetIfTargetTestStep(testStep);
            }

            @Override
            public void testStepRemoved(TestStep testStep, int index) {
                resetIfTargetTestStep(testStep);
            }

            @Override
            public void testStepMoved(TestStep testStep, int fromIndex, int offset) {
                resetIfTargetTestStep(testStep);
            }
        });
        buildUI();
    }

    private void buildUI() {
        addDescription();
        addMaxAttempts();
        addTargetTestStep();
        add(new JScrollPane(form.getPanel()), BorderLayout.CENTER);
        setPreferredSize(new Dimension(500, 300));
    }

    private void addTargetTestStep() {
        Set<String> testStepNamesWithinRange = getTestStepNamesWithinRange();

        this.targetTestStepComboBox = form.appendComboBox("Go to", testStepNamesWithinRange.toArray(), "TestStep to go to");
        this.targetTestStepComboBox.setEnabled(testStepNamesWithinRange.size() != 0);
        this.targetTestStepComboBox.setSelectedItem(getModelItem().getTargetTestStep());
        this.targetTestStepComboBox.addActionListener(this);
    }

    private void addDescription() {
        this.form.appendSeparator();
        this.form.appendHeading("Settings");
        this.form.appendSeparator();
        this.form.append(new JLabel("If there are testStep failures between this testStep"));
        this.form.append(new JLabel("then the runner will reattempt those test-steps until"));
        this.form.append(new JLabel("successful or max attempts have been reached."));
    }

    private void addMaxAttempts() {
        this.maxAttemptsComboBox = form.appendComboBox("Max attempts", new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}, "Maximum number of attempts to run test-steps before giving up. 20-times is way beyond reasonable.");
        this.maxAttemptsComboBox.setSelectedItem(getModelItem().getMaxAttempts());
        this.maxAttemptsComboBox.addActionListener(this);
    }

    private Set<String> getTestStepNamesWithinRange() {
        RepeatTestStep repeatStep = getModelItem();
        WsdlTestCase testCase = repeatStep.getTestCase();
        Set<String> testStepNames = testCase.getTestSteps().keySet();
        Set<String> testStepNamesWithinRange = Sets.newLinkedHashSet();
        int targetTestStepIndex = testCase.getTestStepIndexByName(repeatStep.getName());

        for (String testStepName : testStepNames) {
            int testStepIndex = testCase.getTestStepIndexByName(testStepName);
            if (testStepIndex < targetTestStepIndex) {
                testStepNamesWithinRange.add(testStepName);
            }
        }
        return testStepNamesWithinRange;
    }

    @Override
    public boolean release() {
        return super.release();
    }

    public void actionPerformed(ActionEvent e) {
        String targetTestStepName = targetTestStepComboBox.getSelectedItem().toString();
        getModelItem().setMaxAttempts(Integer.parseInt(maxAttemptsComboBox.getSelectedItem().toString()));
        getModelItem().setTargetTestStep(targetTestStepName);
        WsdlTestStep currentlySelectedTestStep = getModelItem().getTestCase().getTestStepByName(targetTestStepName);
        if (!currentlySelectedTestStep.getName().equals(this.targetTestStep.getName())) {
            this.targetTestStep.removePropertyChangeListener(testStepNameListener);
            this.targetTestStep = currentlySelectedTestStep;
            this.targetTestStep.addPropertyChangeListener(ModelItem.NAME_PROPERTY, testStepNameListener);
        }
    }

    private PropertyChangeListener testStepNameListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ModelItem.NAME_PROPERTY)) {
                resetTargetTestStepComboBox(evt.getNewValue().toString());
            }
        }
    };

    private void resetTargetTestStepComboBox(String targetTestStep) {
        targetTestStepComboBox.removeActionListener(RepeatTestStepDesktopPanel.this);
        targetTestStepComboBox.removeAllItems();
        for (String testStepName : getTestStepNamesWithinRange()) {
            targetTestStepComboBox.addItem(testStepName);
        }
        if (targetTestStepComboBox.getItemCount() != 0) {
            targetTestStepComboBox.setEnabled(targetTestStepComboBox.getItemCount() != 0);
        } else {
            targetTestStepComboBox.setEnabled(true);
            if (getTestStepNamesWithinRange().contains(targetTestStep)) {
                targetTestStepComboBox.setSelectedItem(targetTestStep);
            } else {
                int thisIndex = getModelItem().getTestCase().getTestStepIndexByName(getModelItem().getName());
                WsdlTestStep closestTestStep = getModelItem().getTestCase().getTestStepAt(thisIndex - 1);
                this.targetTestStep = closestTestStep;
                targetTestStepComboBox.setSelectedItem(closestTestStep.getName());
            }
        }
        getModelItem().setTargetTestStep(targetTestStep);
        targetTestStepComboBox.addActionListener(RepeatTestStepDesktopPanel.this);
    }
}