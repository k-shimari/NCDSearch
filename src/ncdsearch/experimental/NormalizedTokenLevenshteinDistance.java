package ncdsearch.experimental;

import ncdsearch.ICodeDistanceStrategy;
import ncdsearch.TokenSequence;

public class NormalizedTokenLevenshteinDistance extends TokenLevenshteinDistance implements ICodeDistanceStrategy {

	private int queryLength;

	public NormalizedTokenLevenshteinDistance(TokenSequence query) {
		super(query);
		this.queryLength = query.size();
	}

	@Override
	public double computeDistance(TokenSequence code) {
		double d = super.computeDistance(code);
		return d * 1.0 / Math.max(queryLength, code.size());
	}

	@Override
	public double computeDistance(TokenSequence code, TokenSequence code2) {
		double d = super.computeDistance(code, code2);
		return d * 1.0 / Math.max(code.size(), code2.size());
	}

	@Override
	public void close() {
		// This object has no system resource.
	}
}
