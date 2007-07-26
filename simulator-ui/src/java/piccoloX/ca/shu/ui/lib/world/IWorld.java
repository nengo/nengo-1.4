package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.world.impl.WorldGround;
import ca.shu.ui.lib.world.impl.WorldObject;
import ca.shu.ui.lib.world.impl.WorldSky;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

public interface IWorld {
	public boolean containsNode(PNode node);
	
	public void zoomToWorld();

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);
	
	public void zoomToNode(IWorldObject node);
	
	public void showTooltip(WorldObject pControls,
			WorldObject nodeAttacedTo);
	public void hideControls();
	public Point2D getPositionInGround(IWorldObject wo);

	public Point2D getPositionInSky(WorldObject wo);

	public WorldGround getGround();

	public WorldSky getSky();

	public double getGroundScale();

	public boolean addActivity(PActivity activity);

	public Point2D skyToGround(Point2D position);

	public double getScreenWidth();

	public double getScreenHeight();

	public void setWorldScale(float scale);
	
	public String getName();
	
//	public void createGrid();
}
