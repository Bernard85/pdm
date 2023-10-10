package sourceViewer.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sourceEditor.SourceViewMap;
import sourceViewer.AClause;
import sourceViewer.Clause;
import sourceViewer.ClauseConditional;

public class Insert {

	@Execute public void execute(@Named(IServiceConstants.ACTIVE_PART) 	MPart part2
			,@Named("TYPE")		                   	String type
			,@Named("DESTINATION")           		String destination
			,@Optional @Named(AClause.RANGES) 		String ranges
			,@Optional @Named(AClause.BLOCK) 		String blockBounds){

		SourceViewMap sourceViewMap = (SourceViewMap) part2.getTransientData().get(SourceViewMap.SOURCE_VIEW_MAP);

		Document dView=sourceViewMap.dView;

		Element eClicked=sourceViewMap.eClicked;
		AClause aClause = (AClause)eClicked.getUserData(AClause.ACLAUSE);
		Node nParent = eClicked.getParentNode();

		Element newElement=null;
		if (type.equals("clause")) {
			newElement = dView.createElement("clause");
			newElement.setAttribute("height", "64");
			newElement.setAttribute("width", "400");
		}
		else if (type.equals("clauseConditional")) {
			newElement = dView.createElement("bracket");
			newElement.setAttribute("heightcond", "30");
			newElement.setAttribute("height", "80");
			newElement.setAttribute("width", "400");
			newElement.setAttribute("expanded", "true");
		}
		Composite cParent=null;
		/* */if (destination.equals("after")) {
			nParent.insertBefore(newElement, eClicked.getNextSibling());
			cParent = ((AClause)eClicked.getUserData(AClause.ACLAUSE)).getParent();
		}
		else if (destination.equals("into")) {
			eClicked.insertBefore(newElement, eClicked.getFirstChild());
			cParent = ((ClauseConditional)eClicked.getUserData(AClause.ACLAUSE)).getClauses().getClauses2();
			}

		/* */if (type.equals("clause"))	{
			new Clause(cParent, newElement);
		}
		else if (type.equals("clauseConditional")) 	{
			new ClauseConditional(cParent, newElement);
		}
		cParent.layout();
	}
}
