package journalViewer;

import org.w3c.dom.Element;

public class Field {
	String id,text;
	public Field(Element eField) {
		this.id=eField.getAttribute("ID");
		this.text=eField.getAttribute("text");
		} 
}
