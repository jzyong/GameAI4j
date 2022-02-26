package com.jzy.ai.quadtree;

import com.game.ai.nav.polygon.Polygon;
import com.game.ai.quadtree.polygon.PolygonGuadTree;

/**
 * 功能函数
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 * @param <K>
 * @param <V>
 */
public interface Func<V> {
//	public default void call(PointQuadTree<V> quadTree, Node<V> node) {
//		
//	}

	public default void call(PolygonGuadTree quadTree, Node<Polygon> node) {
		
	}
}
