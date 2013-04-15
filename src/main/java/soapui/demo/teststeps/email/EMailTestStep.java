package soapui.demo.teststeps.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepWithProperties;
import com.eviware.soapui.model.propertyexpansion.PropertyExpansion;
import com.eviware.soapui.model.propertyexpansion.PropertyExpansionContainer;
import com.eviware.soapui.model.propertyexpansion.PropertyExpansionUtils;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.eviware.soapui.support.xml.XmlObjectConfigurationReader;

/**
 * Custom TestStep that sends emails as configured in the UI. Supports property-expansion in all fields.
 */

public class EMailTestStep extends WsdlTestStepWithProperties implements PropertyExpansionContainer
{
	private String subject;
	private String message;
	private String server;
	private String mailTo;
	private String mailFrom;

	protected EMailTestStep( WsdlTestCase testCase, TestStepConfig config, boolean forLoadTest )
	{
		super( testCase, config, true, forLoadTest );

		if( !forLoadTest )
		{
			setIcon( UISupport.createImageIcon( "email.png" ) );
		}

		if( config.getConfig() != null )
		{
			readConfig( config );
		}
	}

	private void readConfig( TestStepConfig config )
	{
		XmlObjectConfigurationReader reader = new XmlObjectConfigurationReader( config.getConfig() );
		subject = reader.readString( "subject", "" );
		message = reader.readString( "message", "" );
		server = reader.readString( "server", "" );
		mailTo = reader.readString( "mailTo", "" );
		mailFrom = reader.readString( "mailFrom", "" );
	}

	private void updateConfig()
	{
		XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
		builder.add( "subject", subject );
		builder.add( "message", message );
		builder.add( "server", server );
		builder.add( "mailTo", mailTo );
		builder.add( "mailFrom", mailFrom );
		getConfig().setConfig( builder.finish() );
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject( String subject )
	{
		String old = this.subject;
		this.subject = subject;
		updateConfig();
		notifyPropertyChanged( "subject", old, subject );
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage( String message )
	{
		String old = this.message;
		this.message = message;
		updateConfig();
		notifyPropertyChanged( "message", old, message );
	}

	public String getServer()
	{
		return server;
	}

	public void setServer( String server )
	{
		String old = this.server;
		this.server = server;
		updateConfig();
		notifyPropertyChanged( "server", old, server );
	}

	public String getMailTo()
	{
		return mailTo;
	}

	public void setMailTo( String mailTo )
	{
		String old = this.mailTo;
		this.mailTo = mailTo;
		updateConfig();
		notifyPropertyChanged( "mailTo", old, mailTo );
	}

	public String getMailFrom()
	{
		return mailFrom;
	}

	public void setMailFrom( String mailFrom )
	{
		String old = this.mailFrom;
		this.mailFrom = mailFrom;
		updateConfig();
		notifyPropertyChanged( "mailFrom", old, mailFrom );
	}

	@Override
	public TestStepResult run( TestCaseRunner testRunner, TestCaseRunContext context )
	{
		WsdlTestStepResult result = new WsdlTestStepResult( this );
		result.startTimer();

		try
		{
			Properties props = System.getProperties();
			props.put( "mail.smtp.host", context.expand( server ) );

			Session session = Session.getDefaultInstance( props, null );
			Message msg = new MimeMessage( session );
			msg.setFrom( new InternetAddress( context.expand( mailFrom ) ) );
			msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( context.expand( mailTo ), false ) );
			msg.setSubject( context.expand( subject ) );
			msg.setText( context.expand( message ) );
			msg.setHeader( "X-Mailer", "soapUI EMail TestStep" );
			msg.setSentDate( new Date() );

			Transport.send( msg );
			result.setStatus( TestStepStatus.OK );
		}
		catch( Exception ex )
		{
			SoapUI.logError( ex );
			result.setError( ex );
			result.setStatus( TestStepStatus.FAILED );
		}

		result.stopTimer();

		return result;
	}

	public PropertyExpansion[] getPropertyExpansions()
	{
		List<PropertyExpansion> result = new ArrayList<PropertyExpansion>();
		result.addAll( PropertyExpansionUtils.extractPropertyExpansions( this, this, "subject" ) );
		result.addAll( PropertyExpansionUtils.extractPropertyExpansions( this, this, "message" ) );
		result.addAll( PropertyExpansionUtils.extractPropertyExpansions( this, this, "server" ) );
		result.addAll( PropertyExpansionUtils.extractPropertyExpansions( this, this, "mailTo" ) );
		result.addAll( PropertyExpansionUtils.extractPropertyExpansions( this, this, "mailFrom" ) );
		return result.toArray( new PropertyExpansion[result.size()] );
	}
}
