package source;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;


public class Histogram {

	public Histogram() {
		super();
		this.histogramSum = new DoubleAdder();
		this.numberOfSamples = new AtomicInteger(0);
		this.histogram = new ConcurrentHashMap<Interval, List<Double>>();
		this.outliers = Collections.synchronizedList(new ArrayList<Double>());
		this.ranges = Collections.synchronizedList(new ArrayList<Double>());
	}

	private DoubleAdder histogramSum; // Keeps the sum of all valid samples
	private AtomicInteger numberOfSamples; // Keeps the count of all valid samples
	private ConcurrentHashMap<Interval, List<Double>> histogram; // The histogram
	private List<Double> outliers; // Keeps outliers
	private List<Double> ranges; // Helps us find which interval a number belongs to in O(logN) time
	
	// Checks whether a given interval is valid (overlapping intervals)
	public synchronized boolean isIntervalValid(Interval i) {
		if(i == null)
			return false;
		
		if(histogram.isEmpty())
			return true;
		
		double lower = i.getLowerBound();
		double upper = i.getUpperBound();
		
		for(Interval intv : histogram.keySet()) {
			double intvLow = intv.getLowerBound();
			double intvUp = intv.getUpperBound();
			
			// Return FALSE..
			// If lower bound of intv is between i's lower and upper bounds
			// If upper bound of intv is between i's lower and upper bounds
			// If the new interval is a subinterval 
			// If the new interval contains another interval
			if(!( (lower < intvLow && upper <= intvLow) || (lower >= intvUp && upper > intvUp) ))
				return false;
		}
		
		return true;
	}
	
	// Inserts new range/interval values to 'ranges' list and keeps it sorted
	public synchronized void insertRange(Interval i) {
		if(i != null) {
			ranges.add(i.getLowerBound());
			ranges.add(i.getUpperBound());
			Collections.sort(ranges);
		}
	}
	
	// Inserts a valid interval to histogram which is initially empty
	public synchronized void insertIntervalToHistogram(Interval i) throws OverlappingIntervalException {
		if(i != null) {
			if(isIntervalValid(i)) {
				histogram.computeIfAbsent(i, s -> Collections.synchronizedList(new ArrayList<Double>()));
				insertRange(i);
			}
			
			else {
				throw new OverlappingIntervalException("Interval: [" + i.getLowerBound() + ", " + i.getUpperBound() + ") "
						+ "is overlapping with other intervals.");
			}
		}
	}
	
	// Given a value, checks whether it belongs to an interval
	// If so, returns that interval, otw RETURNS NULL
	public synchronized Interval findIntervalOfValue(Double value) {
		// Base case - when given value is the first element in ranges
		if(value.equals(ranges.get(0))) {
			try {
				return new Interval(ranges.get(0), ranges.get(1));
			} catch (InvalidIntervalBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int index = Collections.binarySearch(ranges, value);
		index = Math.abs(index) - 1 ;

		// If the index found is even, then this sample does not belong to any interval 
		if(index % 2 == 0 || index < -1 || index == ranges.size()) {
			return null;
		}
		
		// If index <= 0 or index == size(must be even) it gets eliminated in the upper if check
		// Hence, it is safe to get (index - 1) and (index + 1) 
		else {
			// If a value is equal to a lower bound, fetch the next interval
			if((index + 1) < ranges.size()) {
				if(value.equals(ranges.get(index + 1))) {
					index = index + 2;
				}
			}
			
			try {
				return new Interval(ranges.get(index - 1), ranges.get(index));
			} catch (InvalidIntervalBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	// If the given value is valid, inserts it to the histogram & updates the sum & count,
	// Otw, adds it to the outliers list
	public void insertValueToHistogram(Double value) {
		if(value == null)
			return;
		
		Interval intervalOfValue = findIntervalOfValue(value);
		if(intervalOfValue != null) {
			histogram.computeIfAbsent(intervalOfValue, s -> Collections.synchronizedList(new ArrayList<Double>()))
			.add(value);
			histogramSum.add(value);
			numberOfSamples.incrementAndGet();
		}

		else {
			outliers.add(value);
		}
	} 
	
	// Calculates and returns the current sample mean
	public synchronized Double calculateMean() {
		int count = numberOfSamples.get();
		Double result = (count > 0) ? histogramSum.sum() / count : 0;
		return result;
	}
	
	// Calculates and returns the current sample variance
	public synchronized Double calculateVariance() {
		Double variance = 0.0;
		Double mean = calculateMean();
		if(mean.equals(0)) {
			return 0.0;
		}
		
		else {
			int count = numberOfSamples.get();
			for(List<Double> sampleList : histogram.values()) {
				for(Double d : sampleList) {
					variance = variance + (d - mean) * (d - mean) ;
				}
			}	
			return variance / (count - 1);
		}
	}

	/*Getters & Setters*/
	public DoubleAdder getHistogramSum() {
		return histogramSum;
	}
	public void setHistogramSum(DoubleAdder histogramSum) {
		this.histogramSum = histogramSum;
	}
	public AtomicInteger getNumberOfSamples() {
		return numberOfSamples;
	}
	public void setNumberOfSamples(AtomicInteger numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}
	public ConcurrentHashMap<Interval, List<Double>> getHistogram() {
		return histogram;
	}
	public void setHistogram(ConcurrentHashMap<Interval, List<Double>> histogram) {
		this.histogram = histogram;
	}
	public List<Double> getOutliers() {
		return outliers;
	}
	public void setOutliers(ArrayList<Double> outliers) {
		this.outliers = outliers;
	}
	public List<Double> getRanges() {
		return ranges;
	}
	public void setRanges(List<Double> ranges) {
		this.ranges = ranges;
	}
	/**/
	
	public void printHistogram() {
		StringBuilder sb = new StringBuilder();
		for(Entry<Interval,  List<Double>> entry : histogram.entrySet()) {
			sb.append(entry.getKey().toString() + ": " + entry.getValue().size()).append("\n");
		}
		
		sb.append("\n");
		sb.append("sample mean: " + calculateMean() + "\n");
		sb.append("sample variance: " + calculateVariance() + "\n\n");
		sb.append("outliers: ");
		
		if(outliers != null && outliers.size() > 0) {
			sb.append(outliers.get(0));
			for(int i = 1; i < outliers.size(); i++) {
				sb.append(", ").append(outliers.get(i));
			}
		}
		
		System.out.println(sb.toString());
	}
	
}
