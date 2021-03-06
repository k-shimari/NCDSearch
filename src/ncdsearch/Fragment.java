package ncdsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Fragment implements Comparable<Fragment> {

	private static final String SEPARATOR = ",";

	private String filename;
	private TokenSequence fileTokens;
	private int startPos;
	private int endPos;
	private double distance;
	
	/**
	 * @param filename
	 * @param startPos
	 * @param endPos exclusive.
	 * @param distance
	 */
	public Fragment(String filename, TokenSequence fileTokens, int startPos, int endPos, double distance) {
		this.filename = filename;
		this.fileTokens = fileTokens;
		this.startPos = startPos;
		this.endPos = endPos;
		this.distance = distance;
		assert this.startPos < this.endPos: "Zero-length fragment is not allowed.";
	}
	
	/**
	 * @return a string represenation of the position and the distance.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(filename);
		b.append(SEPARATOR);
		b.append(getStartLine());
		b.append(SEPARATOR);
		b.append(getEndLine());
		b.append(SEPARATOR);
        b.append(distance);
        b.append("\n");
        return b.toString();
	}
	
	/**
	 * @return a string represenation of the position and the distance.
	 * It includes character positions in lines to analyze details.
	 */
	public String toLongString() {
		StringBuilder b = new StringBuilder();
		b.append(filename);
		b.append(SEPARATOR);
		b.append(getStartLine());
		b.append(SEPARATOR);
        b.append(getStartCharPositionInLine()); 
		b.append(SEPARATOR);
		b.append(getEndLine());
		b.append(SEPARATOR);
        b.append(getEndCharPositionInLine()); 
		b.append(SEPARATOR);
        b.append(distance);
        b.append("\n");
        return b.toString();
	}
	
	public String getFilename() {
		return filename;
	}
	
	public int getStartLine() {
		return fileTokens.getLine(startPos);
	}
	
	public int getEndLine() {
		return fileTokens.getLine(endPos-1);
	}
	
	public int getStartCharPositionInLine() {
		return fileTokens.getCharPositionInLine(startPos);
	}
	
	public int getEndCharPositionInLine() {
		return fileTokens.getEndCharPositionInLine(endPos-1);
	}
	
	public double getDistance() {
		return distance;
	}
	
	public String getTokenString() {
		return fileTokens.substring(startPos, endPos).toString();
	}
	
	
	
	/**
	 * @param another fragment.
	 * @return true if this fragment overlaps with another one.
	 */
	public boolean overlapWith(Fragment another) {
		return !(this.endPos <= another.startPos ||
			another.endPos <= this.startPos);
	}
	
	
	/**
	 * Compare two fragments and select a better one.
	 * @param another
	 * @return true if this object is better for output. 
	 * False if another is better.
	 * A fragment is better than another one if it has a lower distance.
	 * If tied, shorter is better.  
	 * If they have the same legnth, smaller start position is better. 
	 */
	public boolean isBetterThan(Fragment another) {
		// Distance: Lower is better
		if (this.distance < another.distance) return true;
		else if (this.distance > another.distance) return false;
		else {
			// Shorter is better
			int thislen = this.endPos - this.startPos;
			int anotherlen = another.endPos - another.startPos;
			if (thislen < anotherlen) return true;
			else if (thislen > anotherlen) return false;
			else {
				return this.startPos < another.startPos;
			}
		}
	}
	
	/**
	 * Sort fragments by their starting positions in the ascending order.
	 */
	@Override
	public int compareTo(Fragment another) {
		return this.startPos - another.startPos;
	}
	
	/**
	 * Remove redundant elements by selecting best fragments in a greedy manner
	 * @param fragments a list of fragments to be processed.  
	 * This collection is modified by this method.
	 * @return a filtered list of fragments.
	 */
	public static ArrayList<Fragment> filter(ArrayList<Fragment> fragments) {
		fragments.sort(new Comparator<Fragment>() {
			@Override
			public int compare(Fragment o1, Fragment o2) {
				if (o1.isBetterThan(o2)) return -1;
				else return 1;
			}
		});
		ArrayList<Fragment> result = new ArrayList<>();
		for (int i=0; i<fragments.size(); i++) {
			Fragment f1 = fragments.get(i);
			if (f1 == null) continue;
			
			result.add(f1);
			for (int j=i+1; j<fragments.size(); j++) {
				Fragment f2 = fragments.get(j);
				if (f2 == null) continue;
				if (f1.overlapWith(f2)) {
					fragments.set(j, null);
				}
			}
		}
		Collections.sort(result);
		return result;
	}

}
