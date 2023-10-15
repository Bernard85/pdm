package sourceViewer.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourceEditor.SourceViewMap;
import sourceViewer.AClause;

public class SelectIt {

	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext;
	@Inject EModelService eModelService;
	@Inject EPartService ePartService;

	@Execute public void execute(@Named(IServiceConstants.ACTIVE_PART)MPart part2
			) throws Throwable {
		
		SourceViewMap sourceViewMap=SourceViewMap.getSourceViewMap(part2);
		
		selectIt(sourceViewMap);
	}

	public static void selectIt(SourceViewMap sourceViewMap) {
		if (sourceViewMap.eSelected!=null) {
			AClause aClause = AClause.getAClause(sourceViewMap.eSelected);
			sourceViewMap.eSelected=null;
			aClause.redraw();
		}

		sourceViewMap.eSelected=sourceViewMap.eNewSelected;
		AClause aClause = AClause.getAClause(sourceViewMap.eSelected);
		aClause.redraw();

		sourceViewMap.styledText.redraw();		
	}
}
