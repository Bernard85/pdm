package sourceEditor.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.graphics.Rectangle;

import sourceEditor.SourceBlock;
import sourceEditor.SourceViewMap;
import sourceViewer.AClause;

public class UpdateIt {

	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext;
	@Inject EModelService eModelService;
	@Inject EPartService ePartService;
	
	@Execute public void updateIt(@Named(IServiceConstants.ACTIVE_PART) MPart part1) throws Throwable {
		SourceViewMap sourceViewMap =SourceViewMap.getSourceViewMap(part1); 
		sourceViewMap.modifierNotifier.setDirty(true);

		int[] ranges = (int[]) iEclipseContext.get(AClause.RANGES);
		String sRanges = "";
		for (int i=0;i<ranges.length;i++) {
			if (sRanges!="") sRanges+=",";
			sRanges+=String.valueOf(ranges[i]);
		}

		Rectangle block = (Rectangle) iEclipseContext.get(AClause.BLOCK);
		String sBlock = block.x+","+block.y+","+block.width+","+block.height;

		sourceViewMap.eSelected.setAttribute(AClause.RANGES, sRanges);
		sourceViewMap.eSelected.setAttribute(AClause.BLOCK, sBlock);

		AClause aClause = (AClause)sourceViewMap.eSelected.getUserData(AClause.ACLAUSE);
		aClause.update();
		
		sourceViewMap.sourceBlocks.add(new SourceBlock(sourceViewMap.eSelected));
	}
}
