package test;

import static org.junit.Assert.*;

import java.net.PasswordAuthentication;

import org.junit.Test;

import source.Interval;
import source.InvalidIntervalBoundsException;

public class IntervalTest {
	
	@Test(expected=InvalidIntervalBoundsException.class)
	public void testIntervalNull() throws InvalidIntervalBoundsException {
		Interval i = new Interval(null, null);
	}
	
	@Test(expected=InvalidIntervalBoundsException.class)
	public void testIntervalGreaterLowerBound() throws InvalidIntervalBoundsException {
		Interval i = new Interval(5.0, 4.0);
	}
	
	@Test(expected=InvalidIntervalBoundsException.class)
	public void testIntervalEqualBounds() throws InvalidIntervalBoundsException {
		Interval i = new Interval(5.0, 5.0);
	}
	
	@Test
	public void testIntervalSuccess() throws InvalidIntervalBoundsException {
		Interval i = new Interval(5.0, 6.0);
		return;
	}
	
	@Test
	public void testHashCodeEquality() throws InvalidIntervalBoundsException {
		Interval i1 = new Interval(1.0, 3.0);
		Interval i2 = new Interval(1.0, 3.0);	
		assertTrue(i1.hashCode() == i2.hashCode());
	}
	
	@Test
	public void testEqualsObjectNull() throws InvalidIntervalBoundsException {
		Interval i = new Interval(5.0, 6.0);
		assertFalse(i.equals(null));
	}
	
	@Test
	public void testEqualsObjectItself() throws InvalidIntervalBoundsException {
		Interval i = new Interval(5.0, 6.0);
		assertTrue(i.equals(i));
	}
	
	@Test
	public void testEqualsObjectDifferent() throws InvalidIntervalBoundsException {
		Interval i1 = new Interval(5.0, 6.0);
		Interval i2 = new Interval(5.0, 7.0);
		assertFalse(i1.equals(i2));
	}

}
