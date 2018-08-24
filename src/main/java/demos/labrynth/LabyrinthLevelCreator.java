package demos.labrynth;

import com.jogamp.opengl.GL4;
import engine.core.OpenGlObject;
import engine.texture.TextureLoader;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class LabyrinthLevelCreator {
    ArrayList<OpenGlObject> createLevelFromFile(GL4 gl, String filePath) {

        int defaultSize = 25, vertSize = 0, horSize = 0;
        List<String> lines = new ArrayList<>();
        ArrayList<OpenGlObject> outList = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            lines = stream.filter(line -> line.matches("\\[[0-9]+,[0-9]+,[HV],[0-9]+\\];")).
                    collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (String l : lines) {
            l = l.replaceAll("[\\[,\\];]", " ");

            var scanner = new Scanner(l);
            int startX = scanner.nextInt();
            int startY = scanner.nextInt();
            String orientation = scanner.next("[HV]");
            int size = scanner.nextInt();

            if (orientation.equals("H")) {
                vertSize = defaultSize;
                horSize = size;
            } else {
                vertSize = size;
                horSize = defaultSize;
            }

            outList.add(createNewObject(startX, startY, horSize, vertSize, gl));
        }

        return outList;
    }

    private OpenGlObject createNewObject(int startX, int startY, int horSize, int vertSize, GL4 gl){
        var out = new OpenGlObject(2, 6, gl,
                startX, startY, new Dimension(horSize, vertSize), 0) {
            @Override
            public void loadTexture(String filePath) {
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    GameLabyrinth.initRepeatableTexParameters(this.texture, this.gl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        out.initRenderData(new String[]{this.getClass().
                                        getClassLoader().
                                        getResource("textures/labyrinth/abbey_base.jpg").
                                        getPath()},
                    false,
                    new float[]{0f, 1f,
                                1f, 0f,
                                0f, 0f,
                                0f, 1f,
                                1f, 1f,
                                1f, 0f},
                    new float[]{10f, 0f,
                                0f, 10f,
                                10f, 10f,
                                10f, 0f,
                                0f, 0f,
                                0f, 10f});

        return out;
    }
}
