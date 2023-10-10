package journalViewer;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.w3c.dom.Element;

import core.HUB;

public class Column {
		String text,formula;
		public int width, leftBorder, rightBorder;

		public JexlExpression expression;

		public Column(Element eColumn, JexlEngine jexl) {
			text=eColumn.getAttribute(HUB.TEXT);
			formula=eColumn.getAttribute(HUB.FORMULA);
			expression = jexl.createExpression(formula);
			width=Integer.parseInt(eColumn.getAttribute(HUB.WIDTH));
		}

		static void computeBorders(List<Column> columns) {
			int position=0;
			Iterator <Column> iColumns = columns.listIterator();
			while (iColumns.hasNext()) {
				Column column = iColumns.next(); 
				column.leftBorder=position;
				position+=column.width;
				column.rightBorder=position-1;
			}
		}

		static int getColumnAtPos(List<Column> columns, int position) {
			Iterator <Column> iColumns = columns.listIterator();
			for (int seq=0; iColumns.hasNext();seq++)	{
				Column column = iColumns.next();
				if (column.rightBorder>=position) return seq;
			}	
			return -1;
		}
		static int isOnBorder(List<Column> columns, int position) {
			final int precision=2;
			Iterator <Column> iColumns = columns.listIterator();
			for (int seq=0; iColumns.hasNext();seq++)	{
				Column column = iColumns.next();
				if ((position>column.rightBorder-precision)&&(position<column.rightBorder+precision)) return seq;
			}	
			return -1;
		}
}
