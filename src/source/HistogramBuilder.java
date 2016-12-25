package source;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

// Given a file and a histogram, populates the histogram
public class HistogramBuilder implements Runnable {
	static final String INTERVAL_PREFIX = "[";
	static final File resourcesDirectory = new File("resources");
	private String filename;
	private Histogram histogram;

	public HistogramBuilder(String filename, Histogram histogram) {
		super();
		this.filename = filename;
		this.histogram = histogram;
	}
	
	public void parseLine(String s) {
		if(s != null && s.length() > 0) {
			if(s.startsWith(INTERVAL_PREFIX)) {
				// Remove first & last char which are '[' and ')' 
				s = s.substring(1, s.length() - 1);
				List<String> boundPair = Arrays.asList(s.split(","));
				Interval i;
				
				try {
					i = new Interval(Double.parseDouble(boundPair.get(0)), Double.parseDouble(boundPair.get(1)));
					histogram.insertIntervalToHistogram(i);
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				} catch (InvalidIntervalBoundsException e) {
					System.err.println(e.getMessage());
				} catch (OverlappingIntervalException e) {
					System.err.println(e.getMessage());
				}
			}
			
			else {
				Double value = Double.parseDouble(s);
				histogram.insertValueToHistogram(value);
			}	
		}
	}

	public void populateHistogram() {
		File fileToBeRead = new File(resourcesDirectory.getAbsolutePath() + File.separator + filename);
		try(Stream<String> stream = Files.lines(Paths.get(fileToBeRead.toURI()))) {
			stream.forEach(line -> parseLine(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*Getters & Setters*/
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Histogram getHistogram() {
		return histogram;
	}
	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
	}
	
	public static File getResourcesdirectory() {
		return resourcesDirectory;
	}
	/**/
	
	@Override
	public void run() {
		populateHistogram();
	}
	
	public static void main(String[] args) throws InterruptedException {
		Histogram hist = new Histogram();
		List<Thread> allThreads = new ArrayList<Thread>();
		
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
		
		Thread.sleep(1000);
		hist.printHistogram();
		
	}
}
