package ca.nengo.ui.lib.world.Search;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Stack;

import ca.nengo.ui.lib.Style.NengoStyle;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.lib.world.Searchable;
import ca.nengo.ui.lib.world.World;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.lib.world.piccolo.WorldObjectImpl;
import ca.nengo.ui.lib.world.piccolo.objects.TooltipWrapper;
import ca.nengo.ui.lib.world.piccolo.primitives.Path;
import ca.nengo.ui.lib.world.piccolo.primitives.Text;
import ca.nengo.ui.models.tooltips.Tooltip;
import ca.nengo.ui.models.tooltips.TooltipBuilder;

class SearchBox extends WorldObjectImpl {

	private double currentOffsetX = 0;

	private Path rectangle;
	private Rectangle2D savedViewBounds;
	private Text searchingText;
	private String searchQuery;
	private Path searchResultHighlight;

	private WorldObject[] searchResults;

	private Hashtable<WorldObject, Text> searchResultsMap;

	private Text searchResultsText;
	private Stack<Text> textStack;

	private TooltipWrapper tooltipWrapper;

	private World world;

	private int currentResultIndex;

	public SearchBox(World world) {
		super();
		this.world = world;
		searchingText = new Text("Searching: ");

		searchingText.setFont(NengoStyle.FONT_SEARCH_TEXT);
		searchResultsText = new Text("");
		searchResultsText.setFont(NengoStyle.FONT_SEARCH_RESULT_COUNT);

		rectangle = Path.createRectangle(0, 0, 1, 1);

		rectangle.setStrokePaint(NengoStyle.COLOR_SEARCH_BOX_BORDER);
		rectangle.setStroke(new BasicStroke(2f));
		rectangle.setPaint(NengoStyle.COLOR_FOREGROUND);
		rectangle.setTransparency(0.2f);

		addChild(rectangle);
		addChild(searchingText);
		addChild(searchResultsText);

		textStack = new Stack<Text>();
		this.setChildrenPickable(false);
		this.setPickable(false);
		this.setSelected(false);

		setEnabled(false);
	}

	private void focusOnSearchResult(int resultIndex) {
		if ((searchResults == null) || (resultIndex >= searchResults.length || resultIndex < 0)) {
			/*
			 * Result is out of bounds
			 */
			return;
		}

		if (savedViewBounds == null) {
			savedViewBounds = world.getSky().getViewBounds();
		}

		currentResultIndex = resultIndex;
		WorldObject wo = searchResults[resultIndex];

		/*
		 * Find the corresponding result text and highlight it
		 */
		Text searchText = searchResultsMap.get(wo);
		if (searchText != null) {
			if (searchResultHighlight != null) {
				searchResultHighlight.destroy();
			}

			searchResultHighlight = Path.createRectangle(0, 0, 1, 1);
			searchResultHighlight.setBounds(searchText.getFullBounds());
			searchResultsText.addChild(searchResultHighlight, 0);
			searchResultHighlight.setTransparency(0.5f);

		}

		TooltipBuilder tooltipBuilder = new TooltipBuilder("Matches in " + wo.getName());
		if (wo instanceof Searchable) {
			Searchable searchable = (Searchable) wo;
			Collection<Searchable.SearchValuePair> searchPairs = searchable.getSearchableValues();
			for (Searchable.SearchValuePair searchPair : searchPairs) {
				if (SearchInputHandler.compare(searchQuery, searchPair.getValue())) {
					tooltipBuilder.addProperty(searchPair.getName(), searchPair.getValue());
				}
			}
		} else if (SearchInputHandler.compare(searchQuery, wo.getName())) {
			tooltipBuilder.addProperty("Object Name", wo.getName());
		}

		removeTooltip();
		Tooltip tooltip = new Tooltip(tooltipBuilder);
		tooltipWrapper = new TooltipWrapper(world.getSky(), tooltip, wo);

		world.zoomToObject(wo);
	}

	private void removeTooltip() {
		if (tooltipWrapper != null) {
			tooltipWrapper.destroy();
		}
	}

	private void updateBounds() {
		rectangle.setBounds(0, 0, 1, 1);
		Rectangle2D bounds = parentToLocal(getFullBounds());

		rectangle.setBounds(bounds);
		// rectangle.setHeight(searchingText.getHeight());

		searchResultsText.setOffset(0, searchingText.getHeight());
	}

	@Override
	protected void prepareForDestroy() {
		setEnabled(false);
		super.prepareForDestroy();
	}

	public void addSearchChar(char keyChar, boolean isSearchSuccessfull) {
		Text textChar = new Text("" + keyChar);
		textChar.setFont(NengoStyle.FONT_SEARCH_TEXT);
		if (!isSearchSuccessfull) {
			textChar.setTextPaint(NengoStyle.COLOR_SEARCH_BAD_CHAR);
		}

		textStack.push(textChar);
		textChar.setOffset(currentOffsetX, 0);

		currentOffsetX += textChar.getWidth();
		this.addChild(textChar);
		this.moveToFront();

		updateBounds();
	}

	public void clearSearchResults() {
		searchResultsText.setText("");
		searchResultsText.destroyChildren();
		searchResultsMap = null;
		searchQuery = null;
		searchResults = null;
		updateBounds();
	}

	public void nextResult() {
		focusOnSearchResult(++currentResultIndex);
	}

	public void previousResult() {
		focusOnSearchResult(--currentResultIndex);
	}

	public void removeSearchChar() {
		if (textStack.size() > 0) {
			Text textChar = textStack.pop();

			currentOffsetX -= textChar.getWidth();
			textChar.destroy();

		} else {
			Util.Assert(false);
		}
		updateBounds();
		this.moveToFront();
	}

	public void restoreView() {
		if (savedViewBounds != null) {
			world.getSky().animateViewToCenterBounds(savedViewBounds, true, 200);
		}
	}

	public void setEnabled(boolean enabled) {

		this.setVisible(enabled);

		if (enabled) {
			savedViewBounds = null;
			moveToFront();
		} else {
			clearSearchResults();
			removeTooltip();

			currentOffsetX = searchingText.getWidth();

			for (WorldObject wo : textStack) {
				wo.destroy();
			}

			textStack.clear();
			updateBounds();
		}

	}

	public void updateSearchResults(Collection<WorldObject> results, String searchQuery) {
		clearSearchResults();

		this.searchQuery = searchQuery;
		this.searchResults = results.toArray(new WorldObject[] {});

		searchResultsText.setText(results.size() + " results from " + world.getName());
		searchResultsMap = new Hashtable<WorldObject, Text>(results.size());

		double offsetY = searchResultsText.getHeight();
		for (WorldObject wo : results) {
			Text resultText = new Text(wo.getName() + " (" + wo.getClass().getSimpleName() + ")");
			searchResultsMap.put(wo, resultText);
			searchResultsText.addChild(resultText);

			resultText.setOffset(0, offsetY);
			offsetY += resultText.getHeight();
		}

		if (results.size() > 0) {
			focusOnSearchResult(0);
		}
		updateBounds();
	}
}