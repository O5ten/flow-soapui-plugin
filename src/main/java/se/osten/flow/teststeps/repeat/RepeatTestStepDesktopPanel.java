package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.ui.support.ModelItemDesktopPanel;
import com.jgoodies.binding.PresentationModel;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
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
		buildUI();
	}

	private void buildUI()
	{
		this.pm = new PresentationModel<RepeatTestStep>(getModelItem());
		this.form = new SimpleBindingForm(pm);
		this.form.appendSeparator();
		this.form.appendHeading("Settings");
		this.form.appendSeparator();
		this.form.append(new JLabel("If there are testStep failures between this testStep"));
        this.form.append(new JLabel("then the runner will reattempt those test-steps until"));
        this.form.append(new JLabel("successful or max attempts have been reached."));
        this.maxAttempts = form.appendComboBox("Max attempts", new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}, "Maximum number of attempts to run test-steps before giving up. 20-times is way beyond reasonable.");
        this.maxAttempts.setSelectedItem(getModelItem().getMaxAttempts());
        this.maxAttempts.addActionListener(this);
        Set<String> testSteps = getModelItem().getTestCase().getTestSteps().keySet();
        this.targetTestStep = form.appendComboBox("Go to", testSteps.toArray(), "TestStep to go to");
        this.targetTestStep.setSelectedItem(getModelItem().getTargetTestStep());
        this.targetTestStep.addActionListener(this);
		add(new JScrollPane(form.getPanel()), BorderLayout.CENTER);
		setPreferredSize(new Dimension(500, 300));
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
