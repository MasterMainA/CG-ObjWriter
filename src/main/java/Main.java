import ru.vsu.cs.khalibekov_a_b_objWriter.model.Model;
import ru.vsu.cs.khalibekov_a_b_objWriter.objreader.ObjReader;
import ru.vsu.cs.khalibekov_a_b_objWriter.objwriter.ObjWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        String PATH = "C:\\Dev\\projects java\\Course2\\Semestr1\\CG\\TaskObjWriter\\3DModels\\test.obj";
        Path fileName = Path.of(PATH);
        String fileContent = Files.readString(fileName);

        System.out.println("Loading model ...");
        Model model = ObjReader.read(fileContent);

        System.out.println("Vertices: " + model.vertices.size());
        System.out.println("Texture vertices: " + model.textureVertices.size());
        System.out.println("Normals: " + model.normals.size());
        System.out.println("Polygons: " + model.polygons.size());

        // сохранение модели
        System.out.println("\nSaving model...");
        ObjWriter.saveModel(model, "output_model.obj");
        System.out.println("Model saved as 'output_model.obj'");
    }
}