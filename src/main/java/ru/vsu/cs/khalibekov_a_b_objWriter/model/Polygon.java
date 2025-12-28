package ru.vsu.cs.khalibekov_a_b_objWriter.model;

import java.util.ArrayList;

public class Polygon {

    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;


    public Polygon() {
        vertexIndices = new ArrayList<Integer>();
        textureVertexIndices = new ArrayList<Integer>();
        normalIndices = new ArrayList<Integer>();
    }

    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        if (vertexIndices == null) {
            throw new IllegalArgumentException("Список вершин не может быть null");
        }
        if (vertexIndices.size() < 3) {
            // Разрешаем меньше 3 вершин для тестирования некорректных случаев
            System.err.println("Предупреждение: полигон имеет менее 3 вершин: " + vertexIndices.size());
        }
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        if (textureVertexIndices != null && textureVertexIndices.size() < 3) {
            // Разрешаем меньше 3 текстур для тестирования некорректных случаев
            System.err.println("Предупреждение: полигон имеет менее 3 текстурных координат: " +
                    (textureVertexIndices == null ? 0 : textureVertexIndices.size()));
        }
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        if (normalIndices != null && normalIndices.size() < 3) {
            // Разрешаем меньше 3 нормалей для тестирования некорректных случаев
            System.err.println("Предупреждение: полигон имеет менее 3 нормалей: " +
                    (normalIndices == null ? 0 : normalIndices.size()));
        }
        this.normalIndices = normalIndices;
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }
}