package anon.conf.defeasible_benchmark.defeasible;

import java.util.Iterator;

public abstract class AbstractKBStructureGeneratorIterable implements Iterable<KBStructure> {
	
	public abstract int[] getSizes();

	public abstract Iterator<KBStructure> iterator();

}
