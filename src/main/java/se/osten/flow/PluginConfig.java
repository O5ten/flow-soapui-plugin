package se.osten.flow;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;
import com.eviware.soapui.support.UISupport;

@PluginConfiguration( groupId = "se.osten.flow", name = "Flow Plugin", version = "1.0.0",
        autoDetect = true, description = "Provides teststeps that allow certain control-flow in your testcases",
        infoUrl = "https://github.com/O5ten/flow-soapui-plugin")
public class PluginConfig extends PluginAdapter {
    public PluginConfig(){
        super();
        UISupport.addResourceClassLoader(getClass().getClassLoader());
    }
}