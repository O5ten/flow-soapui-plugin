package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.ui.support.ModelItemDesktopPanel;
import com.google.common.collect.Sets;
import com.jgoodies.binding.PresentationModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Simple DesktopPanel that provides a basic UI for configuring the EMailTestStep. Should perhaps be improved with
 * a "Test" button and a log panel.
 */
public class RepeatTestStepDesktopPanel extends ModelItemDesktopPanel<RepeatTestStep> implements ActionListener
{
    private PresentationModel<RepeatTestStep> pm;
    private SimpleBindingForm form;
    private JComboBox maxAttempts;
    private JComboBox targetTestStep;

	public RepeatTestStepDesktopPanel(RepeatTestStep modelItem )
	{
		super( modelItem );
		this.pm = new PresentationModel<RepeatTestStep>(getModelItem());
		this.form = new SimpleBindingForm(pm);
		buildUI();
	}

	private void buildUI()
	{
		addDescription();
       	addMaxAttempts();
        addTargetTestStep();
		add(new JScrollPane(form.getPanel()), BorderLayout.CENTER);
		setPreferredSize(new Dimension(500, 300));
	}

	private void addTargetTestStep() {
		Set<String> testStepNamesWithinRange = getTestStepNamesWithinRange();
		this.targetTestStep = form.appendComboBox("Go to", testStepNamesWithinRange.toArray(), "TestStep to go to");
		this.targetTestStep.setSelectedItem(getModelItem().getTargetTestStep());
		this.targetTestStep.addActionListener(this);
	}

	private void addDescription(){
		this.form.appendSeparator();
		this.form.appendHeading("Settings");
		this.form.appendSeparator();
		this.form.append(new JLabel("If there are testStep failures between this testStep"));
		this.form.append(new JLabel("then the runner will reattempt those test-steps until"));
		this.form.append(new JLabel("successful or max attempts have been reached."));
	}

	private void addMaxAttempts(){
		this.maxAttempts = form.appendComboBox("Max attempts", new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}, "Maximum number of attempts to run test-steps before giving up. 20-times is way beyond reasonable.");
		this.maxAttempts.setSelectedItem(getModelItem().getMaxAttempts());
		this.maxAttempts.addActionListener(this);
	}

	private Set<String> getTestStepNamesWithinRange(){
		RepeatTestStep repeatStep = getModelItem();
		WsdlTestCase testCase = repeatStep.getTestCase();
		Set<String> testStepNames = testCase.getTestSteps().keySet();
		Set<String> testStepNamesWithinRange = Sets.newLinkedHashSet();
		int targetTestStepIndex = testCase.getTestStepIndexByName(repeatStep.getName());

		for(String testStepName : testStepNames){
			int testStepIndex = testCase.getTestStepIndexByName(testStepName);
			if(testStepIndex < targetTestStepIndex){
				testStepNamesWithinRange.add(testStepName);
			}
		}
		return testStepNamesWithinRange;
	}

	@Override
	public boolean release(){
		return super.release();
	}

    public void actionPerformed(ActionEvent e) {
        getModelItem().setMaxAttempts(Integer.parseInt(maxAttempts.getSelectedItem().toString()));
        getModelItem().setTargetTestStep(targetTestStep.getSelectedItem().toString());
    }
}
