package p.d.m.journalViewer;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;
import org.eclipse.swt.graphics.Color;

import p.d.m.HUB;

public class Cell {
	String value;
	public Color backGround=HUB.COLOR_WHITE;
	public Cell(JexlContext context, JexlExpression expression) {
		value = ((String) expression.evaluate(context)).intern();
	}
	
}
