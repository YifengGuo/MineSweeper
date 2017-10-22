public class Symbol {
	public Entry e;
	public	Boolean isMine = true;

	public Symbol(Entry e, Boolean isMine) {
		this.e = e;
		this.isMine = isMine;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Symbol)) {
			return false;
		}
		Symbol s = (Symbol) o;
		return s.e.x == e.x
			&& s.e.y == e.y
			&& s.isMine == isMine;
	}

	public Boolean complements(Symbol s) {
		return isMine != s.isMine;
	}

	public Symbol getComplement() {
		return new Symbol(e, !isMine);
	}

	@Override
	public String toString() {
		return isMine?
			"M(" + e.x + "," + e.y + ")":
			"nM(" + e.x + "," + e.y + ")";
	}
}