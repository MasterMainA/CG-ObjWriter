package ru.vsu.cs.khalibekov_a_b;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.khalibekov_a_b_objWriter.math.Vector2f;
import ru.vsu.cs.khalibekov_a_b_objWriter.math.Vector3f;
import ru.vsu.cs.khalibekov_a_b_objWriter.model.Model;
import ru.vsu.cs.khalibekov_a_b_objWriter.model.Polygon;
import ru.vsu.cs.khalibekov_a_b_objWriter.objwriter.ObjWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ObjWriterTest {

    @Test
    void testFullPolygon() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.textureVertices.addAll(Arrays.asList(
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(0, 1)
        ));

        model.normals.add(new Vector3f(0, 0, 1));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 0, 0)));
        model.polygons.add(poly);

        ObjWriter.write(model, "test4.obj");
        String content = Files.readString(Path.of("test4.obj"));
        assertTrue(content.contains("f 1/1/1 2/2/1 3/3/1"));
        assertTrue(content.contains("# Экспортировано ObjWriter"));
        assertTrue(content.contains("# Вершин: 3"));
        assertTrue(content.contains("# Текстурных координат: 3"));
        assertTrue(content.contains("# Нормалей: 1"));
        assertTrue(content.contains("# Полигонов: 1"));
    }


    @Test
    void testEmptyModel() throws IOException {
        Model model = new Model();
        ObjWriter.write(model, "test6.obj");
        String content = Files.readString(Path.of("test6.obj"));
        assertTrue(Files.exists(Path.of("test6.obj")));
        assertTrue(content.contains("# Экспортировано ObjWriter"));
        assertTrue(content.contains("# Вершин: 0"));
        assertTrue(content.contains("# Полигонов: 0"));
        assertFalse(content.contains("v "));
        assertFalse(content.contains("f "));
    }

    @Test
    void testNullModel() {
        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(null, "test7.obj");
        });
        assertEquals("Модель не существует!", exception.getMessage());
    }

    @Test
    void testMultiplePolygons() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0)
        ));

        Polygon poly1 = new Polygon();
        poly1.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));

        Polygon poly2 = new Polygon();
        poly2.setVertexIndices(new ArrayList<>(Arrays.asList(1, 3, 2)));

        model.polygons.add(poly1);
        model.polygons.add(poly2);

        ObjWriter.write(model, "test9.obj");
        String content = Files.readString(Path.of("test9.obj"));

        String[] lines = content.split("\n");
        int faceCount = 0;
        for (String line : lines) {
            if (line.startsWith("f ")) {
                faceCount++;
            }
        }
        assertEquals(2, faceCount);
        assertTrue(content.contains("f 1 2 3"));
        assertTrue(content.contains("f 2 4 3"));
    }

    @Test
    void testTextureOnly() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.textureVertices.addAll(Arrays.asList(
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(0, 1)
        ));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(poly);

        ObjWriter.write(model, "test11.obj");
        String content = Files.readString(Path.of("test11.obj"));
        assertTrue(content.contains("f 1/1 2/2 3/3"));
        assertFalse(content.contains("//"));
    }

    @Test
    void testPolygonWithDifferentIndexSizes() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 1, 0)
        ));

        model.textureVertices.addAll(Arrays.asList(
                new Vector2f(0, 0),
                new Vector2f(1, 0)
        ));

        model.normals.add(new Vector3f(0, 0, 1));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2, 3)));
        poly.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 0, 1)));
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 0, 0, 0)));
        model.polygons.add(poly);

        ObjWriter.write(model, "test14.obj");
        String content = Files.readString(Path.of("test14.obj"));
        assertTrue(content.contains("f 1/1/1 2/2/1 3/1/1 4/2/1"));
    }

    @Test
    void testLargeModel() throws IOException {
        Model model = new Model();

        for (int i = 0; i < 100; i++) {
            model.vertices.add(new Vector3f(i, i * 2, i * 3));
        }

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4)));
        model.polygons.add(poly);

        ObjWriter.write(model, "test16.obj");
        String content = Files.readString(Path.of("test16.obj"));

        long vertexCount = content.lines().filter(line -> line.startsWith("v ")).count();
        assertEquals(100, vertexCount);

        assertTrue(content.contains("# Вершин: 100"));
    }

    @Test
    void testInconsistentPolygonThrowsException() {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.normals.addAll(Arrays.asList(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 0, 0)
        ));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2))); // 3 вершины
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 1))); // 2 нормали вместо 3

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_inconsistent.obj");
        });

        assertTrue(exception.getMessage().contains("не совпадает"));
        assertTrue(exception.getMessage().contains("количество нормалей"));
    }

    @Test
    void testPolygonsWithoutVertices() {
        Model model = new Model();

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_polygons_only.obj");
        });

        assertTrue(exception.getMessage().contains("выходит за границы"));
        assertTrue(exception.getMessage().contains("вершина"));
    }

    @Test
    void testInvalidNormalIndex() {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.normals.add(new Vector3f(0, 0, 1));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 5, 2)));

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_invalid_normal.obj");
        });

        assertTrue(exception.getMessage().contains("нормаль"));
        assertTrue(exception.getMessage().contains("выходит за границы"));
    }

    @Test
    void testNullPolygon() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0));

        model.polygons.add(null);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_null_polygon.obj");
        });

        assertTrue(exception.getMessage().contains("равен null"));
    }


    @Test
    void testPolygonWithoutCorrespondingVertices() {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0)
        ));

        Polygon poly = new Polygon();
        // полигон хочет 3 вершины, но есть только 2
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_missing_vertex.obj");
        });

        assertTrue(exception.getMessage().contains("выходит за границы"));
        assertTrue(exception.getMessage().contains("[0, 1]"));
    }

    @Test
    void testWrongTextureCount() {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.textureVertices.addAll(Arrays.asList(
                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(0, 1)
        ));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2))); // 3 вершины
        poly.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 1))); // Только 2 текстуры

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_wrong_texture.obj");
        });

        assertTrue(exception.getMessage().contains("текстурных индексов"));
        assertTrue(exception.getMessage().contains("не совпадает"));
    }

    @Test
    void testMultipleErrors() {
        Model model = new Model();
        model.vertices.add(new Vector3f(0, 0, 0)); // Только 1 вершина

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2))); // индексы 1 и 2 не существуют

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_multiple_errors.obj");
        });

        // ошибка (выход за границы вершин) ловится
        assertTrue(exception.getMessage().contains("выходит за границы"));
    }

    @Test
    void testValidPolygon() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));

        model.polygons.add(poly);

        assertDoesNotThrow(() -> {
            ObjWriter.write(model, "test_valid.obj");
        });

        String content = Files.readString(Path.of("test_valid.obj"));
        assertTrue(content.contains("f 1 2 3"));
    }

    @Test
    void testPolygonWithTwoNormalsOneTexture() {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.textureVertices.add(new Vector2f(0, 0)); // 1 текстура
        model.normals.addAll(Arrays.asList(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0)  // 2 нормали
        ));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0, 0, 0))); // 3 индекса на 1 текстуру
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 1))); // 2 нормали на 3 вершины

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_2normals_1texture.obj");
        });

        assertTrue(exception.getMessage().contains("нормалей") ||
                exception.getMessage().contains("не совпадает"));
    }

    @Test
    void testVerticesOnlyNoPolygons() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0)
        ));

        assertDoesNotThrow(() -> {
            ObjWriter.write(model, "test_vertices_only.obj");
        });

        String content = Files.readString(Path.of("test_vertices_only.obj"));
        assertTrue(content.contains("v 0.000000 0.000000 0.000000"));
        assertTrue(content.contains("v 1.000000 0.000000 0.000000"));
        assertFalse(content.contains("f ")); // Не должно быть полигонов
    }

    @Test
    void testNormalOnly() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.normals.addAll(Arrays.asList(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 0, 0)
        ));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        model.polygons.add(poly);

        ObjWriter.write(model, "test12.obj");
        String content = Files.readString(Path.of("test12.obj"));

        assertTrue(content.contains("//"), "Должен содержать '//' для нормалей без текстур");

        boolean foundFace = false;
        for (String line : content.split("\n")) {
            if (line.trim().startsWith("f")) {
                System.out.println("Найден полигон: " + line);
                // f 1//1 2//2 3//3
                if (line.contains("1//1") && line.contains("2//2") && line.contains("3//3")) {
                    foundFace = true;
                    break;
                }
            }
        }
        assertTrue(foundFace, "Должен быть полигон в формате 'вершина//нормаль'");
    }

    @Test
    void testMissingTextureIndices() throws IOException {
        Model model = new Model();
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        model.textureVertices.add(new Vector2f(0, 0));
        model.normals.add(new Vector3f(0, 0, 1));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 0, 0)));

        model.polygons.add(poly);

        assertDoesNotThrow(() -> {
            ObjWriter.write(model, "test_missing_texture.obj");
        });

        String content = Files.readString(Path.of("test_missing_texture.obj"));

        boolean foundFace = false;
        for (String line : content.split("\n")) {
            if (line.trim().startsWith("f")) {
                System.out.println("Найден полигон: " + line);
                // f 1//1 2//1 3//1
                if (line.contains("//")) {
                    String[] parts = line.split("\\s+");
                    boolean allCorrect = true;
                    for (String part : parts) {
                        if (part.startsWith("f")) continue;
                        if (!part.matches("\\d+//\\d+")) {
                            allCorrect = false;
                            break;
                        }
                    }
                    if (allCorrect) {
                        foundFace = true;
                        break;
                    }
                }
            }
        }
        assertTrue(foundFace, "Полигон должен быть записан без текстур (формат 'v//vn')");
    }

    @Test
    void testPolygonWithMismatchedDataSizes() {
        Model model = new Model();

        // 3 вершины
        model.vertices.addAll(Arrays.asList(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        ));

        // 2 нормали (вместо 3)
        model.normals.addAll(Arrays.asList(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0)
        ));

        // 1 текстура (вместо 3)
        model.textureVertices.add(new Vector2f(0.5f, 0.5f));

        Polygon poly = new Polygon();
        poly.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));
        poly.setNormalIndices(new ArrayList<>(Arrays.asList(0, 1)));
        poly.setTextureVertexIndices(new ArrayList<>(Arrays.asList(0)));

        model.polygons.add(poly);

        Exception exception = assertThrows(IOException.class, () -> {
            ObjWriter.write(model, "test_mismatched.obj");
        });

        assertTrue(exception.getMessage().contains("не совпадает"));

    }

    @org.junit.jupiter.api.AfterEach
    void cleanup() throws IOException {
        for (int i = 1; i <= 19; i++) {
            Files.deleteIfExists(Path.of("test" + i + ".obj"));
        }

        String[] newTests = {
                "test_inconsistent.obj", "test_vertices_only.obj", "test_polygons_only.obj",
                "test_invalid_normal.obj", "test_null_polygon.obj", "test_empty_polygon.obj",
                "test_wrong_texture.obj", "test_multiple_errors.obj", "test_valid.obj",
                "test_2normals_1texture.obj",
                "test12.obj", "test_missing_texture.obj"
        };

        for (String filename : newTests) {
            Files.deleteIfExists(Path.of(filename));
        }
    }
}