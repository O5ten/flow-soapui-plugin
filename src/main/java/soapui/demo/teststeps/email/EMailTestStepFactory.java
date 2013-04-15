package soapui.demo.teststeps.email;

import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestStepFactory;

/**
 * The actual factory class that creates new EMailTestSteps from scratch or an XMLBeans config.
 */

public class EMailTestStepFactory extends WsdlTestStepFactory
{
	private static final String EMAIL_STEP_ID = "email";

	public EMailTestStepFactory()
	{
		super( EMAIL_STEP_ID, "EMail TestStep", "Sends an email", "email.png" );
	}

	public WsdlTestStep buildTestStep( WsdlTestCase testCase, TestStepConfig config, boolean forLoadTest )
	{
		return new EMailTestStep( testCase, config, forLoadTest );
	}

	public TestStepConfig createNewTestStep( WsdlTestCase testCase, String name )
	{
		TestStepConfig testStepConfig = TestStepConfig.Factory.newInstance();
		testStepConfig.setType( EMAIL_STEP_ID );
		testStepConfig.setName( name );
		return testStepConfig;
	}

	public boolean canCreate()
	{
		return true;
	}
}
