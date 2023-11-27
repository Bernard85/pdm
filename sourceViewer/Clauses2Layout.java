package sourceViewer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Slider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Clauses2Layout extends Layout {
	Element element;
	public static final int V_BORDER = 8;
	int x, y;
	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		x=composite.getClientArea().width;
		return new Point(x,y);
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		Clauses2 clauses2 = (Clauses2) composite;
		Clauses clauses=(Clauses) clauses2.getParent();
		Slider slider = clauses.slider;
		element = clauses2.getElement();

		y=0;
		NodeList childNodes=element.getChildNodes();
		
		for (int n=0;n<childNodes.getLength();n++) {
			Node node = childNodes.item(n);
			if (node.getNodeType() != Node.ELEMENT_NODE) continue;
			y+=(y>0)?V_BORDER:0;
			Element element2 = (Element) node;
			
			AClause aClause = (AClause)element2.getUserData(AClause.ACLAUSE);
			aClause.setBounds(0, y-slider.getSelection(), aClause.getRealWidth(), aClause.getRealHeight());
			
			if (element2.getNodeName().equals(AClause.CLAUSE)) {
				Clause clause = (Clause) aClause;
				clause.h1.setBounds(0, y-slider.getSelection()+aClause.getRealHeight(), clause.getRealWidth(), 3);
				clause.v1.setBounds(aClause.getRealWidth(), y-slider.getSelection(), 3, clause.getRealHeight());
			}
			else if (element2.getNodeName().equals(AClause.EXPANDABLE)) {
				Expandable expandable = (Expandable) aClause;
				expandable.h1.setBounds(0, y-slider.getSelection()+aClause.getRealHeight(), expandable.getWidth(), 3);
			}
			y+=aClause.getRealHeight();
		}
		slider.setMaximum(y);
		
		slider.setThumb(clauses.getClientArea().height);
		
		slider.setVisible(y>clauses.getClientArea().height); 
		
		if (!slider.isVisible()) {
			slider.setSelection(0);
			this.layout(composite, true);
		}
		
	}	
}
