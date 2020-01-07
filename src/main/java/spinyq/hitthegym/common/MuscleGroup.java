package spinyq.hitthegym.common;

public enum MuscleGroup {
	
	BICEP("biceps"),
	TRICEP("triceps"),
	DELTOID("delts"),
	TRAPEZIUS("traps"),
	LATERAL("lats"),
	GLUTEAL("gluts"),
	HAMSTRING("hamstrings"),
	QUAD("quads"),
	ABDOMINAL("abs"),
	OBLIQUE("obliques"),
	PECTORAL("pecs");

	private String pluralName;

	private MuscleGroup(String pluralName) {
		this.pluralName = pluralName;
	}

	public String getPluralName() {
		return pluralName;
	}
	
}
