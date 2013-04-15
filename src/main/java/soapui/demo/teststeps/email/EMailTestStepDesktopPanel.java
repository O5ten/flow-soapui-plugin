package soapui.demo.teststeps.email;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import com.eviware.soapui.support.DocumentListenerAdapter;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.components.JUndoableTextArea;
import com.eviware.soapui.support.components.JUndoableTextField;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.ui.support.ModelItemDesktopPanel;
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder;

/**
 * Simple DesktopPanel that provides a basic UI for configuring the EMailTestStep. Should perhaps be improved with
 * a "Test" button and a log panel.
 */

public class EMailTestStepDesktopPanel extends ModelItemDesktopPanel<EMailTestStep>
{
	private JUndoableTextField subjectField;
	private JSplitPane split;
	private JUndoableTextArea messageField;
	private PresentationModel<EMailTestStep> mailForm;

	public EMailTestStepDesktopPanel( EMailTestStep modelItem )
	{
		super( modelItem );
		buildUI();
	}

	private void buildUI()
	{
		ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addFixed( new JLabel( "EMail Subject" ) );
		builder.addRelatedGap();
		subjectField = new JUndoableTextField( getModelItem().getSubject() );
		subjectField.getDocument().addDocumentListener( new DocumentListenerAdapter()
		{
			@Override
			public void update( Document document )
			{
				getModelItem().setSubject( subjectField.getText() );
			}
		} );

		subjectField.setPreferredSize( new Dimension( 200, 20 ) );
		builder.addFixed( subjectField );

		builder.getPanel().setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );
		add( builder.getPanel(), BorderLayout.NORTH );

		split = UISupport.createVerticalSplit( createMessageField(), createEmailOptionsField() );
		add( split, BorderLayout.CENTER );

		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				split.setDividerLocation( 300 );
			}
		} );
	}

	private JPanel createMessageField()
	{
		JPanel panel = UISupport.createEmptyPanel( 3, 3, 3, 3 );

		ButtonBarBuilder builder = new ButtonBarBuilder();
		builder.addFixed( new JLabel( "<html><b>Message content</b></html>" ) );
		panel.add( builder.getPanel(), BorderLayout.NORTH );

		messageField = new JUndoableTextArea( getModelItem().getMessage() );
		messageField.getDocument().addDocumentListener( new DocumentListenerAdapter()
		{
			@Override
			public void update( Document document )
			{
				getModelItem().setMessage( messageField.getText() );
			}
		} );

		panel.add( new JScrollPane( messageField ) );

		return panel;
	}

	private JComponent createEmailOptionsField()
	{
		mailForm = new PresentationModel<EMailTestStep>( getModelItem() );
		SimpleBindingForm form = new SimpleBindingForm( mailForm );

		form.appendTextField( "server", "Server", "The SMTP Server to use" );
		form.appendTextField( "mailTo", "To", "The mail To address" );
		form.appendTextField( "mailFrom", "From", "The mail From address" );

		form.getPanel().setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );

		return new JScrollPane( form.getPanel() );
	}

	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		super.propertyChange( evt );

		String newValue = String.valueOf( evt.getNewValue() );
		if( evt.getPropertyName().equals( "subject" ) )
		{
			if( !newValue.equals( subjectField.getText() ) )
				subjectField.setText( newValue );
		}
		else if( evt.getPropertyName().equals( "message" ) )
		{
			if( !newValue.equals( messageField.getText() ) )
				messageField.setText( newValue );
		}
	}
}
