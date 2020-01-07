package spinyq.hitthegym.common;

import java.util.List;

/**
 * A list of exercises that can be cycled through when lifting.
 * @author spinyq
 *
 */
public class ExerciseSet {

	private List<Exercise> list;

	public ExerciseSet(List<Exercise> list) {
		super();
		this.list = list;
	}

	public List<Exercise> getList() {
		return list;
	}
	
}
