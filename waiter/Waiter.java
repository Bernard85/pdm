package core;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Waiter {

	static String pathID = "C:\\lib\\pilote\\in\\";
	static Path path = Paths.get(pathID);
	static String[] data = new String[0];
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		final Table table = new Table(shell, SWT.VIRTUAL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setHeaderBackground(display.getSystemColor(SWT.COLOR_GRAY));
		String[] headers = {"files","Instant","Detail"}; 
		for (int h=0;h<headers.length;h++) {
			TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText(headers[h]);
			tc.pack();
		}

		table.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event e) {
				TableItem item = (TableItem) e.item;
				int index = table.indexOf(item);
				item.setText(data[index].split(";"));

				for (TableColumn tc :table.getColumns()) tc.pack();
				table.redraw();

			}
		});

		Thread thread = new Thread() {
			public void run() {
				try {WatchService watchService = FileSystems.getDefault().newWatchService();
				path.register(watchService,StandardWatchEventKinds.ENTRY_CREATE);
				WatchKey key= null;
				while ((key= watchService.take()) != null) {
					if (table.isDisposed()) return;
					for (WatchEvent event : key.pollEvents()) {
						String fileID=event.context().toString();
						String content = null;
						try {content= new String(Files.readAllBytes(Paths.get(pathID+fileID)));
						}catch (Throwable t) {}
						if (content!=null) {

							data = Arrays.copyOf(data,data.length+1);
							data[data.length-1]=fileID+";"+ dtf.format(LocalDateTime.now())+";"+content;

							display.syncExec(new Runnable() {
								public void run() {
									if (table.isDisposed())	return;
									table.setItemCount(data.length);
									table.clearAll();
								}
							});	
						}
					} 
					key.reset();
				}
				}catch (Throwable t) {System.out.println(t);};
			}
		};

		thread.start();
		shell.open();
		while (!shell.isDisposed() || thread.isAlive()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
