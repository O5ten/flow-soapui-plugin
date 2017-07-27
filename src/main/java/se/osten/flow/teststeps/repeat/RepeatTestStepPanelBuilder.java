package se.osten.flow.teststeps.repeat;

import com.eviware.soapui.impl.EmptyPanelBuilder;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.util.PanelBuilderFactory;
import com.eviware.soapui.plugins.auto.PluginPanelBuilder;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginPanelBuilder(targetModelItem = RepeatTestStep.class)
public class RepeatTestStepPanelBuilder extends EmptyPanelBuilder<RepeatTestStep> {
    Logger logger = LoggerFactory.getLogger(RepeatTestStepPanelBuilder.class);

    @Override
    public DesktopPanel buildDesktopPanel(RepeatTestStep modelItem) {
        return new RepeatTestStepDesktopPanel(modelItem);
    }

    @Override
    public boolean hasDesktopPanel() {
        return true;
    }
}
