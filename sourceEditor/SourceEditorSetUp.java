package sourceEditor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

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
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

import sourceViewer.AClause;
import sourceViewer.handlers.SelectIt;

public class SourceEditorSetUp {
	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext; 
	@Inject EModelService eModelService;
	@Inject EMenuService eMenuService;
	@Inject EHandlerService eHandlerService;
	@Inject ECommandService eCommandService;

	String FILE_NAME;
	StyledText styledText;
	Font couriewNew;
	SourceViewMap sourceViewMap;

	@PostConstruct public void postConstruct(MPart part1, Composite parent, @Named(IServiceConstants.ACTIVE_SELECTION) Element eStudy) {
		sourceViewMap=new SourceViewMap();
		part1.getTransientData().put(SourceViewMap.SOURCE_VIEW_MAP, sourceViewMap);
		eStudy.setUserData(SourceViewMap.SOURCE_VIEW_MAP, sourceViewMap, null);
		sourceViewMap.eStudy=eStudy;
		sourceViewMap.styledText=styledText;

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
		styledText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		sourceViewMap.fileName=eStudy.getAttribute("arg1");
		sourceViewMap.styledText=styledText;
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
				
				sourceViewMap.eChosen=sourceViewMap.eSelected;
				
				int[] ranges=styledText.getSelectionRanges();
				String sRanges = "";
				for (int i=0;i<ranges.length;i++) {
					if (sRanges!="") sRanges+=",";
					sRanges+=String.valueOf(ranges[i]);
				}
				iEclipseContext.set(AClause.RANGES, ranges);

				Rectangle block = styledText.getBlockSelectionBounds();
				String sBlock = block.x+","+block.y+","+block.width+","+block.height;
				
				iEclipseContext.set(AClause.BLOCK, block);
			}
		});

		styledText.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {

				Iterator<SourceBlock> iSourceBlock = sourceViewMap.sourceBlocks.iterator();
				while(iSourceBlock.hasNext()) {
					SourceBlock sourceBlock = iSourceBlock.next();
					Rectangle rect=sourceBlock.rect;
					e.gc.setForeground((sourceBlock.isSelected())?Display.getCurrent().getSystemColor(SWT.COLOR_RED):Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					e.gc.drawRectangle(rect.x,rect.y+1-styledText.getVerticalBar().getSelection(),rect.width,rect.height-2);
				}
			}
		});

		styledText.addLineBackgroundListener(new LineBackgroundListener() {

			@Override
			public void lineGetBackground(LineBackgroundEvent e) {
				Iterator<SourceBlock> iSourceBlock = sourceViewMap.sourceBlocks.iterator();
				
				int y=styledText.getLinePixel(styledText.getLineAtOffset(e.lineOffset));
				y+=styledText.getVerticalBar().getSelection();
				
				while(iSourceBlock.hasNext()) {
					SourceBlock sourceBlock = iSourceBlock.next();
					
					if (y>=sourceBlock.rect.y&&y<sourceBlock.rect.y+sourceBlock.rect.height) {
						e.lineBackground = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
						return;}
				}
			}

		});

		styledText.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				SourceBlock sourceBlock= SourceBlock.getSourceBlock(sourceViewMap.sourceBlocks, new Point(e.x,e.y+styledText.getVerticalBar().getSelection()));
				if(sourceBlock!=null) {
					sourceViewMap.eNewSelected=sourceBlock.e;
					SelectIt.selectIt(sourceViewMap);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
			
		}) ;
		
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
