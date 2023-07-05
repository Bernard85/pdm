package p.d.m;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class HUB {

	public static final String PROGRAM_SELECTION = "PROGRAM_SELECTION";
	
	public static Color COLOR_CYAN =Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	public static Color COLOR_GREEN =new Color (Display.getCurrent(), 230,255,237);
	public static Color COLOR_RED =Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	public static Color COLOR_PINK = new Color (Display.getCurrent(), 255,238,240);
	public static Color COLOR_WHITE =Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	public static Color COLOR_YELLOW =Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	public static Color COLOR_BLACK =Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	public static Color COLOR_GRAY =Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	public static Color COLOR_WIDGET_LIGHT_SHADOW=Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
	
	public static final String JOURNAL = "journal";
	public static final String ROWMAX="rowmax";
	public static final String COLMAX="colmax";
	public static final String COLUMN="column";
	public static final String COLUMN2="column";
	public static final String FORMULA="formula";
	public static final String TEXT="text";
	public static final String WIDTH="width";

}
