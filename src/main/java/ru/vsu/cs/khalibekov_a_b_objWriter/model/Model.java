package ru.vsu.cs.khalibekov_a_b_objWriter.model;

import ru.vsu.cs.khalibekov_a_b_objWriter.math.Vector2f;
import ru.vsu.cs.khalibekov_a_b_objWriter.math.Vector3f;

import java.util.ArrayList;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
}
