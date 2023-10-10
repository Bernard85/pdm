package sourceEditor.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import sourceEditor.SourceViewMap;
import sourceViewer.AClause;

public class UpdateIt {

	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext;
	@Inject EModelService eModelService;
	@Inject EPartService ePartService;
	
	@Execute public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part1) throws Throwable {
		SourceViewMap sourceViewMap = (SourceViewMap) part1.getTransientData().get(SourceViewMap.SOURCE_VIEW_MAP);
		String sRanges = (String) iEclipseContext.get(AClause.RANGES);
		String sBlock = (String) iEclipseContext.get(AClause.BLOCK);
		sourceViewMap.modifierNotifier.setDirty(true);
		sourceViewMap.eSelected.setAttribute(AClause.RANGES, sRanges);
		sourceViewMap.eSelected.setAttribute(AClause.BLOCK, sBlock);
		AClause aClause = (AClause)sourceViewMap.eSelected.getUserData(AClause.ACLAUSE);
		aClause.update();
	}
}
