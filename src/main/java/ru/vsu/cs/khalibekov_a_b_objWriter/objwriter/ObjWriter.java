package ru.vsu.cs.khalibekov_a_b_objWriter.objwriter;

import ru.vsu.cs.khalibekov_a_b_objWriter.math.Vector2f;
import ru.vsu.cs.khalibekov_a_b_objWriter.math.Vector3f;
import ru.vsu.cs.khalibekov_a_b_objWriter.model.Model;
import ru.vsu.cs.khalibekov_a_b_objWriter.model.Polygon;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ObjWriter {

    public static void write(Model model, String filename) throws IOException {
        if (model == null) {
            throw new IOException("Модель не существует!");
        }

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Экспортировано ObjWriter\n");
            writer.write("# Вершин: " + model.vertices.size() + "\n");
            writer.write("# Текстурных координат: " + model.textureVertices.size() + "\n");
            writer.write("# Нормалей: " + model.normals.size() + "\n");
            writer.write("# Полигонов: " + model.polygons.size() + "\n\n");

            // запись вершин
            for (Vector3f vertex : model.vertices) {
                writer.write(String.format(Locale.US, "v %.6f %.6f %.6f\n", vertex.x, vertex.y, vertex.z));
            }
            if (!model.vertices.isEmpty()) {
                writer.write("\n");
            }

            // запись текстурных координат
            for (Vector2f texCoord : model.textureVertices) {
                writer.write(String.format(Locale.US, "vt %.6f %.6f\n", texCoord.x, texCoord.y));
            }
            if (!model.textureVertices.isEmpty()) {
                writer.write("\n");
            }

            // запись нормалей
            for (Vector3f normal : model.normals) {
                writer.write(String.format(Locale.US, "vn %.6f %.6f %.6f\n", normal.x, normal.y, normal.z));
            }
            if (!model.normals.isEmpty()) {
                writer.write("\n");
            }

            // запись полигонов
            for (int polyIndex = 0; polyIndex < model.polygons.size(); polyIndex++) {
                Polygon poly = model.polygons.get(polyIndex);

                if (poly == null) {
                    throw new IOException("Полигон " + polyIndex + " равен null");
                }

                ArrayList<Integer> vertexIndices = poly.getVertexIndices();
                ArrayList<Integer> textureIndices = poly.getTextureVertexIndices();
                ArrayList<Integer> normalIndices = poly.getNormalIndices();

                if (vertexIndices == null || vertexIndices.isEmpty()) {
                    throw new IOException("Полигон " + polyIndex + " не содержит вершин");
                }

                // проверка согласованности индексов
                checkPolygonConsistency(polyIndex, vertexIndices, textureIndices, normalIndices);

                // проверка границ индексов
                checkIndicesBounds(polyIndex, vertexIndices, textureIndices, normalIndices,
                        model.vertices.size(), model.textureVertices.size(), model.normals.size());

                writer.write("f");

                for (int i = 0; i < vertexIndices.size(); i++) {
                    int vIndex = vertexIndices.get(i) + 1;

                    boolean hasTexture = textureIndices != null && !textureIndices.isEmpty() && i < textureIndices.size();
                    boolean hasNormal = normalIndices != null && !normalIndices.isEmpty() && i < normalIndices.size();

                    writer.write(" " + vIndex);

                    if (hasTexture || hasNormal) {
                        if (hasTexture && hasNormal) {
                            // v/vt/vn
                            writer.write("/");
                            int vtIndex = textureIndices.get(i) + 1;
                            writer.write(String.valueOf(vtIndex));
                            writer.write("/");
                            int vnIndex = normalIndices.get(i) + 1;
                            writer.write(String.valueOf(vnIndex));
                        } else if (hasTexture) {
                            // v/vt
                            writer.write("/");
                            int vtIndex = textureIndices.get(i) + 1;
                            writer.write(String.valueOf(vtIndex));
                        } else if (hasNormal) {
                            // v//vn
                            writer.write("//");
                            int vnIndex = normalIndices.get(i) + 1;
                            writer.write(String.valueOf(vnIndex));
                        }
                    }
                }
                writer.write("\n");
            }
        }
    }

     // Проверка согласованности индексов в полигоне
    private static void checkPolygonConsistency(int polyIndex,
                                                ArrayList<Integer> vertexIndices,
                                                ArrayList<Integer> textureIndices,
                                                ArrayList<Integer> normalIndices) throws IOException {
        int vertexCount = vertexIndices.size();

        if (textureIndices != null && !textureIndices.isEmpty() && textureIndices.size() != vertexCount) {
            throw new IOException("Полигон " + polyIndex + ": количество текстурных индексов (" +
                    textureIndices.size() + ") не совпадает с количеством вершин (" + vertexCount + ")");
        }

        if (normalIndices != null && !normalIndices.isEmpty() && normalIndices.size() != vertexCount) {
            throw new IOException("Полигон " + polyIndex + ": количество нормалей (" +
                    normalIndices.size() + ") не совпадает с количеством вершин (" + vertexCount + ")");
        }
    }

     // проверка границ индексов
    private static void checkIndicesBounds(int polyIndex,
                                           ArrayList<Integer> vertexIndices,
                                           ArrayList<Integer> textureIndices,
                                           ArrayList<Integer> normalIndices,
                                           int maxVertices, int maxTextures, int maxNormals) throws IOException {
        // вершины
        for (int i = 0; i < vertexIndices.size(); i++) {
            int index = vertexIndices.get(i);
            if (index < 0 || index >= maxVertices) {
                throw new IOException("Полигон " + polyIndex + ", вершина " + i +
                        ": индекс " + index + " выходит за границы [0, " + (maxVertices-1) + "]");
            }
        }

        // текст коорд
        if (textureIndices != null && !textureIndices.isEmpty()) {
            for (int i = 0; i < textureIndices.size(); i++) {
                int index = textureIndices.get(i);
                if (index < 0 || index >= maxTextures) {
                    throw new IOException("Полигон " + polyIndex + ", текстура " + i +
                            ": индекс " + index + " выходит за границы [0, " + (maxTextures-1) + "]");
                }
            }
        }

        // нормали
        if (normalIndices != null && !normalIndices.isEmpty()) {
            for (int i = 0; i < normalIndices.size(); i++) {
                int index = normalIndices.get(i);
                if (index < 0 || index >= maxNormals) {
                    throw new IOException("Полигон " + polyIndex + ", нормаль " + i +
                            ": индекс " + index + " выходит за границы [0, " + (maxNormals-1) + "]");
                }
            }
        }
    }
}