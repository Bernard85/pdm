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
	public static final int V_BORDER = 10;
	int x, y;
	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Clauses2 clauses2 = (Clauses2) composite;
		element = clauses2.getElement();		
		y=V_BORDER;
		NodeList childNodes=element.getChildNodes();
		
		for (int n=0;n<childNodes.getLength();n++) {
			Node node = childNodes.item(n);
			if (node.getNodeType() != Node.ELEMENT_NODE) continue;
			Element element2 = (Element) node;
			AClause aClause = (AClause)element2.getUserData(AClause.ACLAUSE);
			y+=aClause.getRealHeight();
			y+=V_BORDER;
		}
		x=clauses2.getClientArea().width;
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
			y+=aClause.getRealHeight();
		}
		int maximum = y-clauses2.getSize().y-slider.getThumb();
		slider.setMaximum(maximum<0?1:maximum);
		slider.setVisible(slider.getMaximum()>1);
	}	
}
