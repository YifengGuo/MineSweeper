import javax.xml.ws.EndpointReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class KnowledgeBase {
	// Set of symbols in disjunctive connections.
	public static class Clause {
		private List<Symbol> ls;

		public Clause(List<Symbol> ls) {
			this.ls = ls;
		}

		public Boolean isEmpty() {
			return ls.isEmpty();
		}

		// return true if added symbol cause clause to 
		// be true. and if true, the clause is cleaned.
		// return false if the clause is uncertain
		public Boolean add(List<Symbol> als) {
			for (Symbol s : als) {
				if (ls.contains(s)) continue;
				// get !als
				Symbol ns = s.getComplement();
				if (ls.contains(ns)) {
					//clear the list
					ls.clear();
					return true;
				}
				ls.add(s);
			}
			return false;
		}

		public int size() {
			return ls.size();
		}

		public void remove(Symbol s) {
			ls.remove(s);
		}
		public List<Symbol> getSymbols() {
			List<Symbol> res = new ArrayList<Symbol>(ls);
			return res;
		}
		public List<Entry> getEntries() {
			List<Entry> res = new ArrayList<Entry>();
			for (Symbol s: ls) {
				if (!res.contains(s.e)) res.add(s.e);
			}
			return res;
		}
		public Symbol getFirst() {
			return ls.get(0);
		}
		@Override
		public String toString() {
			String st = "[ ";
			int idx = 0;
			for (Symbol s : ls) {
				if (idx != 0) st += "+";
				st += s.toString();
			}
			st += "]";
			return st;
		}
	}


	private List<Clause> kb;

	public KnowledgeBase() {
		kb = new ArrayList<Clause>();
	}


	public void addClause(Clause c) {
		kb.add(c);
	}

	public boolean infer(Symbol s) {
		// reconstruct new knowledge base as KB^c
		Entry e = s.e;
		boolean isMine = s.isMine;
		List<Entry> le = new ArrayList<Entry>();
		// get Entry list of KB
		for (Clause c: kb) {
			List<Symbol> ls = c.getSymbols();
			for (Symbol s1: ls) {
				if (!le.contains(s1.e) && !s1.e.equals(e)) le.add(s1.e);
			}
		}
		le.add(0, e);

		return (infer(kb, le, isMine));

	}

	private boolean infer(List<Clause> kb, List<Entry> le, boolean isMine) {
		if (le.size() == 0) return true;
		if (kb.size() == 0) return false;

//		System.out.println("Infer (" + le.get(0).toString() + ": " + isMine + ") from: " + kb.toString());

		List<Clause> kb_new = new ArrayList<Clause>();
		Entry currE = le.get(0);
		List<Entry> le_new = new ArrayList<Entry>(le);
		le_new.remove(0);
		for (Clause c: kb) {
			List<Entry> ls_entry = c.getEntries();
			List<Symbol> ls = c.getSymbols();
			List<Symbol> new_c = new ArrayList<>();
			boolean isTrue = false;
			for (Symbol s : ls) {
				if (s.e == currE && s.isMine == isMine){
					isTrue = true;
//					System.out.println("Find same!");
					break;
				}

				else if (s.e == currE && s.isMine == !isMine){
//					System.out.println("Find comp!");
					continue;
				}

				else
					new_c.add(new Symbol(s.e, s.isMine));
			}


			if (new_c.size() == 0 && isTrue == false) return false;
			if (new_c.size() != 0 && isTrue == false) kb_new.add(new Clause(new_c));
		}
		return infer(kb_new, le_new, true) || infer(kb_new, le_new, false);
	}

	public List<Entry> getPositiveUnitClause() {
		List<Entry> res = new ArrayList<Entry>();
		List<Clause> removeList = new ArrayList<>();

		for(Clause c : kb) {
			if (c.size() == 1) {
				if (c.getFirst().isMine == true) {
					res.add(c.getFirst().e);
					removeList.add(c);
				}
			}
		}

		return res;
	}
	public List<Entry> getNegativeUnitClause() {
		List<Entry> res = new ArrayList<Entry>();
		List<Clause> removeList = new ArrayList<>();
		for(Clause c : kb) {
			if (c.size() == 1) {
				if (c.getFirst().isMine == false) {
					res.add(c.getFirst().e);
					removeList.add(c);
				}
			}
		}

		return res;
	}


	public String toSting() {
		String st = "";
		for (Clause c: kb) {
			st += c.toString();
			st += "&";
		}
		return st;
	}

	public void update(Board_logic bd, Queue<Entry> hasMine, Queue<Entry> noMine) {
		boolean[][] isCleared = bd.getCleared();
		boolean[][] isLabeled = bd.getLabeled();
		// get known information from board
		List<Symbol> knownInfo = new ArrayList<>();
		for (int i=0; i < bd.getWidth(); i++) {
			for (int j=0; j < bd.getLen(); j++) {
				if (isCleared[i][j]) {
					knownInfo.add(new Symbol(bd.entries[i][j], false));
//					System.out.println("From board: clear(" + i + ", " + j + ")");
				}
				if (isLabeled[i][j]) {
					knownInfo.add(new Symbol(bd.entries[i][j], true));
//					System.out.println("From board: label(" + i + ", " + j + ")");
				}
			}
		}
		List<Entry> mines = new ArrayList<>(hasMine);
		for (Entry e : mines) {
			knownInfo.add(new Symbol(e, true));
//			System.out.println("From logic: "+e.toString());
		}
		List<Entry> nomines = new ArrayList<>(noMine);
		for (Entry e : nomines) {
			knownInfo.add(new Symbol(e, false));
//			System.out.println("From logic: "+e.toString());
		}

		// Update knowledge base
		for (Symbol ks : knownInfo) {
			List<Clause> kb_new = new ArrayList<Clause>();
			List<Clause> lc = new ArrayList<>(kb);
//			System.out.println("S: " + ks.toString() + ", KBsize = " + lc.size());
			for (Clause c: lc) {
				boolean isTrue = false;
				List<Symbol> slc = c.getSymbols();
				List<Symbol> new_c = new ArrayList<>();
				for (Symbol s : slc) {
					if (s.e == ks.e && s.isMine == ks.isMine) {
						isTrue = true;
						break;
					} else if (s.e == ks.e && s.isMine == !ks.isMine) {
						continue;
					} else {
						new_c.add(new Symbol(s.e, s.isMine));
					}
				}
				if (new_c.size() != 0 && isTrue == false) kb_new.add(new Clause(new_c));
			}
			kb = kb_new;
		}

	}

	public boolean isEmpty() {
		return kb.isEmpty();
	}
	public void print() {
		for (Clause c : kb) {
			System.out.print(c.toString() + "^");
		}
		System.out.println();
	}
}