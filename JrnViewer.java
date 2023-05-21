package p.d.m.journalViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import p.d.m.HUB;

public class JrnViewer extends Composite {
	public static JexlEngine jexl = new JexlBuilder().cache(512).strict(true).silent(false).create();
	JexlExpression isDeleteRow = jexl.createExpression("bean7.joentt.equals('DL')");
	JexlContext values = new MapContext();

	IMessageBox messageBox;
	IModifierNotifier modifierNotifier;

	final int rowHeight = 20,headerHeight=rowHeight*2,margin = 5;
	ScrollBar hb,vb;
	int rowMax=0,colMax=0,col1,col9,row1,row9;
	int columnOnMouseDown, positionOnMouseDown, positionCurrent;
	boolean resize;

	List<Column> columns=new ArrayList<>();
	ListIterator<Column> iColumns; 

	List<Row> rows = new ArrayList<>();
	ListIterator<Row> iRows; 

	List<Field> fields=new ArrayList<>();
	ListIterator<Field> iFields;

	Point selectedCell=new Point(0, 0);

	public JrnViewer(Composite parent) {
		super(parent, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL|SWT.DOUBLE_BUFFERED|SWT.SMOOTH);

		setUpBars();
		setUpPainters();
		setUpMouseListeners();		
		setUpKeyListener();
		setUpResizeListener();

		redraw();
	}
	public void setColumns(String fileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dProperties = db.parse(new File(fileName));

			Element eProperties=dProperties.getDocumentElement();

			colMax=Integer.parseInt((eProperties.getAttribute(HUB.COLMAX)));
			rowMax=Integer.parseInt((eProperties.getAttribute(HUB.ROWMAX)));

			NodeList nColumns = eProperties.getChildNodes();

			for (int n=0;n<nColumns.getLength();n++) {
				Node nColumn = nColumns.item(n);
				if (nColumn.getNodeType()!=Node.ELEMENT_NODE) continue;
				Element eColumn = (Element)nColumn;
				columns.add(new Column(eColumn, jexl));
			}
			Column.computeBorders(columns);
		} catch (Throwable e){
			System.out.println("set columns failure");
		}	
	}
	public void setFields(String fileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dFormat = db.parse(new File(fileName));

			Element eFormat=dFormat.getDocumentElement();

			NodeList nFields = eFormat.getChildNodes();

			for (int n=0;n<nFields.getLength();n++) {
				if (nFields.item(n).getNodeType()==Node.ELEMENT_NODE) {
					Node nField = nFields.item(n);
					Element eField = (Element) nField;
					fields.add(new Field(eField));
				}
			}
		} catch (Throwable e){
			System.out.println("set format(fields) failure");
		}	
	}
	public void setData(String fileName) {
		try {
			BufferedReader	br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
			
				///List<Cell> cells = getCells(line);
				rows.add(new Row(line));
				
			}
			br.close();
		} catch (Throwable e) {
			System.out.println("Load Data failed");
		}

	}

	public void setModifierNotifier (IModifierNotifier modifierNotifier) {
		this.modifierNotifier = modifierNotifier;
	}
	private void setUpBars() {
		hb = getHorizontalBar();
		hb.setMinimum(0);
		hb.setSelection(0);
		hb.setIncrement(1);
		hb.setThumb(1);
		hb.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				col1=Column.getColumnAtPos(columns, hb.getSelection());
				col9=Column.getColumnAtPos(columns, hb.getSelection()+getClientArea().width);
				messageBox.send("scroll position:"+hb.getSelection()+" - first/last column:"+col1+"/"+col9);
				redraw();
			}
		});
		hb.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				iColumns=columns.listIterator(col1);
				Column column = iColumns.next();
				if (e.detail==SWT.ARROW_DOWN) {
					int x =column.rightBorder;
					hb.setSelection(x);
				}
				else if (e.detail==SWT.ARROW_UP) {
					int x =column.leftBorder;
					hb.setSelection(x);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		vb = getVerticalBar();
		vb.setMinimum(0);
		vb.setIncrement(1);
		vb.setThumb(1);		
		vb.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				row1=vb.getSelection();
				row9=row1-1+(getClientArea().height-headerHeight)/rowHeight;

				messageBox.send("first/last row:"+row1+'/'+row9);

				redraw();
			}

		});
	}
	private void setUpPainters()   {

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Image image = new Image(Display.getCurrent(), e.width, rowHeight); 
				GC gc = new GC(image);

				int rowX = row1;
				iRows = rows.listIterator(row1);


				while ((rowX<=row9) && (iRows.hasNext())) {
					Row row = (Row) iRows.next();
					int colX=col1;
					List <Cell> cells = getCells((String)row.ujo); 
					Iterator <Cell> iCells= cells.listIterator(col1);
					iColumns= columns.listIterator(col1);
					while (colX<=col9) {
						Column column = iColumns.next();
						Cell cell = iCells.next();
						String string = cell.value;
						Point requiredSize = e.gc.textExtent(string);
						gc.fillRectangle(image.getBounds());
						gc.drawString(string, 10, (rowHeight-requiredSize.y)/2);
						e.gc.drawImage(image, column.leftBorder-hb.getSelection()-1, (rowX-row1)*rowHeight+headerHeight);

						colX++;
					}
					rowX++;
				}
				image.dispose();

				// header
				e.gc.setBackground(HUB.COLOR_YELLOW);
				e.gc.fillRectangle(e.x,e.y,e.width,headerHeight);
				Font font = new Font(Display.getCurrent(),"Courier New",8,SWT.NORMAL);

				iColumns= columns.listIterator(col1);
				for (int col=col1;col<=col9;col++)
				{
					Column column = iColumns.next();
					e.gc.drawLine(column.rightBorder-hb.getSelection()-1, 0, column.rightBorder-hb.getSelection()-1, headerHeight+(row9-row1+1)*rowHeight);
					String string = column.text;
					Point requiredSize = e.gc.textExtent(string);
					e.gc.drawString(string, column.leftBorder-hb.getSelection()+margin, (headerHeight-requiredSize.y)/2);
					e.gc.drawLine(column.leftBorder-hb.getSelection(),headerHeight-1,column.rightBorder-hb.getSelection(),headerHeight-1);
				}

				font.dispose();

				if (resize){
					e.gc.setLineWidth(1);
					e.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					e.gc.drawLine(positionCurrent,0,positionCurrent, headerHeight+(row9-row1+1)*rowHeight);
				}

				// selected cell
				if ((selectedCell.x <= col9) &&(selectedCell.x >= col1)&&(selectedCell.y <= row9) &&(selectedCell.y >= row1)) {
					e.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
					e.gc.setLineWidth(2);
					iColumns= columns.listIterator(selectedCell.x);
					Column column=iColumns.next();
					e.gc.drawRectangle(
							column.leftBorder-hb.getSelection()-1
							,headerHeight+(selectedCell.y-vb.getSelection())*rowHeight							
							,column.width-1
							,rowHeight-1
							);
				}
			}
		});
	}
	private List<Cell> getCells(String line){
		List<String> strings = Arrays.asList(line.split("\t"));
		ListIterator<String> iStrings = strings.listIterator(); 
		// load map for each field
		iFields = fields.listIterator(); 
		while (iFields.hasNext()) {
			Field field=iFields.next();
			String string=iStrings.next();
			values.set(field.id, string);
		}
		// load each cell 
		List<Cell>cells=new ArrayList<Cell>();
		iColumns = columns.listIterator();
		while (iColumns.hasNext()) {
			Column column = iColumns.next();
			cells.add(new Cell(values, column.expression));
		}
		return cells;
	}
	private void setUpMouseListeners() {
		addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				int ap=e.x+hb.getSelection();
				if ((e.y<headerHeight) && (columnOnMouseDown=Column.isOnBorder(columns, ap))>-1) {
					positionOnMouseDown=e.x;
					positionCurrent=e.x;
					Cursor cursor = new Cursor(Display.getCurrent(), SWT.CURSOR_SIZEWE);
					setCursor(cursor);
					cursor.dispose();
					resize=true;
					modifierNotifier.setDirty(true);
					redraw();
				}
				else if (e.y>headerHeight) {
					selectedCell.y=vb.getSelection()+(e.y-headerHeight)/rowHeight;

					selectedCell.x=Column.getColumnAtPos(columns,e.x+hb.getSelection());
					messageBox.send("selected cell: "+selectedCell.y+"/"+selectedCell.x);
				}
				else {
					setCursor(null);
				}
			}
		});

		addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				int ap=e.x+hb.getSelection();
				if (((e.y<headerHeight) && (Column.isOnBorder(columns,ap)>-1)) || (resize)) {
					Cursor cursor = new Cursor(Display.getCurrent(), SWT.CURSOR_SIZEWE);
					setCursor(cursor);
					positionCurrent=e.x;
					if (resize) {
						redraw();
					}
					cursor.dispose();
				}
				else {
					setCursor(null);
				}
			}
		});
		addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				if (resize){
					int delta=positionCurrent-positionOnMouseDown;
					iColumns = columns.listIterator(columnOnMouseDown);
					Column column=iColumns.next();
					column.width+=delta;
					hb.setMaximum(hb.getMaximum()+delta);
					Column.computeBorders(columns);
					col1=Column.getColumnAtPos(columns, hb.getSelection());
					col9=Column.getColumnAtPos(columns, hb.getSelection()+getClientArea().width);
				}
				positionOnMouseDown=-1;
				resize=false;
				redraw();
				setCursor(null);
			}
		});

		addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event event) {
				positionOnMouseDown=-1;
				resize=false;
			}
		});

	}
	private void setUpKeyListener() {
		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e)
			{
				if ((e.keyCode==SWT.ARROW_DOWN)&&(selectedCell.y<rowMax)) selectedCell.y+=1;
				else if ((e.keyCode==SWT.ARROW_UP)&&(selectedCell.y>0)) selectedCell.y-=1;
				else if ((e.keyCode==SWT.ARROW_RIGHT)&&(selectedCell.x<columns.size())) selectedCell.x+=1;
				else if ((e.keyCode==SWT.ARROW_LEFT)&&(selectedCell.x>0)) selectedCell.x-=1;
				else if (e.keyCode==SWT.ESC) {selectedCell.x=-1; selectedCell.y=-1;} 
				redraw();
				messageBox.send("selected cell: "+selectedCell.y+"/"+selectedCell.x);
			}

		});
	}
	private void setUpResizeListener() {
		addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				iColumns= columns.listIterator(columns.size()-1);
				Column column = iColumns.next();
				hb.setMaximum(1+column.rightBorder-getClientArea().width);
				col1=Column.getColumnAtPos(columns, hb.getSelection());
				col9=Column.getColumnAtPos(columns, hb.getSelection()+getClientArea().width);
				messageBox.send("first/last column: "+col1+'/'+col9);

				vb.setMaximum(1+rowMax-(getClientArea().height-headerHeight)/rowHeight);
				row1=vb.getSelection();
				row9=row1-1+(getClientArea().height-headerHeight)/rowHeight;
				messageBox.send("first/last row: "+row1+'/'+row9);
			}
		});
	}
	public void setMessageBox(IMessageBox messageBox) {
		this.messageBox= messageBox;
	}
	public void saveIt(String fileName)  {
		try {
			Document dJournal=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element eJournal = dJournal.createElement(HUB.JOURNAL); 
			eJournal.setAttribute(HUB.ROWMAX, String.valueOf(rowMax));
			eJournal.setAttribute(HUB.COLMAX, String.valueOf(colMax));
			dJournal.appendChild(eJournal);
			iColumns=columns.listIterator();
			while (iColumns.hasNext()){
				Column column= iColumns.next();
				Element eColumn=dJournal.createElement(HUB.COLUMN2);
				eColumn.setAttribute(HUB.WIDTH, String.valueOf(column.width));
				eColumn.setAttribute(HUB.TEXT, String.valueOf(column.text));
				eColumn.setAttribute(HUB.FORMULA, String.valueOf(column.formula));
				eJournal.appendChild(eColumn);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			DOMSource source = new DOMSource(dJournal);

			FileOutputStream fos = new FileOutputStream(fileName);
			StreamResult result =new StreamResult(fos);
			transformer.transform(source, result);
			modifierNotifier.setDirty(false);
		}  catch (Throwable e){
			System.out.println("save failure");
		}	
	}
}


