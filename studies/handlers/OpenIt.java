package studies.handlers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.viewers.TreeViewer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import core.HUB;
import studies.StudiesSetUp;

public class OpenIt {
	@Inject MApplication mApplication;
	@Inject EModelService eModelService;
	@Inject EPartService ePartService;
	@Inject IEclipseContext iEclipseContext;

	@CanExecute	public boolean canExecute (@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable {

		if (element.hasAttribute("open")) return !Boolean.valueOf(element.getAttribute("open"));

		return true;
	}

	@Execute public void openIt(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element element) throws Throwable{

		if (element.hasAttribute(HUB.XRL) && element.getUserData(HUB.RIGHT_ALIAS)==null) {

			try {
				Document dAlias=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(element.getAttribute(HUB.XRL));
				Element eAlias=dAlias.getDocumentElement();
				element.setUserData(HUB.RIGHT_ALIAS, eAlias, null);
				eAlias.setUserData(HUB.LEFT_ALIAS, element, null);
				element.setAttribute("open", "true");
				eAlias.setAttribute("open", "true");
				StudiesSetUp.refreshIt();
				StudiesSetUp.tStudies.expandToLevel(eAlias, TreeViewer.ALL_LEVELS);
			}
			catch(Exception e) {
				System.out.println("failure when open\""+element.getAttribute(HUB.XRL)+"\"");
			}
		}

		String partDescriptor[]=getPartDescriptor(element); 

		for (int p=0;p<partDescriptor.length;p++ ) {
			String sDetailStack="detailStack"+String.valueOf(p+1);
			MPartStack detailStack = (MPartStack) eModelService.find(sDetailStack, mApplication);
			detailStack.getParent().setVisible(true);
			String ID=element.getAttribute("ID");
			MPart part = (MPart) eModelService.find(ID, detailStack);
			if (part==null) {
				part = ePartService.createPart(partDescriptor[p]);
				part.setToBeRendered(true);
				part.setVisible(true);
				part.setElementId(ID);
				part.setLabel(ID);
				detailStack.getChildren().add(part);
			}
			detailStack.setSelectedElement(part);
		}

		if (element.hasAttribute("open")&&element.getAttribute("open")!="true") element.setAttribute("open", "true");

		StudiesSetUp.refreshIt();
	}

	private String[] getPartDescriptor(Element element) {
		/* */if (element.getNodeName()=="journalview") 	return new String[]{"JournalViewer"};
		else if (element.getNodeName()=="sourceview") 	return new String[]{"SourceEditor","SourceViewer"};
		else return new String[] {};
	}	
}
