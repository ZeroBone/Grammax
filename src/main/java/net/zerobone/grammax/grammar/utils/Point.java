package net.zerobone.grammax.grammar.utils;

import java.util.Objects;

public class Point {

    public int productionId;

    public int position;

    public Point(int productionId, int position) {
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
        Point point = (Point)o;
        return productionId == point.productionId &&
            position == point.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionId, position);
    }

}