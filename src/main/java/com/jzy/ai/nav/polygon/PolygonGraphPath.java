package com.jzy.ai.nav.polygon;

import com.game.ai.pfa.Connection;
import com.game.ai.pfa.DefaultGraphPath;
import com.game.engine.math.Vector3;

/**
 * 多边形路径点
 * 
 * @author JiangZhiYong
 * @date 2018年2月20日
 * @mail 359135103@qq.com
 */
public class PolygonGraphPath extends DefaultGraphPath<Connection<Polygon>> {
    public Vector3 start;
    public Vector3 end;
    public Polygon startPolygon;

    public Polygon getEndPolygon() {
        return (getCount() > 0) ? get(getCount() - 1).getToNode() : startPolygon;
    }

}
