package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import source.Histogram;
import source.HistogramBuilder;
import source.Interval;
import source.InvalidIntervalBoundsException;
import source.OverlappingIntervalException;

public class HistogramTest {
	
	public static Histogram hist = new Histogram();
	public static String filename = "test_histogram.txt";
	
	@BeforeClass public static void initializeHistogram() {
		Thread t = new Thread(new HistogramBuilder(filename , hist));
		t.run();
	}

	@Test
	public void testIsIntervalValidSuccess() throws InvalidIntervalBoundsException {
		Interval i = new Interval(20.0, 25.1);
		assertTrue(hist.isIntervalValid(i));
	}
	
	@Test
	public void testIsIntervalValidFailNull() throws InvalidIntervalBoundsException {
		Interval i = null;
		assertFalse(hist.isIntervalValid(i));
	}
	
	@Test
	public void testIsIntervalValidFailLeftOverlap() throws InvalidIntervalBoundsException {
		Interval i = new Interval(20.0, 32.1);
		assertFalse(hist.isIntervalValid(i));
	}
	
	@Test
	public void testIsIntervalValidFailSameLowerBound1() throws InvalidIntervalBoundsException {
		Interval i = new Interval(50.0, 51.0);
		assertFalse(hist.isIntervalValid(i));
	}
	
	@Test
	public void testIsIntervalValidFailSameLowerBound2() throws InvalidIntervalBoundsException {
		Interval i = new Interval(50.0, 53.0);
		assertFalse(hist.isIntervalValid(i));
	}
	
	@Test
	public void testIsIntervalValidFailRightOverlap() throws InvalidIntervalBoundsException {
		Interval i = new Interval(1.0, 2.0);
		assertFalse(hist.isIntervalValid(i));
	}
	
	@Test
	public void testIsIntervalValidFailSubInterval() throws InvalidIntervalBoundsException {
		Interval i = new Interval(32.0, 33.1);
		assertFalse(hist.isIntervalValid(i));
	}
	
	@Test
	public void testIsIntervalValidFailConsistsInterval() throws InvalidIntervalBoundsException {
		Interval i = new Interval(49.0, 57.1);
		assertFalse(hist.isIntervalValid(i));
	}

	@Test
	public void testInsertRangeSuccess() throws InvalidIntervalBoundsException {
		assertFalse(hist.getRanges().contains(55.0));
		assertFalse(hist.getRanges().contains(56.0));
		hist.insertRange(new Interval(55.0, 56.0));
		assertTrue(hist.getRanges().contains(55.0));
		assertTrue(hist.getRanges().contains(56.0));
		int indexOfLower = hist.getRanges().indexOf(55.0);
		int indexOfUpper = hist.getRanges().indexOf(56.0);
		assertTrue(indexOfUpper - 1 == indexOfLower);
	}
	
	@Test
	public void testInsertRangeFailNull() {
		List<Double> initial = hist.getRanges();
		hist.insertRange(null);
		List<Double> later = hist.getRanges();
		for(int i = 0; i < initial.size(); i++) {
			if(initial.get(i) != later.get(i))
				fail("Range list must not be affected!");
		}
	}

	@Test
	public void testInsertIntervalToHistogramSuccess() throws InvalidIntervalBoundsException, OverlappingIntervalException {
		Interval i = new Interval(60.0, 70.0);
		hist.insertIntervalToHistogram(i);
		return;
	}
	
	@Test
	public void testInsertIntervalToHistogramFailNull() throws OverlappingIntervalException {
		int formerSize = hist.getHistogram().size();
		Interval i = null;
		hist.insertIntervalToHistogram(i);
		int latterSize = hist.getHistogram().size();
		assertTrue(formerSize == latterSize);
	}
	
	@Test(expected=InvalidIntervalBoundsException.class)
	public void testInsertIntervalToHistogramFailInvalid() throws InvalidIntervalBoundsException, OverlappingIntervalException {
		Interval i = new Interval(40.0, 30.0);
		hist.insertIntervalToHistogram(i);
	}
	
