package ru.vsu.cs.khalibekov_a_b_objWriter.math;

public class Vector2f {
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f)", x, y);
    }

    public float x;
    public float y;
}