package sourceViewer;

import java.util.regex.Pattern;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

import sourceEditor.SourceViewMap;

public class ClauseText extends StyledText {
	Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	AClause aClause;
	Boolean userInputEnabled=false;
	public ClauseText(AClause aClause) {
		super(aClause, SWT.V_SCROLL);
		setAlwaysShowScrollBars(false);
		this.aClause = aClause;
		addPaintListener( e-> {
			if (aClause.isSelected()) {
				e.gc.setForeground(red);
				e.gc.drawRectangle(e.x,e.y,e.width-1,e.height-1);
			}
			if (aClause.isTagged()) {
				e.gc.setBackground (red);
				e.gc.fillPolygon(new int[] {e.width-7,0,e.width,0,e.width,7});
			}
		});
		addListener(SWT.MouseDoubleClick, e->{
			aClause.setSelected();
		}); 
		addMenuDetectListener(new MenuDetectListener () {
			@Override
			public void menuDetected(MenuDetectEvent e) {
				ClauseText c = (ClauseText) e.widget;
				AClause aClause = (AClause) c.getParent();
				Element element = aClause.element;
				SourceViewMap sourceViewMap= (SourceViewMap) element.getOwnerDocument().getUserData(SourceViewMap.SOURCE_VIEW_MAP);
				sourceViewMap.eClicked=element;
			}
		});
	



		//		addVerifyListener(e -> {
		//			if (userInputEnabled)	{
		//				parent.getElement().setAttribute("userinput",parent.getElement().getAttribute("userinput")+e.text+":");
		//				parent.getElement().setAttribute("userinputstart",parent.getElement().getAttribute("userinputstart")+e.start+":");
		//				parent.getElement().setAttribute("userinputend",parent.getElement().getAttribute("userinputend")+e.end+":");
		//			}
		//		});
		loadText();
		setUserInputEnabled(true);

		EMenuService eMenuService = (EMenuService) aClause.element.getOwnerDocument().getUserData("EMenuService");
		eMenuService.registerContextMenu(this, "SourceViewer.popupmenu");
	}

	public void setUserInputEnabled(Boolean userInputEnabled) {
		this.userInputEnabled = userInputEnabled;
	}
	public void loadText() {

		/// Chiko Khaloucha
		//           012345678 
		String text="lancier\nLancier\nLancier";
		setText(text);
		///setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY)); 
		StyleRange[] style = new StyleRange[1];
		style[0]= new StyleRange();
		style[0].background=Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		int[] ranges = new int[] { 10, 10};
		setStyleRanges(ranges,style);

		if (!aClause.getElement().hasAttribute("userinput")) return;

		String userInput[] =aClause.getElement().getAttribute("userinput").split(Pattern.quote(":"),-1);
		String userInputstart[] =aClause.getElement().getAttribute("userinputstart").split(Pattern.quote(":"));
		String userInputend[] =aClause.getElement().getAttribute("userinputend").split(Pattern.quote(":"));

		for (int n=0;n<userInputstart.length;n++) {


			text=text.substring(0,Integer.parseInt(userInputstart[n]))
					+userInput[n]
							+text.substring(Integer.parseInt(userInputend[n]));

		}

		setText(text);

	}
}
