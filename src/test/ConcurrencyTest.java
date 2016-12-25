package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import source.Histogram;
import source.HistogramBuilder;

public class ConcurrencyTest {
	Histogram hist = new Histogram();
	List<Thread> allThreads = new ArrayList<Thread>();
	static final File resourcesDirectory = new File("resources");
	
	@Test
	public void concurrentTest() throws InterruptedException {
		try(Stream<Path> paths = Files.walk(Paths.get(resourcesDirectory.toURI()))) {
			paths.forEach( filePath -> {
				if(Files.isReadable(filePath) && !(Files.isDirectory(filePath))) {
					String filename = filePath.getFileName().toString();
					Thread t = new Thread(new HistogramBuilder(filename, hist), "Thread: " + filename);
					allThreads.add(t);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Thread t : allThreads) {
			t.start();
		}
		
		hist.printHistogram();
		Thread.sleep(10);
		hist.printHistogram();
		Thread.sleep(10);
		hist.printHistogram();
	}
}
