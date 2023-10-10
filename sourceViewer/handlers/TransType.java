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
import org.w3c.dom.Element;

import sourceEditor.SourceViewMap;
import sourceViewer.AClause;
import sourceViewer.Clause;
import sourceViewer.ClauseConditional;

public class TransType {

	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext;
	@Inject EModelService eModelService;
	@Inject EPartService ePartService;


	@Execute public void execute(@Named(IServiceConstants.ACTIVE_PART)MPart part2) throws Throwable {

		SourceViewMap sourceViewMap = (SourceViewMap) part2.getTransientData().get(SourceViewMap.SOURCE_VIEW_MAP);
		sourceViewMap.modifierNotifier.setDirty(true);
		
		Element eOld = sourceViewMap.eClicked;
		Element eNew=null;
		
		if (eOld.getNodeName()==AClause.CLAUSE) {
			eNew = sourceViewMap.dView.createElement(AClause.BRACKET);
			eNew.setAttribute(AClause.HEIGHTCOND,"30");
			eNew.setAttribute(AClause.EXPANDED,"true");
		}
		else if	(eOld.getNodeName()==AClause.BRACKET) {
			eNew = sourceViewMap.dView.createElement(AClause.CLAUSE);
		}
		eNew.setAttribute(AClause.NO, eOld.getAttribute(AClause.NO));
		eNew.setAttribute(AClause.BLOCK, eOld.getAttribute(AClause.BLOCK));
		eNew.setAttribute(AClause.RANGES, eOld.getAttribute(AClause.RANGES));
		eNew.setAttribute(AClause.WIDTH, eOld.getAttribute(AClause.WIDTH));
		eNew.setAttribute(AClause.HEIGHT, eOld.getAttribute(AClause.HEIGHT));
		
		Element eParent=(Element) eOld.getParentNode();
		eParent.insertBefore(eNew, eOld);

		Composite cOld = (Composite) eOld.getUserData(AClause.ACLAUSE);
		Composite cParent = cOld.getParent();
		
		if (eNew.getNodeName()==AClause.CLAUSE)	new Clause(cParent,eNew);
		else if (eNew.getNodeName()==AClause.BRACKET)	new ClauseConditional(cParent,eNew);
		
		eParent.removeChild(eOld);
		cOld.setVisible(false);
		cParent.layout();
		sourceViewMap.eClicked=eNew;
	}
}
