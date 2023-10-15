package sourceViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import sourceEditor.SourceViewMap;

public abstract class AClause extends Composite {
	public static final String CLAUSE = "clause";
	public static final String BRACKET = "bracket";
	public static final String EXPANDED = "expanded";
	public static final String RANGES = "ranges";
	public static final String BLOCK = "block";
	public static final String ACLAUSE = "ACLAUSE";
	public static final String NO = "NO";
	public static final String HEIGHT = "height";
	public static final String HEIGHTCOND = "heightcond";
	public static final String WIDTH = "width";
	protected Element element;
	ClauseText clauseText;
	
	IModifierNotifier modifierNotifier;
	
	public AClause(Composite clauses2, Element element) {
		super(clauses2, SWT.NONE);
		this.element=element;
		element.setUserData(ACLAUSE, this, null);
		SourceViewMap sourceViewMap=(SourceViewMap) element.getOwnerDocument().getUserData(SourceViewMap.SOURCE_VIEW_MAP);
		modifierNotifier=sourceViewMap.modifierNotifier;
		clauseText = new ClauseText(this);
		update();
	}
	public Element getElement() {
		return element;
	}
	public boolean isTagged() {
		return element.hasAttribute("tag");
	}
	public void switchTag() {
		modifierNotifier.setDirty(true);
		if (element.hasAttribute("tag")) 
			element.removeAttribute("tag"); 
		else 
			element.setAttribute("tag", "true");
	}
	
	public boolean isSelected() {
		return element==SourceViewMap.getSourceViewMap(element).eSelected;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return Integer.valueOf(element.getAttribute("width"));
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		element.setAttribute("width",String.valueOf(width));
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return Integer.valueOf(element.getAttribute("height"));
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		element.setAttribute("height",String.valueOf(height));
	}
	protected abstract int getRealWidth();
	protected abstract int getRealHeight();
	public void redraw() {
		clauseText.setSelection(clauseText.getCaretOffset(),clauseText.getCaretOffset());
		clauseText.redraw();
	}
	public void setSelected() {
		modifierNotifier.setDirty(true);
		Element element0 =(Element)element.getOwnerDocument().getUserData("SELECTED");
		if (element0!=null) {
			AClause aClause0 = (AClause) element0.getUserData(AClause.ACLAUSE);
			aClause0.redraw();
		}
		element.getOwnerDocument().setUserData("SELECTED", element, null);
		SourceViewMap sourceViewMap = (SourceViewMap) element.getOwnerDocument().getUserData(SourceViewMap.SOURCE_VIEW_MAP);
		sourceViewMap.eSelected=element;
		sourceViewMap.styledText.redraw();
		redraw();
	}

	public void update() {
		SourceViewMap sourceViewMap=(SourceViewMap)element.getOwnerDocument().getUserData(SourceViewMap.SOURCE_VIEW_MAP);
		String sClauseText="";
		int[]ranges=StringToInts(element.getAttribute(RANGES));
		for (int i=0;i<ranges.length;i=i+2) {
			if (!sClauseText.isEmpty()) sClauseText+="\n";
			sClauseText+=sourceViewMap.buffer.substring(ranges[i], ranges[i]+ranges[i+1]);
		}
		clauseText.setText(sClauseText);
	}
	public int[] StringToInts(String ids) {
		  if (ids.isEmpty()) return new int[0]; 	
	      String[] temps = ids.split(",");
	      int[] ia = new int[temps.length];
	      for (int i = 0; i < temps.length; i++) {
	            ia[i] = Integer.parseInt(temps[i]);
	        }
	       return ia;
	   } 
	public static AClause getAClause(Element element) {
		return (AClause) element.getUserData(ACLAUSE);
	}
}
