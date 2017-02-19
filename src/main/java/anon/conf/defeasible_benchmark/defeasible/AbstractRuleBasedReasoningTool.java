package anon.conf.defeasible_benchmark.defeasible;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import anon.conf.defeasible_benchmark.core.Approach;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;

public abstract class AbstractRuleBasedReasoningTool implements Approach {
	public abstract String getKBString();
	
	public abstract String getAnswer(Object answer);
}
