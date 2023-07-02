package p.d.m.studies.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Element;

import p.d.m.studies.Studies;


public class OpenIt {
	@Inject EModelService eModelService;
	@Inject EPartService ePartService;
	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext;



	@CanExecute	public boolean canExecute (@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable {

		if (element.hasAttribute("open")) return !Boolean.valueOf(element.getAttribute("open"));

		return true;
	}

	@Execute public void openIt(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable{
		if (element.hasAttribute("open")) element.setAttribute("open", "true");	

		System.out.println("element.getNodeName()="+element.getNodeName());
		
		if ((element.getNodeName())=="program") {
			MPartStack detailStack = (MPartStack) eModelService.find("p.d.m.detailStack", mApplication);
			String ID=element.getAttribute("ID");
			MPart part = (MPart) eModelService.find(ID, detailStack);
			if (part==null) {

				part = MBasicFactory.INSTANCE.createPart();

				part.setContributionURI("bundleclass://p.d.m/"+element.getAttribute("class"));

				part.setToBeRendered(true);
				part.setVisible(true);
				part.setElementId(ID);
				part.setLabel(ID);
				part.getTransientData().put("ELEMENT", element);
				detailStack.getChildren().add(part);
				ePartService = iEclipseContext.get(EPartService.class);
				ePartService.activate(part);
				ePartService.showPart(part, PartState.ACTIVATE);
			}
			detailStack.setSelectedElement(part);
		}

		Studies.refreshIt();
	}	
}
