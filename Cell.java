package p.d.m.journalViewer;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;

public class Cell {
	String value;
	public Cell(JexlContext context, JexlExpression expression) {
		value = ((String) expression.evaluate(context)).intern();
	}
	
}
