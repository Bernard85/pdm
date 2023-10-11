package sourceViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Clauses2 extends Composite {
	Element element;
	Clauses2(Composite parent, Element element) {
		super(parent, SWT.NONE);
		this.element = element;
		setLayout(new Clauses2Layout());
		NodeList nl = element.getChildNodes();
		for (int n=0;n<nl.getLength();n++) {
			Node wN = nl.item(n);
			if (wN.getNodeType() == Node.ELEMENT_NODE) {
				/* */if (wN.getNodeName()=="bracket") 	new Expandable(this, (Element) wN);
				else if (wN.getNodeName()=="clause") 	new Clause(this, (Element) wN);
			}
		}

	}
	public Element getElement() {
	return element;
	}
}
