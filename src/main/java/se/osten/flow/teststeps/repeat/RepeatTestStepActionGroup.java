package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.impl.wsdl.actions.teststep.WsdlTestStepSoapUIActionGroup;
import com.eviware.soapui.plugins.ActionConfiguration;

@ActionConfiguration(targetType = RepeatTestStep.class, iconPath = "repeat.jpg")
public class RepeatTestStepActionGroup extends WsdlTestStepSoapUIActionGroup {
    public RepeatTestStepActionGroup() {
        super("RepeatTestStepActions", "Repeat Test Step Actions");
    }
}
