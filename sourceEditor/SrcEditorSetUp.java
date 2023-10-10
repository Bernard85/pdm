package sourceEditor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

import sourceViewer.AClause;

public class SrcEditorSetUp {
	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext; 
	@Inject EModelService eModelService;
	@Inject EMenuService eMenuService;
	@Inject EHandlerService eHandlerService;
	@Inject ECommandService eCommandService;

	String FILE_NAME;
	StyledText styledText;
	Font couriewNew;

	@PostConstruct public void postConstruct(MPart part1, Composite parent, @Named(IServiceConstants.ACTIVE_SELECTION) Element eStudy) {
		SourceViewMap sourceViewMap=new SourceViewMap();
		part1.getTransientData().put(SourceViewMap.SOURCE_VIEW_MAP, sourceViewMap);
		eStudy.setUserData(SourceViewMap.SOURCE_VIEW_MAP, sourceViewMap, null);
		sourceViewMap.eStudy=eStudy;
		
		parent.setLayout(new FormLayout());
		
		styledText = new StyledText(parent, SWT.BORDER|SWT.V_SCROLL);
		FormData fd = new FormData();
		fd.left= new FormAttachment(0);
		fd.top= new FormAttachment(0);
		fd.right= new FormAttachment(100);
		fd.bottom= new FormAttachment(100);
		styledText.setLayoutData(fd);
		
		styledText.setEditable(false);
		styledText.setBlockSelection(true);
		
		sourceViewMap.fileName=eStudy.getAttribute("arg1");

		try {sourceViewMap.buffer = new String(Files.readAllBytes(Paths.get(sourceViewMap.fileName)));
		} catch (IOException e)
		{
			System.out.println("failure on opening the file \"" + FILE_NAME+"\"");
			return;
		}
		styledText.setText(sourceViewMap.buffer);
		styledText.setFont(createTextFont(parent));

		parent.layout();

		eMenuService.registerContextMenu(styledText, "SourceEditor.PopupMenu");

		addListeners(styledText);
	}

	private void addListeners(StyledText styledText) {
		styledText.addMenuDetectListener(new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent e) {
				int[] ranges=styledText.getSelectionRanges();
				String sRanges = "";
				for (int i=0;i<ranges.length;i++) {
					if (sRanges!="") sRanges+=",";
					sRanges+=String.valueOf(ranges[i]);
				}
				iEclipseContext.set(AClause.RANGES, sRanges);
				
				Rectangle block = styledText.getBlockSelectionBounds();
				String sBlock = block.x+","+block.y+","+block.width+","+block.height;
				iEclipseContext.set(AClause.BLOCK, sBlock);
			}
		});
	}

	Font createTextFont(Composite parent) {
		final Font newFont = new Font(Display.getCurrent(),"Courier New",12,SWT.NORMAL);
		parent.addDisposeListener(e ->{
			if(!newFont.isDisposed()) {
				newFont.dispose();	
			}
		}); 
		return newFont;
	}
}
