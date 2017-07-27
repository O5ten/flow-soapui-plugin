package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.impl.wsdl.actions.teststep.WsdlTestStepSoapUIActionGroup;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.plugins.ActionGroup;

@ActionConfiguration(targetType = RepeatTestStep.class)
public class RepeatTestStepActionGroup extends WsdlTestStepSoapUIActionGroup {
    public RepeatTestStepActionGroup() {
        super("RepeatTestStepActions", "Repeat Test Step Actions");
    }
}
