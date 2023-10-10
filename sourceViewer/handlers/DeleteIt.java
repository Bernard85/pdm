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
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourceEditor.SourceViewMap;
import sourceViewer.AClause;

public class DeleteIt {

	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext;
	@Inject EModelService eModelService;
	@Inject EPartService ePartService;


	@Execute public void execute(@Named(IServiceConstants.ACTIVE_PART)MPart part2
			) throws Throwable {

		SourceViewMap sourceViewMap = (SourceViewMap) part2.getTransientData().get(SourceViewMap.SOURCE_VIEW_MAP);
		sourceViewMap.modifierNotifier.setDirty(true);
		
		Element eClicked = sourceViewMap.eClicked;
		
		AClause aClause=(AClause)eClicked.getUserData(AClause.ACLAUSE); 
		
		Composite cParent = aClause.getParent(); 
		Element papa=(Element) eClicked.getParentNode();
		
		papa.removeChild(eClicked);
		aClause.setVisible(false);
		
		cParent.layout();
		
	}
}
