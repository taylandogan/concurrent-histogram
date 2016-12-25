package source;

public class Interval {

	public Interval(Double lowerBound, Double upperBound) throws InvalidIntervalBoundsException {
		if(lowerBound == null || upperBound == null){
			throw new InvalidIntervalBoundsException("Invalid interval: constructor argument(s) is(are) null.");
		}
		
		// An interval where lower bound is greater than (or equal to) upper is invalid.
		if(lowerBound >= upperBound) {
			throw new InvalidIntervalBoundsException("Invalid interval bounds: [" + lowerBound + ", " + upperBound + ")");
		}
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	private Double lowerBound;
	private Double upperBound;
	
	public Double getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(Double lowerBound) {
		this.lowerBound = lowerBound;
	}
	public Double getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(Double upperBound) {
		this.upperBound = upperBound;
	}
	
	@Override
	public String toString() {
		return "[" + lowerBound + ", " + upperBound + ")";
	}
	
	// Both lower and upper bound values are used in hashcode() & equals()
	// (Since Histogram.java uses a concurrent map <Interval, V>)
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lowerBound == null) ? 0 : lowerBound.hashCode());
		result = prime * result
				+ ((upperBound == null) ? 0 : upperBound.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval other = (Interval) obj;
		if (lowerBound == null) {
			if (other.lowerBound != null)
				return false;
		} else if (!lowerBound.equals(other.lowerBound))
			return false;
		if (upperBound == null) {
			if (other.upperBound != null)
				return false;
		} else if (!upperBound.equals(other.upperBound))
			return false;
		return true;
	}
	
}
