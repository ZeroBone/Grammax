package net.zerobone.grammax.grammar.point;

// this class is final because we will never mix up different kinds of points
public final class Point extends BasePoint {

    public Point(int productionId, int position) {
        super(productionId, position);
    }

}