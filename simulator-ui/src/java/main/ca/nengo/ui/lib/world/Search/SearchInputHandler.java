package ca.nengo.ui.lib.world.Search;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import ca.nengo.ui.lib.world.Destroyable;
import ca.nengo.ui.lib.world.Searchable;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.lib.world.piccolo.WorldImpl;

public class SearchInputHandler implements Destroyable {
	public static boolean compare(String searchTerm, String target) {
		return compare(searchTerm, target, CompareMode.CONTAINS);

	}

	private static boolean compare(String searchTerm, String target, CompareMode mode) {
		boolean isMatch = false;
		if (target == null) {
			return false;
		}

		switch (mode) {

		case CONTAINS:
			isMatch = target.matches("(?i).*" + searchTerm + ".*");
			break;
		case ENDS_WITH:
			isMatch = target.matches("(?i).*" + searchTerm);
			break;
		case STARTS_WITH:
			// Starts with
			isMatch = target.matches("(?i)" + searchTerm + ".*");
			break;
		default:
			throw new InvalidParameterException();
		}

		return isMatch;
	}

	private boolean isSearching;

	private Iterable<WorldObject> objects;

	private StringBuilder searchQuery;

	private SearchBox searchUI;

	private WorldImpl world;

	public SearchInputHandler(WorldImpl world) {
		super();
		this.world = world;

		searchUI = new SearchBox(world);
		world.getSky().addChild(searchUI);
		searchUI.setOffset(10, 10);

		searchQuery = new StringBuilder(20);
	}

	private Collection<WorldObject> searchResults;

	private void addChar(char keyChar) {
		if (isSearching) {
			searchQuery.append(keyChar);
			boolean successfull = searchAndUpdateResults();
			searchUI.addSearchChar(keyChar, successfull);
		}

	}

	private boolean searchAndUpdateResults() {
		searchResults = doSearch(world, objects, searchQuery.toString());
		searchUI.updateSearchResults(searchResults, searchQuery.toString());

		return (searchResults.size() > 0);
	}

	private void cancelSearch() {
		finishSearch();

		searchUI.restoreView();
	}

	private static Collection<WorldObject> doSearch(WorldImpl world, Iterable<WorldObject> objects,
			String searchTermStr) {
		LinkedList<WorldObject> matchingObjects = new LinkedList<WorldObject>();
		if (objects != null && searchTermStr.length() > 0) {

			for (WorldObject wo : objects) {

				if (wo instanceof Searchable) {
					/*
					 * Match using Searchable interface
					 */
					Searchable searchable = (Searchable) wo;
					Collection<Searchable.SearchValuePair> searchPairs = searchable
							.getSearchableValues();

					boolean foundMatch = false;
					for (Searchable.SearchValuePair searchPair : searchPairs) {
						if (compare(searchTermStr, searchPair.getValue())) {
							foundMatch = true;
							break;
						}
					}
					if (foundMatch) {
						matchingObjects.add(wo);
					}

				} else {
					/*
					 * Match using Object name
					 */
					if (compare(searchTermStr, wo.getName())) {
						matchingObjects.add(wo);
					}
				}
			}

		}
		return rankResults(world, matchingObjects, searchTermStr);
	}

	private static Collection<WorldObject> rankResults(WorldImpl world,
			Collection<WorldObject> results, String searchTerm) {
		/*
		 * Rank results by distance from screen center
		 */

		Point2D center = world.getSky().getBounds().getCenter2D();
		world.skyToGround(center);

		ArrayList<SortWrapper> sortList = new ArrayList<SortWrapper>(results.size());
		for (WorldObject wo : results) {

			Point2D centerWo = new Point2D.Double(wo.getBounds().getCenterX(), wo.getBounds()
					.getCenterY());

			wo.localToGlobal(centerWo);

			double distance = centerWo.distance(center);

			sortList.add(new SortWrapper(distance, wo));
		}
		Collections.sort(sortList);

		ArrayList<WorldObject> sortedResults = new ArrayList<WorldObject>(results.size());
		for (SortWrapper sortWr : sortList) {
			sortedResults.add(sortWr.getWorldObject());
		}
		return sortedResults;
	}

	private void backspacePressed() {
		if (searchQuery.length() > 0) {
			searchQuery.deleteCharAt(searchQuery.length() - 1);

			searchAndUpdateResults();

			searchUI.removeSearchChar();

			if (searchQuery.length() == 0) {
				searchUI.clearSearchResults();
			}

		} else {
			cancelSearch();
		}
	}

	public void beginSearch() {

		isSearching = true;
		searchUI.setEnabled(true);

		objects = world.getGround().getChildren();
	}

	public void finishSearch() {

		objects = null;
		isSearching = false;

		searchUI.setEnabled(false);

		if (searchQuery.length() > 0) {
			searchQuery.delete(0, searchQuery.length());
		}

	}

	public boolean isSearching() {
		return isSearching;
	}

	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_KP_RIGHT || keyCode == KeyEvent.VK_RIGHT
				|| keyCode == KeyEvent.VK_KP_DOWN || keyCode == KeyEvent.VK_DOWN
				|| keyCode == KeyEvent.VK_PAGE_DOWN) {
			searchUI.nextResult();
		} else if (keyCode == KeyEvent.VK_KP_LEFT || keyCode == KeyEvent.VK_LEFT
				|| keyCode == KeyEvent.VK_KP_UP || keyCode == KeyEvent.VK_UP
				|| keyCode == KeyEvent.VK_PAGE_UP) {
			searchUI.previousResult();
		}
	}

	public void keyTyped(char keyChar) {
		if (keyChar == KeyEvent.VK_ESCAPE) {
			cancelSearch();
		} else if (keyChar == KeyEvent.VK_BACK_SPACE) {
			backspacePressed();
		} else if (keyChar == KeyEvent.VK_ENTER) {
			finishSearch();
		} else {
			if (keyChar >= 48 && keyChar <= 122) {
				addChar(keyChar);
			}
		}

	}

	private enum CompareMode {
		CONTAINS, ENDS_WITH, STARTS_WITH
	}

	public void destroy() {
		finishSearch();

	}
}

class SortWrapper implements Comparable<SortWrapper> {

	private double sortValue;
	private WorldObject worldObject;

	public SortWrapper(double sortValue, WorldObject worldObject) {
		super();
		this.sortValue = sortValue;
		this.worldObject = worldObject;
	}

	public int compareTo(SortWrapper o) {

		double delta = (this.getSortValue() - o.getSortValue());
		if (delta > 0) {
			return 1;
		} else if (delta < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	public double getSortValue() {
		return sortValue;
	}

	public WorldObject getWorldObject() {
		return worldObject;
	}

}