	@Test(expected=OverlappingIntervalException.class)
	public void testInsertIntervalToHistogramFailOverlapping() throws InvalidIntervalBoundsException, OverlappingIntervalException {
		Interval i = new Interval(30.0, 40.0);
		hist.insertIntervalToHistogram(i);
	}

	@Test
	public void testFindIntervalOfValueSuccess() {
		Interval inv = hist.findIntervalOfValue(3.5);
		if(inv.getLowerBound() == 3 && inv.getUpperBound() == 4.1)
			return;
		else
			fail("Could not find interval correctly");
	}
	
	@Test
	public void testFindIntervalOfValueFailNull() {
		Interval inv = hist.findIntervalOfValue(5.0);
		if(inv != null) 
			fail("Could not find interval correctly");
	}
	
	@Test
	public void testFindIntervalOfValueFailZero() {
		Interval inv = hist.findIntervalOfValue(-1.0);
		if(inv != null) 
			fail("Could not find interval correctly");
	}
	
	@Test
	public void testFindIntervalOfValueSuccessLowerBound() {
		Interval inv = hist.findIntervalOfValue(60.0);
		assertTrue(inv.getLowerBound() == 60.0 && inv.getUpperBound() == 70.0);
	}
	
	@Test
	public void testFindIntervalOfValueSuccessEqualToFirstElement() {
		Interval inv = hist.findIntervalOfValue(0.0);
		assertTrue(inv.getLowerBound() == 0.0 && inv.getUpperBound() == 1.1);
	}
	
	@Test
	public void testFindIntervalOfValueFailEqualToLastElement() {
		Interval inv = hist.findIntervalOfValue(70.0);
		if(inv != null) 
			fail("Could not find interval correctly");
	}
	
	@Test
	public void testFindIntervalOfValueFailLastElement() {
		Interval inv = hist.findIntervalOfValue(80.0);
		if(inv != null) 
			fail("Could not find interval correctly");
	}
	
	@Test
	public void testFindIntervalOfValueFailUpperBound() {
		Interval inv = hist.findIntervalOfValue(51.5);
		if(inv != null) 
			fail("Could not find interval correctly");
	}

	@Test
	public void testInsertValueToHistogramSuccess() {
		int formerNumberOfSamples = hist.getNumberOfSamples().get();
		double formerSum = hist.getHistogramSum().sum();
		hist.insertValueToHistogram(0.5);
		int latterNumberOfSamples = hist.getNumberOfSamples().get();
		double latterSum = hist.getHistogramSum().sum();
		
		assertTrue(formerNumberOfSamples + 1 == latterNumberOfSamples);
		assertTrue(formerSum + 0.5 == latterSum);
		return;
	}
	
	@Test
	public void testInsertValueToHistogramFail() {
		int formerNumberOfSamples = hist.getNumberOfSamples().get();
		double formerSum = hist.getHistogramSum().sum();
		hist.insertValueToHistogram(-1.0);
		int latterNumberOfSamples = hist.getNumberOfSamples().get();
		double latterSum = hist.getHistogramSum().sum();
		
		assertTrue(formerNumberOfSamples == latterNumberOfSamples);
		assertTrue(formerSum == latterSum);
		return;
	}
	
	@Test
	public void testInsertValueToHistogramFailNull() {
		int formerNumberOfSamples = hist.getNumberOfSamples().get();
		double formerSum = hist.getHistogramSum().sum();
		hist.insertValueToHistogram(null);
		int latterNumberOfSamples = hist.getNumberOfSamples().get();
		double latterSum = hist.getHistogramSum().sum();
		
		assertTrue(formerNumberOfSamples == latterNumberOfSamples);
		assertTrue(formerSum == latterSum);
		return;
	}

}
