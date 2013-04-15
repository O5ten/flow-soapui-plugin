package soapui.demo.teststeps.email;

import com.eviware.soapui.impl.EmptyPanelBuilder;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.util.PanelBuilderFactory;
import com.eviware.soapui.ui.desktop.DesktopPanel;

/**
 * Creates the DesktopPanel - could be extended to also support the bottom left Properties tab
 */

public class EMailTestStepPanelBuilderFactory implements PanelBuilderFactory<EMailTestStep>
{
	@Override
	public PanelBuilder<EMailTestStep> createPanelBuilder()
	{
		return new EMailTestStepPanelBuilder();
	}

	@Override
	public Class<EMailTestStep> getTargetModelItem()
	{
		return EMailTestStep.class;
	}

	public static class EMailTestStepPanelBuilder extends EmptyPanelBuilder<EMailTestStep>
	{
		@Override
		public DesktopPanel buildDesktopPanel( EMailTestStep modelItem )
		{
			return new EMailTestStepDesktopPanel( modelItem );
		}

		@Override
		public boolean hasDesktopPanel()
		{
			return true;
		}
	}
}
