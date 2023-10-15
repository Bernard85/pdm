package sourceEditor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Element;

public class SourceBlock implements Comparator<SourceBlock> {
	Rectangle rect;
	Element e;

	public SourceBlock(Element e) {

		try {String sRectangle = e.getAttribute("block");
			String[] c = sRectangle.split(",");

			int x=Integer.parseInt(c[0]);
			int y=Integer.parseInt(c[1]);
			int w=Integer.parseInt(c[2]);
			int h=Integer.parseInt(c[3]);

			rect  =  new Rectangle(x,y,w,h);
			}
		catch (Exception exception ) {
			System.out.println("block loading failure : "+e.getAttribute("block"));
		}

		this.e = e;
	}
	public boolean isSelected() {
		SourceViewMap sourceViewMap = SourceViewMap.getSourceViewMap(e);
		return (e==sourceViewMap.eSelected);
	}
	public int compareTo(Rectangle other) {
		return (other.intersects(rect))?0:(other.y<rect.y)?-1:1;
	}

	public static SourceBlock getSourceBlock(List<SourceBlock> sourceBlocks, Point p) {
		Iterator<SourceBlock> iSourceBlocks=sourceBlocks.iterator();
		while(iSourceBlocks.hasNext()){
			SourceBlock sourceBlock = iSourceBlocks.next();
			if (sourceBlock.rect.contains(p)) return sourceBlock;
		}
		return null;
	}

	
	
	@Override
	public int compare(SourceBlock sb1, SourceBlock sb2) {

		/* */if (sb1.rect.y<sb2.rect.y) return -1;
		else if	(sb1.rect.y>sb2.rect.y) return 1;

		/* */if (sb1.rect.x<sb2.rect.x) return -1;
		else if	(sb1.rect.x>sb2.rect.x) return 1;

		/* */if (sb1.rect.height <sb2.rect.height) return -1;
		else if	(sb1.rect.height>sb2.rect.height) return 1;

		/* */if (sb1.rect.width <sb2.rect.width) return -1;
		else if	(sb1.rect.width>sb2.rect.width) return 1;

		return 0;
	}
}