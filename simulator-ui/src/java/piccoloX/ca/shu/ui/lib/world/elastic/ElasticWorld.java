package ca.shu.ui.lib.world.elastic;

import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldGround;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PLayer;

/**
 * A World which supports Spring layout. Objects within this world attract and
 * repel each other
 * 
 * @author Shu Wu
 */
public abstract class ElasticWorld extends World {

	private static final long serialVersionUID = 1L;

	private final WorldGround.ChildFilter elasticObjectFilter = new WorldGround.ChildFilter() {
		public boolean acceptChild(WorldObject obj) {
			if (obj instanceof ElasticObject) {
				return true;
			} else {
				return false;
			}
		}
	};

	public ElasticWorld(String name) {
		super(name);
		getGround().setChildFilter(elasticObjectFilter);
	}

	@Override
	protected WorldGround createGround(PLayer layer) {
		return new ElasticGround(this, layer);
	}

	@Override
	public ElasticGround getGround() {
		return (ElasticGround) super.getGround();
	}
}
