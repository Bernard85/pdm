package sourceViewer.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sourceEditor.SourceViewMap;
import sourceViewer.AClause;
import sourceViewer.Clause;
import sourceViewer.Expandable;

public class Insert {
	
	@CanExecute public boolean canExecute(
			 @Named(IServiceConstants.ACTIVE_PART) 	MPart part
			,@Named("TYPE")		                   	String type
			,@Named("DESTINATION")           		String destination
			,@Optional @Named(AClause.RANGES) 		String ranges
			,@Optional @Named(AClause.BLOCK) 		String blockBounds){
		
		SourceViewMap sourceViewMap = SourceViewMap.getSourceViewMap(part);
		
		Element eChosen=sourceViewMap.eChosen;

		return !(eChosen.getNodeName().equals("clause")&&destination.equals("into"));
	}
	
	
	@Execute public void execute(@Named(IServiceConstants.ACTIVE_PART) 	MPart part
			,@Named("TYPE")		                   	String type
			,@Named("DESTINATION")           		String destination
			,@Optional @Named(AClause.RANGES) 		int[] ranges
			,@Optional @Named(AClause.BLOCK) 		Rectangle blockBounds){

		SourceViewMap sourceViewMap=SourceViewMap.getSourceViewMap(part); 

		Document dView=sourceViewMap.dView;

		Element eChosen=sourceViewMap.eChosen;
		Node nParent = eChosen.getParentNode();

		Element newElement=null;
		
		int height = (ranges!=null)?blockBounds.height+6:20;
		
		if (type.equals(AClause.CLAUSE)) {
			newElement = dView.createElement(AClause.CLAUSE);
			newElement.setAttribute(AClause.HEIGHT, String.valueOf(height));
			newElement.setAttribute(AClause.WIDTH, "400");
		}
		else if (type.equals(AClause.EXPANDABLE)) {
			newElement = dView.createElement(AClause.EXPANDABLE);
			int heightCond= height;
			height*=2;
			newElement.setAttribute(AClause.HEIGHT, String.valueOf(height));
			newElement.setAttribute(AClause.HEIGHTCOND, String.valueOf(heightCond));
			newElement.setAttribute(AClause.WIDTH, "400");
			newElement.setAttribute(AClause.EXPANDED, "true");
		}
		Composite cParent=null;
		/* */if (destination.equals("after")) {
			nParent.insertBefore(newElement, eChosen.getNextSibling());
			cParent = ((AClause)eChosen.getUserData(AClause.ACLAUSE)).getParent();
		}
		else if (destination.equals("into")) {
			eChosen.insertBefore(newElement, eChosen.getFirstChild());
			cParent = ((Expandable)eChosen.getUserData(AClause.ACLAUSE)).getClauses().getClauses2();
		}
		if (ranges!=null) {
			
			String sRanges = "";
			for (int i=0;i<ranges.length;i++) {
				if (sRanges!="") sRanges+=",";
				sRanges+=String.valueOf(ranges[i]);
			}
			newElement.setAttribute(AClause.RANGES, sRanges);
			
			String sBlockBounds = blockBounds.x+","+blockBounds.y+","+blockBounds.width+","+blockBounds.height;
			
			newElement.setAttribute(AClause.BLOCK, sBlockBounds);
		}
		/* */if (type.equals(AClause.CLAUSE))	{
			new Clause(cParent, newElement);
		}
		else if (type.equals(AClause.EXPANDABLE)) 	{
			new Expandable(cParent, newElement);
		}

		sourceViewMap.eNewSelected=newElement;

		sourceViewMap.loadSourceBlocks();

		SelectIt.selectIt(sourceViewMap);
		cParent.layout();
	}
}
