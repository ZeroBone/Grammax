package net.zerobone.grammax.grammar.point;

import java.util.Objects;

public abstract class BasePoint {

    public final int productionId;

    public final int position;

    protected BasePoint(int productionId, int position) {
        this.productionId = productionId;
        this.position = position;
    }

    @Override
    public String toString() {
        return "(" + productionId + ", " + position + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasePoint basePoint = (BasePoint)o;
        return productionId == basePoint.productionId &&
            position == basePoint.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionId, position);
    }

}