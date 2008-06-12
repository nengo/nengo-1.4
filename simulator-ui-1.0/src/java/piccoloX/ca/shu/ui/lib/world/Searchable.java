package ca.shu.ui.lib.world;

import java.util.Collection;

public interface Searchable {

	public Collection<SearchValuePair> getSearchableValues();

	class SearchValuePair {
		private String name;
		private String value;

		public SearchValuePair(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}
}
